package org.lolli.birgram

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.lolli.birgram.presentation.AuthScreen
import org.lolli.birgram.presentation.ChatsScreen
import org.lolli.birgram.presentation.TGViewModel
import org.lolli.birgram.presentation.components.auth.PasswordRecovery

@Composable
fun Navigation(
    navController: NavHostController,
    tgViewModel: TGViewModel,
    padding: PaddingValues
){
    NavHost(
        navController = navController,
        startDestination = Route.Auth.route
    ){
        composable(
            route = Route.PasswordRecovery.route,
            enterTransition = { slideInHorizontally{-it} }
        ) {
            PasswordRecovery()
        }
        composable(
            route = Route.Auth.route
        ){
            AuthScreen(tgViewModel,navController)
        }
        composable(
            route = Route.Chats.route
        ) {
            ChatsScreen(tgViewModel,padding)
        }
    }
}

sealed class Route(val route: String) {
    object Auth: Route("login")
    object Chats: Route("chats")
    object PasswordRecovery: Route("passwordRecovery")
}