package org.lolli.birgram.presentation.components.auth

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.lolli.birgram.R
import org.lolli.birgram.presentation.ApiState

@Composable
fun BaseTextField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    apiState: ApiState
){
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = value,
        onValueChange = { onChange(it) },
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        enabled = apiState !is ApiState.Loading,
        isError = apiState is ApiState.Error,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(horizontal = 6.dp),
        textStyle = MaterialTheme.typography.labelMedium,
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
    )
}
@Composable
fun BaseAuth(
    title: String,
    description: String,
    customTextField: @Composable (() -> Unit)? = null,
    textFieldValue: String = "",
    onChange: (String) -> Unit = {},
    labelText: String = "",
    onNext: () -> Unit = {},
    isVisible: Boolean = false,
    leftContent: @Composable (() -> Unit)? = null,
    callBack: (() -> Unit)? = null,
    callText: String = "",
    callColor: Color = MaterialTheme.colorScheme.primary,
    apiState: ApiState,
    custom: @Composable (() -> Unit)? = null
){
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = description,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
            color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
        )
        Spacer(Modifier.height(16.dp))
        if (customTextField == null) {
            if (leftContent != null) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { onChange(it) },
                    label = { Text(labelText, style = MaterialTheme.typography.labelMedium) },
                    enabled = apiState !is ApiState.Loading,
                    isError = apiState is ApiState.Error,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(horizontal = 6.dp).focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.labelMedium,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone,
                    ),
                    keyboardActions = KeyboardActions(onDone = { onNext() }),
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if(apiState !is ApiState.Loading){
                                AnimatedVisibility(
                                    visible = isVisible,
                                    enter = slideInVertically(tween(350, 50), { it }) +
                                            expandVertically(
                                                tween(250, 50),
                                                initialHeight = { it }),
                                    exit = slideOutVertically(tween(350, 0), { it }) +
                                            shrinkVertically(
                                                tween(250, 0),
                                                targetHeight = { it })
                                ) {
                                    VerticalDivider(
                                        thickness = 2.dp,
                                        modifier = Modifier.height(56.dp),
                                        color = if(apiState is ApiState.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInHorizontally(
                                    spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    ), { it }) + expandHorizontally(
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
                                    { it },
                                ) + shrinkHorizontally(
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
                                        onClick = { onNext() },
                                    ) {
                                        Icon(
                                            imageVector = if (apiState is ApiState.Error) ImageVector.vectorResource(
                                                R.drawable.replay_24px
                                            ) else ImageVector.vectorResource(
                                                R.drawable.arrow_back
                                            ),
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
                    leadingIcon = {
                        leftContent()
                    }
                )
            } else {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { onChange(it) },
                    label = { Text(labelText, style = MaterialTheme.typography.labelMedium) },
                    enabled = apiState !is ApiState.Loading,
                    isError = apiState is ApiState.Error,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(horizontal = 6.dp),
                    textStyle = MaterialTheme.typography.labelMedium,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone,
                    ),
                    keyboardActions = KeyboardActions(onDone = { onNext() }),
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnimatedVisibility(
                                visible = isVisible && apiState !is ApiState.Loading,
                                enter = slideInVertically(tween(350, 50), { it }) +
                                        expandVertically(
                                            tween(250, 50),
                                            initialHeight = { it }),
                                exit = slideOutVertically(tween(350, 0), { it }) +
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
                                visible = isVisible,
                                enter = slideInHorizontally(
                                    spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    ), { it }) + expandHorizontally(
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
                                    { it },
                                ) + shrinkHorizontally(
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
                                        onClick = { onNext() },
                                    ) {
                                        Icon(
                                            imageVector = if (apiState is ApiState.Error) ImageVector.vectorResource(
                                                R.drawable.replay_24px
                                            ) else ImageVector.vectorResource(
                                                R.drawable.arrow_back
                                            ),
                                            contentDescription = "",
                                            modifier = Modifier.size(26.dp)
                                                .graphicsLayer { scaleX = -1f },
                                            tint = if (apiState is ApiState.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }
        } else {
            customTextField()
        }
        if (callBack != null) {
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = { callBack.invoke() },
                enabled = apiState !is ApiState.Loading
            ) {
                Text(
                    text = callText,
                    color = callColor,
                    style = MaterialTheme.typography.labelMedium
                )
            }
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
                    text = apiState.message,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        custom?.invoke()
    }
    DisposableEffect(Unit){
        focusRequester.requestFocus()
        onDispose {}
    }
}