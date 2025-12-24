package org.lolli.birgram.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.drinkless.tdlib.TdApi
import org.lolli.birgram.BackArrow
import org.lolli.birgram.R
import org.lolli.birgram.Route
import org.lolli.birgram.presentation.TGViewModel

@Composable
fun ChatsTopBar(){
    val containerHeight = 80.dp
    Box(
        modifier = Modifier.fillMaxWidth().height(containerHeight).statusBarsPadding().background(MaterialTheme.colorScheme.surface)
    ){
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.menu),
                    contentDescription = "menu",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {},
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.search),
                    contentDescription = "menu",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTopBar(
    navController: NavController,
    tgViewModel: TGViewModel
){
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val loginState by tgViewModel.loginState.collectAsState()
    val authHistory by tgViewModel.authHistory.collectAsState()
    val isDark by tgViewModel.isDarkTheme.collectAsState()
    TopAppBar(
        title = {},
        navigationIcon = {
            if(currentRoute == Route.PasswordRecovery.route){
                BackArrow { navController.navigate(Route.Auth.route) }
            }
            if(authHistory.size > 1 && currentRoute != Route.PasswordRecovery.route){
                if (
                    loginState.javaClass != TdApi.AuthorizationStateWaitPhoneNumber::class.java &&
                    loginState.javaClass != TdApi.AuthorizationStateWaitRegistration::class.java &&
                    authHistory[authHistory.lastIndex-1] !is TdApi.AuthorizationStateWaitTdlibParameters
                ) {
                    BackArrow { tgViewModel.previousAuthHistory() }
                }
            }
        },
        actions = {
            if (loginState is TdApi.AuthorizationStateWaitPhoneNumber && !tgViewModel.isNumber) {
                IconButton(
                    onClick = {tgViewModel.switchIsDark()}
                ) {
                    Icon(
                        imageVector = if(isDark) ImageVector.vectorResource(R.drawable.light_mode) else ImageVector.vectorResource(R.drawable.dark_mode),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}