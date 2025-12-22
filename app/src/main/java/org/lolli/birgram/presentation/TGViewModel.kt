package org.lolli.birgram.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.drinkless.tdlib.TdApi
import org.lolli.birgram.Route
import org.lolli.birgram.data.AuthData
import org.lolli.birgram.data.TDLibRepository
import org.lolli.birgram.data.UserPreferencesRepository

sealed class ApiState {
    object Init: ApiState()
    object Loading: ApiState()
    data class Error(val message: String): ApiState()
}

sealed class DownloadType {
    data class ChatPhoto(val chatKey: Long): DownloadType()
}

class TGViewModel(
    val userPreferencesRepository: UserPreferencesRepository,
    val tdLibRepository: TDLibRepository
): ViewModel() {
    var isPhoneNumber by mutableStateOf(Pair(false,""))
    var isReverse by mutableStateOf(false)
    var isPendingRecovery by mutableStateOf<TdApi.EmailAddressResetStatePending?>(null)
    var isAvailableRecovery by mutableStateOf<TdApi.EmailAddressResetStateAvailable?>(null)
    var isAccountDelete by mutableStateOf(false)
    var isTerms by mutableStateOf(false)
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    private val _loginState = MutableStateFlow<TdApi.AuthorizationState>(TdApi.AuthorizationStateWaitTdlibParameters())
    val loginState = _loginState.asStateFlow()

    val initialRoute = userPreferencesRepository.initialRoute.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        Route.Default.route
    )

    private val _authHistory = MutableStateFlow<List<TdApi.AuthorizationState>>(emptyList())
    val authHistory = _authHistory.asStateFlow()

    private val _apiState = MutableStateFlow<ApiState>(ApiState.Init)
    val apiState = _apiState.asStateFlow()

    private val downloadType = MutableStateFlow<DownloadType>(DownloadType.ChatPhoto(0))

    private val _mainChats = MutableStateFlow<Map<Long, TdApi.Chat>>(emptyMap())
    val mainChats = _mainChats.asStateFlow()

