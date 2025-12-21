package org.lolli.birgram.presentation.components.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

sealed class RecoveryState {
    object CodeCheck: RecoveryState()
    object NewPassword: RecoveryState()
    object WaitReset: RecoveryState()
}

@Composable
fun PasswordRecovery(){
    var currentState by rememberSaveable { mutableStateOf(RecoveryState.CodeCheck) }
    AnimatedContent(
        targetState = currentState,
        transitionSpec = {
//            if(viewModel.isReverse){
//                viewModel.isReverse = false
//                slideInHorizontally(tween(300,50),{-it}).togetherWith(shrinkHorizontally(tween(300,50)))
//            } else {
                slideInHorizontally(tween(300,50),{it}).togetherWith(shrinkHorizontally(tween(300,50), targetWidth = {-it}))
//            }
        }
    ){
        when(it){
            else -> {

            }
        }
    }
}