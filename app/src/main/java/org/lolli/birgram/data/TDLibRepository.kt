package org.lolli.birgram.data

import android.content.Context
import android.os.Build
import android.util.Log
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import org.lolli.birgram.BuildConfig
import java.util.Locale

sealed class AuthData {
//    data class PremiumPurchase(
//        val productId: String,
//        val premiumSupport: String,
//        val email: String
//    ): AuthData()

    object TDLibParameters : AuthData()
//    data class OtherDeviceConfirmation(val link: String) : AuthData()
    data class PhoneNumber(val phoneNumber: String) : AuthData()
    data class Code(val code: String) : AuthData()
    data class Password(val password: String) : AuthData()
    data class Email(val email: String) : AuthData()
    data class EmailCode(val emailCode: String) : AuthData()
    data class Registration(
        val firstName: String,
        val lastName: String,
        val disableNotification: Boolean
    ) : AuthData()
}

interface TdLibClient {
    fun initClient(
        onError: (String) -> Unit,
        onAuth: (state: TdApi.AuthorizationState) -> Unit,
        onChats: (
            position: TdApi.UpdateChatPosition?,
            chat: TdApi.Chat?,
            lastMessage: TdApi.UpdateChatLastMessage?,
            outbox: TdApi.UpdateChatReadOutbox?,
            photo: TdApi.UpdateChatPhoto?,
            title: TdApi.UpdateChatTitle?,
            mentionCount: TdApi.UpdateChatUnreadMentionCount?,
            reactionCount: TdApi.UpdateChatUnreadReactionCount?,
            unreadCount: TdApi.UpdateChatReadInbox?
        ) -> Unit,
        onFile: (file: TdApi.File) -> Unit,
        onChatFolders: (folders: TdApi.UpdateChatFolders) -> Unit,
        onUnreadChatCount: (unreadCount: TdApi.UpdateUnreadChatCount) -> Unit
    )

    fun updateAuthState(
        state: TdApi.AuthorizationState,
        data: AuthData,
        onError: (String) -> Unit
    )

    fun resendCode(
        isUser: Boolean,
        onError: (String) -> Unit
    )

    fun deleteAccount(
        password: String? = null,
        onError: (String) -> Unit
    )

    fun resetEmail(onError: (String) -> Unit)

    fun accountPasswordRecovery(
        code: String? = null,
        onError: (String) -> Unit,
        onOk: () -> Unit
    )

    fun setPassword(
        oPassword: String,
        password: String,
        hint: String,
        isSetEmail: Boolean = false,
        newEmail: String? = null,
        onError: (String) -> Unit
    )

    fun loadChats(
        type: TdApi.ChatList,
        limit: Int,
        onError: (String) -> Unit
    )

    fun downloadFile(
        fileId: Int,
        priority: Int,
        offset: Long,
        limit: Long,
        synchronous: Boolean,
        onError: (String) -> Unit
    )

    fun sendMessage()

    fun getChatMessages()
}

class TDLibRepository(val context: Context) : TdLibClient {

    private lateinit var client: Client

