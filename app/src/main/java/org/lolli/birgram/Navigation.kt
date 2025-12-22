package org.lolli.birgram

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.drinkless.tdlib.TdApi
import org.lolli.birgram.data.AuthData
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
        composable(Route.Default.route) {
            Default(tgViewModel,navController)
        }
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

@Composable
fun Default(
    viewModel: TGViewModel,
    navController: NavHostController
){
    val initialRoute by viewModel.initialRoute.collectAsState()
    LaunchedEffect(initialRoute){
        if(initialRoute != Route.Auth.route){
            viewModel.nextLoginStep(TdApi.AuthorizationStateWaitTdlibParameters(), AuthData.TDLibParameters)
            navController.navigate(Route.Chats.route)
        } else {
            navController.navigate(Route.Auth.route)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text("")
    }
}

sealed class Route(val route: String) {
    object Default: Route("default")
    object Auth: Route("login")
    object Chats: Route("chats")
    object PasswordRecovery: Route("passwordRecovery")
    object Chat: Route("chat")
}