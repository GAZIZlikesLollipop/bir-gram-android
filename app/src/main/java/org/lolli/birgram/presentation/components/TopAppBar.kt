package org.lolli.birgram.presentation.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.drinkless.tdlib.TdApi
import org.lolli.birgram.BackArrow
import org.lolli.birgram.R
import org.lolli.birgram.Route
import org.lolli.birgram.presentation.Direction
import org.lolli.birgram.presentation.TGViewModel

@Composable
fun FolderSection(
   isSelected: Boolean = false,
   onSelected: () -> Unit,
   title: String,
   icon: ImageVector,
   unreadCount: Int
){
    val contentHeight = 40.dp
    val color by animateColorAsState(if(isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(0.5f))
    var width by rememberSaveable { mutableIntStateOf(0) }
    Box(
        modifier = Modifier.clickable{ onSelected() }
    ){
        Column(
            modifier = Modifier.padding(8.dp).fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.onGloballyPositioned{width = it.size.width}
            ) {
                Icon(
                    imageVector = icon,
                    tint = color,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(6.dp))
                if(isSelected){
                    Text(
                        text = title,
                        color = color,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                if(unreadCount > 0) {
                    Spacer(Modifier.width(2.dp))
                    Box(
                        modifier =
                            Modifier
                                .clip(CircleShape)
                                .background(color)
                                .size((contentHeight / 2) + 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$unreadCount",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            if(isSelected){
                Spacer(Modifier.weight(1f))
                HorizontalDivider(
                    thickness = 3.dp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width((width/3).dp)
                )
            }
        }
    }
}

@Composable
fun ChatsTopBar(
    viewModel: TGViewModel
){
    val folders by viewModel.folders.collectAsState()
    val unreadCount by viewModel.unreadChatCounts.collectAsState()
    val containerHeight = 80.dp
    Box(
        modifier = Modifier.fillMaxWidth().height(containerHeight).statusBarsPadding().background(MaterialTheme.colorScheme.surface)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.menu),
                    contentDescription = "menu",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(26.dp)
                )
            }
            if(folders.isNotEmpty()){
                var selected by rememberSaveable { mutableIntStateOf(0) }
                LazyRow(
                    modifier = Modifier.weight(4f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    itemsIndexed(folders){ index,folder ->
                        val icon = when (folder.icon.name) {
                            "All" -> ImageVector.vectorResource(R.drawable.inbox)
                            "Unread" -> ImageVector.vectorResource(R.drawable.mark_email_unread)
                            "Unmuted" -> ImageVector.vectorResource(R.drawable.notifications_active)
                            "Setup" -> ImageVector.vectorResource(R.drawable.settings)
                            "Custom" -> ImageVector.vectorResource(R.drawable.tune)
                            "Bots" -> ImageVector.vectorResource(R.drawable.smart_toy)
                            "Channels" -> ImageVector.vectorResource(R.drawable.campaign)
                            "Groups" -> ImageVector.vectorResource(R.drawable.groups)
                            "Private" -> ImageVector.vectorResource(R.drawable.lock)
                            "Favorite" -> ImageVector.vectorResource(R.drawable.star)
                            "Like" -> ImageVector.vectorResource(R.drawable.thumb_up)
                            "Love" -> ImageVector.vectorResource(R.drawable.favorite)
                            "Party" -> ImageVector.vectorResource(R.drawable.celebration)
                            "Mask" -> ImageVector.vectorResource(R.drawable.masks)
                            "Cat" -> ImageVector.vectorResource(R.drawable.pets)
                            "Crown" -> ImageVector.vectorResource(R.drawable.crown)
                            "Flower" -> ImageVector.vectorResource(R.drawable.local_florist)
                            "Game" -> ImageVector.vectorResource(R.drawable.sports_esports)
                            "Sport" -> ImageVector.vectorResource(R.drawable.sports_soccer)
                            "Study" -> ImageVector.vectorResource(R.drawable.school)
                            "Work" -> ImageVector.vectorResource(R.drawable.work)
                            "Home" -> ImageVector.vectorResource(R.drawable.home)
                            "Travel" -> ImageVector.vectorResource(R.drawable.travel_explore)
                            "Airplane" -> ImageVector.vectorResource(R.drawable.flight)
                            "Trade" -> ImageVector.vectorResource(R.drawable.swap_horizontal_circle)
                            "Money" -> ImageVector.vectorResource(R.drawable.payments)
                            "Note" -> ImageVector.vectorResource(R.drawable.note_stack)
                            "Book" -> ImageVector.vectorResource(R.drawable.menu_book)
                            "Palette" -> ImageVector.vectorResource(R.drawable.palette)
                            "Light" -> ImageVector.vectorResource(R.drawable.lightbulb)
                            else -> ImageVector.vectorResource(R.drawable.folder)
                        }

                        FolderSection(
                            isSelected = selected == index,
                            onSelected = {
                                if(selected != index){
                                    viewModel.loadChats(TdApi.ChatListFolder().apply { this.chatFolderId = folder.id })
                                    if(selected < index) {
                                        viewModel.animationDirection = Direction.Right
                                    } else {
                                        viewModel.animationDirection = Direction.Left
                                    }
                                    if(index == 0){
                                        viewModel.targetChatList = TdApi.ChatListMain()
                                    } else {
                                        Log.d("FUCK","${folder.id}")
                                        viewModel.targetChatList = TdApi.ChatListFolder().apply { this.chatFolderId = folder.id }
                                    }
                                    selected = index
                                }
                            },
                            title = folder.name.text.text,
                            icon = icon,
                            unreadCount = if(index != 0){
                                unreadCount[TdApi.ChatListFolder().apply { chatFolderId = folder.id }] ?: 0
                            } else {
                                unreadCount[TdApi.ChatListMain()] ?: 0
                            }
                        )
                    }
                }
            } else {
                Spacer(Modifier.weight(4f))
            }
            IconButton(
                onClick = {},
                modifier = Modifier.weight(1f)
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