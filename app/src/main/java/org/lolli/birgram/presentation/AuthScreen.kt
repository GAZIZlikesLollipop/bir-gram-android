package org.lolli.birgram.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import org.drinkless.tdlib.TdApi
import org.lolli.birgram.R
import org.lolli.birgram.Route
import org.lolli.birgram.data.AuthData
import org.lolli.birgram.presentation.components.auth.BaseAuth
import org.lolli.birgram.presentation.components.auth.BaseTextField

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthScreen(
    viewModel: TGViewModel,
    navController: NavController
){
    val loginState by viewModel.loginState.collectAsState()
    val cnt = stringArrayResource(R.array.login_cnt)
    val apiState by viewModel.apiState.collectAsState()
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(loginState){
        viewModel.addAuthToHistory()
    }
    AnimatedContent(
        targetState = loginState,
        transitionSpec = {
            if(viewModel.isReverse){
                slideInHorizontally(tween(500,50)) { -it }.togetherWith(shrinkHorizontally(tween(500,50), targetWidth = {it}))
            } else {
                slideInHorizontally(tween(500,50)) { it }.togetherWith(shrinkHorizontally(tween(500,50), targetWidth = {-it}))
            }
        }
    ){ authState ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (authState) {
                is TdApi.AuthorizationStateWaitTdlibParameters -> {
                    BackHandler { }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.app_launcher),
                            contentDescription = stringResource(R.string.app_name),
                            modifier = Modifier.size(175.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.app_name),
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = cnt[0],
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                        )
                        Spacer(Modifier.height(60.dp))
                        Button(
                            onClick = {
                                viewModel.nextLoginStep(TdApi.AuthorizationStateWaitTdlibParameters(),AuthData.TDLibParameters)
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                            enabled = apiState is ApiState.Init || apiState is ApiState.Error
                        ) {
                            Text(
                                text = cnt[1],
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                is TdApi.AuthorizationStateWaitOtherDeviceConfirmation -> TODO("ЕЩЕ в разработке")
                is TdApi.AuthorizationStateWaitPhoneNumber -> {
                    BackHandler { }
                    Box(modifier = Modifier.fillMaxSize()) {
                        BaseAuth(
                            title = cnt[3],
                            description = cnt[5],
                            customTextField = null,
                            textFieldValue = phoneNumber,
                            onChange = { phoneNumber = it },
                            labelText = cnt[4],
                            onNext = {
                                focusManager.clearFocus()
                                viewModel.isPhoneNumber = Pair(true,phoneNumber)
                            },
                            isVisible = phoneNumber.length > 9,
                            apiState = apiState,
                            leftContent = {
                                Text(
                                    "+",
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        )
                    }
                }
                is TdApi.AuthorizationStateWaitCode -> {
                    BackHandler { }
                    var code by rememberSaveable { mutableStateOf("") }
                    val typeDescriptions = stringArrayResource(R.array.code_type)

                    val codeType = when (authState.codeInfo.type) {
                        is TdApi.AuthenticationCodeTypeTelegramMessage -> typeDescriptions[0]
                        is TdApi.AuthenticationCodeTypeSms -> typeDescriptions[1]
                        is TdApi.AuthenticationCodeTypeCall -> typeDescriptions[2]
                        is TdApi.AuthenticationCodeTypeFlashCall -> typeDescriptions[3]
                        is TdApi.AuthenticationCodeTypeMissedCall -> typeDescriptions[4]
                        is TdApi.AuthenticationCodeTypeFragment -> typeDescriptions[5]
                        is TdApi.AuthenticationCodeTypeFirebaseAndroid -> typeDescriptions[6]
                        is TdApi.AuthenticationCodeTypeFirebaseIos -> typeDescriptions[7]
                        else -> cnt[8]
                    }

                    var counter by rememberSaveable { mutableStateOf(authState.codeInfo.timeout) }
                    LaunchedEffect(Unit) {
                        while (counter > 0) {
                            delay(1000)
                            counter--
                        }
                    }
                    val nextWaiting = stringArrayResource(R.array.next_type_waiting)
                    val nextReady = stringArrayResource(R.array.next_type_ready)
                    val index = when (authState.codeInfo.nextType) {
                        is TdApi.AuthenticationCodeTypeTelegramMessage -> 0
                        is TdApi.AuthenticationCodeTypeSms -> 1
                        is TdApi.AuthenticationCodeTypeCall -> 2
                        is TdApi.AuthenticationCodeTypeFlashCall -> 3
                        is TdApi.AuthenticationCodeTypeMissedCall -> 4
                        is TdApi.AuthenticationCodeTypeFragment -> 5
                        is TdApi.AuthenticationCodeTypeFirebaseAndroid -> 6
                        is TdApi.AuthenticationCodeTypeFirebaseIos -> 7
                        else -> 8
                    }

                    val nextType = Pair(nextWaiting[index], nextReady[index])

                    val nextString = if (counter > 0) {
                        "${nextType.first} $counter"
                    } else {
                        nextType.second
                    }

                    BaseAuth(
                        title = cnt[11],
                        description = codeType,
                        customTextField = null,
                        textFieldValue = code,
                        onChange = { code = it },
                        labelText = cnt[12],
                        onNext = {
                            focusManager.clearFocus()
                            viewModel.nextLoginStep(
                                TdApi.AuthorizationStateWaitCode(),
                                AuthData.Code(code)
                            )
                        },
                        isVisible = code.length > 3,
                        apiState = apiState,
                        callBack = if(counter < 1 && authState.codeInfo.nextType != null){
                            {
                                code = ""
                                focusManager.clearFocus()
                                viewModel.resendCode()
                            }
                        } else null,
                        callColor = if (counter < 1 && authState.codeInfo.nextType != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(0.5f),
                        callText = nextString
                    )
                }
                is TdApi.AuthorizationStateWaitPassword -> {
                    BackHandler { }
                    var textFieldState by rememberSaveable { mutableStateOf("") }
                    BaseAuth(
                        title = cnt[13],
                        description = cnt[14],
                        customTextField = {
                            OutlinedTextField(
                                value = textFieldState,
                                onValueChange = {textFieldState = it},
                                singleLine = true,
                                label = {
                                    Text(
                                        cnt[15],
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                enabled = apiState is ApiState.Init || apiState is ApiState.Error,
                                isError = apiState is ApiState.Error,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(horizontal = 8.dp),
                                textStyle = MaterialTheme.typography.labelMedium,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Password,
                                    autoCorrectEnabled = false,
                                ),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        viewModel.nextLoginStep(
                                            TdApi.AuthorizationStateWaitPassword(),
                                            AuthData.Password(textFieldState)
                                        )
                                    }
                                ),
                                placeholder = {
                                    Text(
                                        authState.passwordHint ?: "Password"
                                    )
                                },
                                trailingIcon = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AnimatedVisibility(
                                            visible = textFieldState.length > 5 && apiState !is ApiState.Loading,
                                            enter = slideInVertically(tween(350, 50)) { it } +
                                                    expandVertically(
                                                        tween(250, 50),
                                                        initialHeight = { it }),
                                            exit = slideOutVertically(tween(350, 0)) { it } +
                                                    shrinkVertically(
                                                        tween(250, 0),
                                                        targetHeight = { it })
                                        ) {
                                            VerticalDivider(
                                                thickness = 2.dp,
                                                modifier = Modifier.height(56.dp),
                                                color = if (apiState is ApiState.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        AnimatedVisibility(
                                            visible = textFieldState.length > 5 && apiState !is ApiState.Loading,
                                            enter = slideInHorizontally(
                                                spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMediumLow
                                                )
                                            ) { it } + expandHorizontally(
                                                spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMediumLow
                                                ), initialWidth = { it }),
                                            exit = slideOutHorizontally(
                                                tween(
                                                    500,
                                                    50,
                                                    LinearOutSlowInEasing
                                                ),
                                            ) { it } + shrinkHorizontally(
                                                tween(
                                                    500,
                                                    50,
                                                    LinearOutSlowInEasing
                                                ), targetWidth = { it })
                                        ) {
                                            if (apiState is ApiState.Loading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(24.dp),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    strokeWidth = 3.dp
                                                )
                                            } else {
                                                IconButton(
                                                    onClick = {
                                                        viewModel.nextLoginStep(
                                                            TdApi.AuthorizationStateWaitPassword(),
                                                            AuthData.Password(textFieldState)
                                                        )
                                                    },
                                                ) {
                                                    Icon(
                                                        imageVector = if (apiState is ApiState.Error) ImageVector.vectorResource(
                                                            R.drawable.replay_24px
                                                        ) else ImageVector.vectorResource(R.drawable.arrow_back),
                                                        contentDescription = "",
                                                        modifier = Modifier.size(26.dp)
                                                            .graphicsLayer { scaleX = -1f },
                                                        tint = if (apiState is ApiState.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                },
                            )
                        },
                        callBack = {
                            textFieldState = ""
                            focusManager.clearFocus()
                            if((loginState as TdApi.AuthorizationStateWaitPassword).hasRecoveryEmailAddress) {
                                viewModel.passwordRecovery(onOk = { navController.navigate(Route.PasswordRecovery.route) })
                            } else {
                                viewModel.isAccountDelete = true
                            }
                        },
                        callText = cnt[16],
                        apiState = apiState
                    )
                }
                is TdApi.AuthorizationStateWaitEmailAddress -> {
                    BackHandler { }
                    var email by rememberSaveable { mutableStateOf("") }
                    BaseAuth(
                        title = cnt[26],
                        description = cnt[27],
                        customTextField = null,
                        textFieldValue = email,
                        onChange = {email = it},
                        labelText = cnt[28],
                        onNext = {
                            focusManager.clearFocus()
                            viewModel.nextLoginStep(
                                TdApi.AuthorizationStateWaitEmailAddress(),
                                AuthData.Email(email)
                            )
                        },
                        isVisible = email.isNotBlank(),
                        apiState = apiState
                    )
                }
                is TdApi.AuthorizationStateWaitEmailCode -> {
                    BackHandler { }
                    var code by rememberSaveable { mutableStateOf("") }
                    BaseAuth(
                        title = cnt[29],
                        description = "${cnt[30]} ${authState.codeInfo?.emailAddressPattern}",
                        textFieldValue = code,
                        onChange = {code = it},
                        labelText = cnt[31],
                        onNext = {
                            focusManager.clearFocus()
                            viewModel.nextLoginStep(
                                TdApi.AuthorizationStateWaitEmailCode(),
                                AuthData.EmailCode(code)
                            )
                        },
                        isVisible = code.length >= (authState.codeInfo?.length ?: 3),
                        callBack = {
                            when(authState.emailAddressResetState) {
                                is TdApi.EmailAddressResetStatePending -> viewModel.isPendingRecovery = authState.emailAddressResetState as TdApi.EmailAddressResetStatePending
                                is TdApi.EmailAddressResetStateAvailable -> viewModel.isAvailableRecovery = authState.emailAddressResetState as TdApi.EmailAddressResetStateAvailable
                                else -> viewModel.isAccountDelete = true
                            }
                        },
                        callText = if(authState.emailAddressResetState is TdApi.EmailAddressResetStatePending) cnt[41] else cnt[32],
                        apiState = apiState,
                        custom = {
                            if(authState.emailAddressResetState is TdApi.EmailAddressResetStatePending){
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "${cnt[37]} ${(authState.emailAddressResetState as TdApi.EmailAddressResetStatePending).resetIn} ${cnt[38]}",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    )
                }
                is TdApi.AuthorizationStateWaitRegistration -> {
                    BackHandler { }
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        AsyncImage(
                            model = ImageVector.vectorResource(R.drawable.persons),
                            contentDescription = null,
                            modifier = Modifier.clickable{}.size(100.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = cnt[42],
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = cnt[43],
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                            color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BaseTextField(
                                value = viewModel.firstName,
                                onChange = {viewModel.firstName = it},
                                label = cnt[44],
                                apiState = apiState
                            )
                            Spacer(Modifier.width(12.dp))
                            BaseTextField(
                                value = viewModel.lastName,
                                onChange = {viewModel.lastName = it},
                                label = cnt[45],
                                apiState = apiState
                            )
                        }
                        if(apiState is ApiState.Error){
                            Spacer(Modifier.height(8.dp))
                        }
                        AnimatedVisibility(
                            visible = apiState is ApiState.Error,
                            enter = expandVertically(spring(Spring.DampingRatioHighBouncy,Spring.StiffnessMediumLow)),
                            exit = shrinkHorizontally()
                        ) {
                            if(apiState is ApiState.Error){
                                Text(
                                    text = (apiState as ApiState.Error).message,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                is TdApi.AuthorizationStateReady -> {
                    BackHandler { }
                    navController.navigate(Route.Chats.route)
                }
                is TdApi.AuthorizationStateLoggingOut -> TODO()
                is TdApi.AuthorizationStateClosing -> TODO()
                is TdApi.AuthorizationStateClosed -> TODO()
                else -> {
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)){ Text("") }
                }
            }
        }
    }
    viewModel.isReverse = false
}