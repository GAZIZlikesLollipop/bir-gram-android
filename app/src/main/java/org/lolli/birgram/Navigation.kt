package org.lolli.birgram

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    val route by tgViewModel.initialRoute.collectAsState()
    NavHost(
        navController = navController,
        startDestination = route
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
            ChatsScreen()
        }
    }
}

sealed class Route(val route: String) {
    object Auth: Route("login")
    object Chats: Route("chats")
    object PasswordRecovery: Route("passwordRecovery")
    object Chat: Route("chat")
}