    override fun initClient(
        onError: (String) -> Unit,
        onAuth: (state: TdApi.AuthorizationState) -> Unit,
        onChats: (
            position: TdApi.UpdateChatPosition?,
            chat: TdApi.Chat?,
            lastMessage: TdApi.UpdateChatLastMessage?,
            outbox: TdApi.UpdateChatReadOutbox?,
            photo: TdApi.UpdateChatPhoto?,
            title: TdApi.UpdateChatTitle?,
            mentionCount: TdApi.UpdateChatUnreadMentionCount?,
            reactionCount: TdApi.UpdateChatUnreadReactionCount?,
            unreadCount: TdApi.UpdateChatReadInbox?
        ) -> Unit,
        onFile: (file: TdApi.File) -> Unit,
        onChatFolders: (folders: TdApi.UpdateChatFolders) -> Unit,
        onUnreadChatCount: (unreadCount: TdApi.UpdateUnreadChatCount) -> Unit
    ) {
        client = Client.create(
            { authState ->
                when (authState) {
                    is TdApi.Error -> {
                        Log.e("TDLib", "${authState.code}: ${authState.message}")
                        onError(authState.message)
                    }
                    is TdApi.UpdateAuthorizationState -> {
                        onAuth(authState.authorizationState)
                    }
                    is TdApi.UpdateFile -> {
                        onFile(authState.file)
                    }
                    is TdApi.UpdateChatPosition -> {
                        onChats(authState,null,null,null,null,null,null,null,null)
                    }
                    is TdApi.UpdateNewChat -> {
                        onChats(null,authState.chat,null,null,null,null,null,null,null)
                    }
                    is TdApi.UpdateChatLastMessage -> {
                        onChats(null,null,authState,null,null,null,null,null,null)
                    }
                    is TdApi.UpdateChatReadOutbox -> {
                        onChats(null,null,null,authState,null,null,null,null,null)
                    }
                    is TdApi.UpdateChatPhoto -> {
                        onChats(null,null,null,null,authState,null,null,null,null)
                    }
                    is TdApi.UpdateChatTitle -> {
                        onChats(null,null,null,null,null,authState,null,null,null)
                    }
                    is TdApi.UpdateChatUnreadMentionCount -> {
                        onChats(null,null,null,null,null,null,authState,null,null)
                    }
                    is TdApi.UpdateChatUnreadReactionCount -> {
                        onChats(null,null,null,null,null,null,null,authState,null)
                    }
                    is TdApi.UpdateChatReadInbox -> {
                        onChats(null,null,null,null,null,null,null,null,authState)
                    }
                    is TdApi.UpdateChatFolders -> {
                        onChatFolders(authState)
                    }
                    is TdApi.UpdateUnreadChatCount -> {
                        onUnreadChatCount(authState)
                    }
                }
            },
            {
                val message = it.localizedMessage ?: it.message ?: "unknown update handler exception"
                Log.e("TDLib", message)
                onError(message)
            },
            null
        )
    }

    private fun sendRequest(
        query: TdApi.Function<*>,
        onError: (String) -> Unit,
        onOk: () -> Unit = {}
    ) {
        client.send(
            query,
            {
                when (it.javaClass) {
                    TdApi.Error::class.java -> {
                        val err = it as TdApi.Error
                        Log.e("TDLib", "${err.code}, ${err.message}")
                        onError(err.message)
                    }
                    TdApi.Ok::class.java -> onOk()
                }
            },
            {
                val message = it.localizedMessage ?: it.message ?: "unknown request exception"
                Log.e("TDLib", message)
                onError(message)
            }
        )
    }

    override fun setPassword(
        oPassword: String,
        password: String,
        hint: String,
        isSetEmail: Boolean,
        newEmail: String?,
        onError: (String) -> Unit
    ) {
        val params = TdApi.SetPassword().apply {
            oldPassword = oPassword
            newPassword = password
            newHint = hint
            setRecoveryEmailAddress = isSetEmail
            newRecoveryEmailAddress = newEmail
        }
        sendRequest(params, onError)
    }

    override fun accountPasswordRecovery(
        code: String?,
        onError: (String) -> Unit,
        onOk: () -> Unit
    ) {
        val parameters = if (code == null) {
            TdApi.RequestAuthenticationPasswordRecovery()
        } else {
            TdApi.CheckAuthenticationPasswordRecoveryCode().apply { recoveryCode = code }
        }
        sendRequest(parameters, onError, onOk)
    }

    override fun resendCode(isUser: Boolean, onError: (String) -> Unit) {
        val parameters = TdApi.ResendAuthenticationCode().apply {
            reason = if (isUser) TdApi.ResendCodeReasonUserRequest()
            else TdApi.ResendCodeReasonVerificationFailed()
        }
        sendRequest(parameters, onError)
    }

    override fun resetEmail(onError: (String) -> Unit) {
        val request = TdApi.ResetAuthenticationEmailAddress()
        sendRequest(request, onError)
    }

    override fun deleteAccount(password: String?, onError: (String) -> Unit) {
        val params = TdApi.DeleteAccount().apply { if (password != null) this.password = password }
        sendRequest(params, onError)
    }