//    val cacheMessages = mutableStateMapOf<Long,TdApi.Message?>()
//    val cacheChats = mutableStateMapOf<Long,TdApi.Chat>()
    val cacheMessages = mutableMapOf<Long,TdApi.Message?>()
    val cacheChats = mutableMapOf<Long,TdApi.Chat>()

    init {
        tdLibRepository.initClient(
            {
                _apiState.value = ApiState.Error(it)
            },
            {
                viewModelScope.launch {
                    if(it is TdApi.AuthorizationStateReady) {
                        userPreferencesRepository.updateInitialRoute(Route.Chats.route)
                    } else {
                        userPreferencesRepository.updateInitialRoute(Route.Auth.route)
                    }
                    _loginState.value = it
                    _apiState.value = ApiState.Init
                }
            },
            { position,chat,lastMessage ->
                viewModelScope.launch {
                    when {
                        chat != null -> {
                            cacheChats[chat.id] = chat
                        }
                        lastMessage != null -> {
                            cacheMessages[lastMessage.chatId] = lastMessage.lastMessage
                        }
                        position != null -> {
                            if(position.position.list is TdApi.ChatListMain) {
                                if(cacheChats.containsKey(position.chatId)){
                                    val chat = cacheChats[position.chatId]!!
                                    _mainChats.update { map ->
                                        map + (position.position.order to chat)
                                    }
                                    cacheChats.remove(position.chatId)
                                    if(chat.photo?.small?.local?.canBeDownloaded == true){
                                        downloadChatPhoto(
                                            chat.photo?.small?.id ?: 0,
                                            chat.photo?.small?.local?.downloadOffset ?: 0,
                                            position.position.order
                                        )
                                    }
                                } else {
                                    val chat = TdApi.Chat().apply {
                                        title = ""
                                        positions = arrayOf(position.position)
                                    }
                                    _mainChats.update { map ->
                                        map + (position.position.order to chat)
                                    }
                                }
                                if(cacheMessages.containsKey(position.chatId)){
                                    mainChats.value.toList().find { it.second.id == position.chatId }?.let { mainChat ->
                                        val chat = mainChat.second.apply {
                                            this.lastMessage = cacheMessages[position.chatId]
                                        }
                                        _mainChats.update { map ->
                                            map + (position.position.order to chat)
                                        }
                                        cacheMessages.remove(position.chatId)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            { file ->
                when(val type = downloadType.value){
                    is DownloadType.ChatPhoto -> {
                        if(mainChats.value.containsKey(type.chatKey)){
                            _mainChats.update {
                                it + (type.chatKey to mainChats.value[type.chatKey]!!.apply { photo.apply { this?.small = file } })
                            }
                        }
                    }
                }
            }
        )
    }

    fun downloadChatPhoto(
        fileId: Int,
        offset: Long,
        chatKey: Long
    ){
        _apiState.value = ApiState.Loading
        downloadType.value = DownloadType.ChatPhoto(chatKey)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                tdLibRepository.downloadFile(fileId, 32,offset,0,true){_apiState.value = ApiState.Error(it)}
            } catch(it: Exception){
                val message = it.message ?: "unknown client exception"
                _apiState.value = ApiState.Error(message)
                Log.e("TDLib",message)
            }
        }
    }

    fun loadChats(
        type: TdApi.ChatList = TdApi.ChatListMain(),
        limit: Int = 100
    ){
        _apiState.value = ApiState.Loading
        viewModelScope.launch {
            try {
                tdLibRepository.loadChats(type,limit){_apiState.value = ApiState.Error(it)}
            } catch(it: Exception){
                val message = it.message ?: "unknown client exception"
                _apiState.value = ApiState.Error(message)
                Log.e("TDLib",message)
            }
        }
    }

    fun resendCode(isUser: Boolean = true){
        _apiState.value = ApiState.Loading
        viewModelScope.launch {
            try {
                tdLibRepository.resendCode(isUser) { _apiState.value = ApiState.Error(it) }
            } catch(it: Exception){
                val message = it.message ?: "unknown client exception"
                _apiState.value = ApiState.Error(message)
                Log.e("TDLib",message)
            }
        }
    }
    fun setPassword(
        oPassword: String,
        password: String,
        hint: String,
        isSetEmail: Boolean,
        newEmail: String?
    ){
        _apiState.value = ApiState.Loading
        viewModelScope.launch {
            try {
                tdLibRepository.setPassword(oPassword,password,hint,isSetEmail,newEmail){_apiState.value = ApiState.Error(it)}
            } catch(e: Exception){
                val message = e.message ?: "unknown client exception"
                _apiState.value = ApiState.Error(message)
                Log.e("TDLib",message)
            }
        }
    }
    fun resetEmail(){
        _apiState.value = ApiState.Loading
        viewModelScope.launch {
            try {
                tdLibRepository.resetEmail({_apiState.value = ApiState.Error(it)})
            } catch(e: Exception){
                val message = e.message ?: "unknown client exception"
                _apiState.value = ApiState.Error(message)
                Log.e("TDLib",message)
            }
        }
    }
    fun deleteAccount(password: String? = null){
        _apiState.value = ApiState.Loading
        viewModelScope.launch {
            try {
                tdLibRepository.deleteAccount(password,{_apiState.value = ApiState.Error(it)})
            } catch(e: Exception){
                val message = e.message ?: "unknown client exception"
                _apiState.value = ApiState.Error(message)
                Log.e("TDLib",message)
            }
        }
    }
    fun passwordRecovery(
        code: String? = null,
        onOk: () -> Unit = {}
    ){
        _apiState.value = ApiState.Loading
        viewModelScope.launch {
            try {
                previousAuthHistory()
                tdLibRepository.accountPasswordRecovery(code,{_apiState.value = ApiState.Error(it)},{onOk()})
            } catch(e: Exception){
                val message = e.message ?: "unknown client exception"
                _apiState.value = ApiState.Error(message)
                Log.e("TDLib",message)
            }
        }
    }
    fun nextLoginStep(
        state: TdApi.AuthorizationState,
        data: AuthData
    ){
        _apiState.value = ApiState.Loading
        viewModelScope.launch {
            try {
                tdLibRepository.updateAuthState(state,data) { _apiState.value = ApiState.Error(it) }
            } catch(e: Exception){
                val message = e.message ?: "unknown client exception"
                _apiState.value = ApiState.Error(message)
                Log.e("TDLib",message)
            }
        }
    }

    fun addAuthToHistory(){
        viewModelScope.launch {
            val list = authHistory.value + loginState.value
            _authHistory.value = list
        }
    }

    fun previousAuthHistory(){
        isReverse = true
        _apiState.value = ApiState.Init
        viewModelScope.launch {
            val list = authHistory.value.toMutableList().apply { removeAt(lastIndex) }
            when(list.last()) {
                is TdApi.AuthorizationStateWaitCode -> list[list.lastIndex] = TdApi.AuthorizationStateWaitPhoneNumber()
                is TdApi.AuthorizationStateWaitEmailCode -> list[list.lastIndex] = TdApi.AuthorizationStateWaitPhoneNumber()
                else -> {}
            }
            _loginState.value = list.last()
            _authHistory.value = list
        }
    }
}

class TGViewModelFactory(
    val userPreferencesRepository: UserPreferencesRepository,
    val tdLibRepository: TDLibRepository
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TGViewModel(userPreferencesRepository,tdLibRepository) as T
    }
}