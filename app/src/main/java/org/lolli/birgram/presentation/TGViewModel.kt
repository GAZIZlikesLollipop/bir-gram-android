package org.lolli.birgram.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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
        Route.Auth.route
    )

    private val _authHistory = MutableStateFlow<List<TdApi.AuthorizationState>>(emptyList())
    val authHistory = _authHistory.asStateFlow()

    private val _apiState = MutableStateFlow<ApiState>(ApiState.Init)
    val apiState = _apiState.asStateFlow()

//    private val _chats = MutableStateFlow<List<TdApi.Chat>>(emptyList())
//    val chat = _chats.asStateFlow()

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
            }
        )
    }

    fun resendCode(isUser: Boolean = true){
        _apiState.value = ApiState.Loading
        viewModelScope.launch {
            try {
                tdLibRepository.resendCode(isUser) { _apiState.value = ApiState.Error(it) }
            } catch(it: Exception){
                val message = it.message ?: "unknown client exception"
                _apiState.value = ApiState.Error(message)
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