    override fun updateAuthState(
        state: TdApi.AuthorizationState,
        data: AuthData,
        onError: (String) -> Unit
    ) {
        when (state.javaClass) {
            TdApi.AuthorizationStateWaitTdlibParameters::class.java -> {
                val filesDir = context.getExternalFilesDir("tdlib")
                val parameters = TdApi.SetTdlibParameters().apply {
                    apiId = BuildConfig.API_ID.toInt()
                    apiHash = BuildConfig.API_HASH
                    filesDirectory = filesDir?.absolutePath + "_files"
                    databaseDirectory = filesDir?.absolutePath
                    useMessageDatabase = true
                    useFileDatabase = true
                    useChatInfoDatabase = true
                    useSecretChats = false
                    systemLanguageCode = "${Locale.getDefault().language}-${Locale.getDefault().country}"
                    deviceModel = Build.MODEL
                    applicationVersion = "0.1"
                }
                sendRequest(parameters, onError)
            }

            TdApi.AuthorizationStateWaitOtherDeviceConfirmation::class.java -> TODO("other device state в разработке")

            TdApi.AuthorizationStateWaitPhoneNumber::class.java -> {
                val phoneNumber = TdApi.SetAuthenticationPhoneNumber().apply {
                    phoneNumber = (data as? AuthData.PhoneNumber)?.phoneNumber ?: ""
                    settings = null
                }
                sendRequest(phoneNumber, onError)
            }

            TdApi.AuthorizationStateWaitCode::class.java -> {
                val code = TdApi.CheckAuthenticationCode().apply {
                    this.code = (data as? AuthData.Code)?.code ?: ""
                }
                sendRequest(code, onError)
            }

            TdApi.AuthorizationStateWaitEmailAddress::class.java -> {
                val parameters = TdApi.SetAuthenticationEmailAddress().apply {
                    this.emailAddress = (data as? AuthData.Email)?.email ?: ""
                }
                sendRequest(parameters, onError)
            }

            TdApi.AuthorizationStateWaitPassword::class.java -> {
                val parameters = TdApi.CheckAuthenticationPassword().apply {
                    this.password = (data as? AuthData.Password)?.password ?: ""
                }
                sendRequest(parameters, onError)
            }

            TdApi.AuthorizationStateWaitEmailCode::class.java -> {
                val parameters = TdApi.CheckAuthenticationCode().apply {
                    this.code = (data as? AuthData.EmailCode)?.emailCode ?: ""
                }
                sendRequest(parameters, onError)
            }

            TdApi.AuthorizationStateWaitRegistration::class.java -> {
                val user = TdApi.RegisterUser().apply {
                    this.firstName = (data as? AuthData.Registration)?.firstName ?: ""
                    this.lastName = (data as? AuthData.Registration)?.lastName ?: ""
                    this.disableNotification = (data as? AuthData.Registration)?.disableNotification ?: false
                }
                sendRequest(user, onError)
            }

            TdApi.AuthorizationStateReady::class.java -> {}
            TdApi.AuthorizationStateLoggingOut::class.java -> TODO("обработчик выхода еще в разработке")
            TdApi.AuthorizationStateClosed::class.java -> TODO("обработчик закрытия еще в разработке")
            TdApi.AuthorizationStateClosing::class.java -> TODO("обработчик закрытия еще в разработке")
        }
    }

    override fun loadChats(
        type: TdApi.ChatList,
        limit: Int,
        onError: (String) -> Unit
    ) {
        val query = TdApi.LoadChats().apply {
            this.limit = limit
            this.chatList = type
        }
        sendRequest(query, onError)
    }

    override fun downloadFile(
        fileId: Int,
        priority: Int,
        offset: Long,
        limit: Long,
        synchronous: Boolean,
        onError: (String) -> Unit
    ) {
        val query = TdApi.DownloadFile().apply {
            this.fileId = fileId
            this.priority = priority
            this.offset = offset
            this.limit = limit
            this.synchronous = synchronous
        }
        sendRequest(query,onError)
    }

    override fun sendMessage() {
        TODO("Еще в разработке")
    }

    override fun getChatMessages() {
        TODO("Not yet implemented")
    }
}
