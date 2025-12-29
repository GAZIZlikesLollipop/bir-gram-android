package org.lolli.birgram.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.valentinilk.shimmer.shimmer
import org.drinkless.tdlib.TdApi
import org.lolli.birgram.R
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Instant
import kotlin.time.toJavaInstant

@Composable
fun MessageThumbnail(
    content: Any?,
    contentText: String
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AsyncImage(
            model = content,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = contentText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.W400,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
@Composable
fun MessageText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
){
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Start,
        fontSize = 10.sp,
        modifier = modifier
    )
}
@Composable
fun ChatCard(
    isLoading: Boolean = false,
    chatIcon: Any?,
    title: String,
    lastMessage: TdApi.MessageContent?,
    lastMessageDate: String,
    unreadCount: Int,
    unreadMention: Int,
    unreadReaction: Boolean,
    isRead: Boolean? = null,
    isOutgoing: Boolean = false
    //isOnline: Boolean? = null
    //sender: String? = null
    //isNotificationBlocked: Boolean? = null
) {
    val containerHeight = 80.dp
    val contentHeight = 55.dp
    val windowInfo = LocalWindowInfo.current
    val placeHolderColor = MaterialTheme.colorScheme.onBackground.copy(0.5f)
    val placeHolderHeight = (contentHeight / 2) - 8.dp
    val cnt = stringArrayResource(R.array.chats_cnt)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(containerHeight)
            .width(windowInfo.containerDpSize.width)
            .clickable {}
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                !isLoading && title.isBlank() -> {
                    Box(
                        modifier =
                            Modifier
                                .size(contentHeight)
                                .clip(CircleShape)
                                .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.skull),
                            contentDescription = cnt[2],
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size((contentHeight/2)+5.dp)
                        )
                    }
                }
                chatIcon != null -> {
                    AsyncImage(
                        model = chatIcon,
                        contentDescription = title,
                        modifier = Modifier.size(contentHeight).clip(CircleShape),
                    )
                }
                else -> {
                    Box(
                        modifier = if (title.isNotBlank()) {
                            Modifier
                                .size(contentHeight)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        } else {
                            Modifier
                                .shimmer()
                                .size(contentHeight)
                                .clip(CircleShape)
                                .background(placeHolderColor)
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (title.isNotEmpty()) title[0].toString() else "",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.W600,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!isLoading) {
                        Text(
                            text = title.ifBlank { cnt[2] },
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(2f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start
                        )
                        Row(
                            modifier = Modifier.weight(1.3f),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            if(isRead != null){
                                Icon(
                                    imageVector = if(isRead) ImageVector.vectorResource(R.drawable.done_all) else ImageVector.vectorResource(R.drawable.check),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                            }
                            Text(
                                text = lastMessageDate,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.End
                            )
                        }
                    } else {
                        Box(
                            Modifier
                                .shimmer()
                                .clip(RoundedCornerShape(0.dp))
                                .background(placeHolderColor)
                                .height(placeHolderHeight)
                                .weight(2f)
                        ) {}
                        Spacer(Modifier.weight(1f))
                        Box(
                            Modifier
                                .shimmer()
                                .clip(RoundedCornerShape(0.dp))
                                .background(placeHolderColor)
                                .height(placeHolderHeight)
                                .weight(0.75f)
                        ) {}
                    }
                }
                Spacer(Modifier.height(if (isLoading) 4.dp else 6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!isLoading) {
                        when(lastMessage){
                            is TdApi.MessageCall -> {
                                val call = if(lastMessage.isVideo) "${cnt[7]} ${cnt[12]}" else cnt[12]
                                val reason = when(lastMessage.discardReason){
                                    is TdApi.CallDiscardReasonDeclined -> "${cnt[13]} $call"
                                    is TdApi.CallDiscardReasonMissed -> "${cnt[14]} $call"
                                    is TdApi.CallDiscardReasonHungUp -> "${cnt[15]} $call"
                                    is TdApi.CallDiscardReasonDisconnected -> "${cnt[16]} $call"
                                    is TdApi.CallDiscardReasonEmpty -> "${cnt[17]} $call"
                                    is TdApi.CallDiscardReasonUpgradeToGroupCall -> "${cnt[18]}: ${(lastMessage.discardReason as TdApi.CallDiscardReasonUpgradeToGroupCall).inviteLink}"
                                    else -> if(isOutgoing) "${cnt[3]} $call" else "${cnt[4]} $call"
                                }
                                MessageText(
                                    text = if(lastMessage.duration > 0) "$reason (${lastMessage.duration} ${cnt[5]})" else reason,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(12f)
                                )
                            }
                            is TdApi.MessageText -> {
                                MessageText(
                                    text = lastMessage.text.text,
                                    modifier = Modifier.weight(12f)
                                )
                            }
                            is TdApi.MessagePhoto -> {
                                MessageThumbnail(
                                    content = lastMessage.photo.minithumbnail?.data,
                                    contentText = cnt[6]
                                )
                            }
                            is TdApi.MessageVideo -> {
                                MessageThumbnail(
                                    content = lastMessage.video.minithumbnail?.data,
                                    contentText = cnt[7]
                                )
                            }
                            is TdApi.MessageAudio -> {
                                MessageText(
                                    text = "🎧${lastMessage.audio.title} - ${lastMessage.audio.performer}",
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(2f)
                                )
                            }
                            is TdApi.MessageVoiceNote -> {
                                Text(
                                    text = "${cnt[8]} ${cnt[9]}",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.weight(2f)
                                )
                            }
                            is TdApi.MessageVideoNote -> {
                                MessageThumbnail(
                                    content = lastMessage.videoNote.minithumbnail?.data,
                                    contentText = "${cnt[7]} ${cnt[9]}"
                                )
                            }
                            is TdApi.MessageDocument -> {
                                Text(
                                    text = lastMessage.document.fileName,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.weight(2f)
                                )
                            }
                            is TdApi.MessageAnimation -> {
                                MessageThumbnail(
                                    content = lastMessage.animation.minithumbnail?.data,
                                    contentText = "GIF"
                                )
                            }
                            is TdApi.MessageSticker -> {
                                MessageText(
                                    text = "${lastMessage.sticker.emoji} ${cnt[10]}",
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(2f)
                                )
                            }
                            else -> {
                                MessageText(
                                    text = cnt[11],
                                    modifier = Modifier.weight(2f)
                                )
                            }
                        }
                    } else {
                        Box(
                            Modifier
                                .shimmer()
                                .clip(RoundedCornerShape(0.dp))
                                .background(placeHolderColor)
                                .height(placeHolderHeight)
                                .weight(1f)
                        ) {}
                    }
                    Spacer(Modifier.weight(1f))
                    if(unreadReaction && !isLoading) {
                        Box(
                            modifier =
                                Modifier
                                    .clip(CircleShape)
                                    .background(Color.Red)
                                    .size((contentHeight / 2) + 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.favorite),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp).offset(y = 1.dp)
                            )
                        }
                        if (unreadCount > 0 || unreadMention > 0) {
                            Spacer(Modifier.width(6.dp))
                        }
                    }
                    if (unreadCount > 0 || unreadMention > 0 || isLoading) {
                        Box(
                            modifier = if(!isLoading){
                                Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if(unreadMention > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(0.11f)
                                    )
                                    .size((contentHeight / 2) + 3.dp)
                            } else {
                                Modifier
                                    .shimmer()
                                    .clip(CircleShape)
                                    .background(placeHolderColor)
                                    .size((contentHeight / 2) + 3.dp)
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if(!isLoading){
                                    if(unreadMention > 0) "@${unreadMention}" else unreadCount.toString()
                                } else "",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatList(
    chatList: Map<Long,TdApi.Chat>,
    paddingValues: PaddingValues,
    chatsPhotos: Map<Long,Any?>,
    isNewAccount: Boolean
){
    val cnt = stringArrayResource(R.array.chats_cnt)
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding()),
        verticalArrangement = if(chatList.isNotEmpty()) Arrangement.spacedBy(2.dp) else Arrangement.Center
    ){
        if(chatList.isNotEmpty()){
            items(
                chatList.toList().sortedByDescending { srt -> srt.second.positions.find { it.list is TdApi.ChatListMain }?.order }
            ){ (order,chat) ->
                val rawLastMessageDate = Instant.fromEpochSeconds(chat.lastMessage?.date?.toLong() ?: 0)
                val dateJava = LocalDateTime.ofInstant(
                    rawLastMessageDate.toJavaInstant(),
                    Clock.systemDefaultZone().zone
                )
                val nowDate = LocalDateTime.now()
                val today = nowDate.dayOfYear == dateJava.dayOfYear && nowDate.year == dateJava.year
                val thisWeek = nowDate.minusWeeks(1) < dateJava
                val thisYear = nowDate.year == dateJava.year
                val lastMessageDate = when {
                    today -> {
                        dateJava.format(DateTimeFormatter.ofPattern("HH:mm"))
                    }
                    !today && thisWeek -> {
                        dateJava.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    }
                    thisYear -> {
                        "${dateJava.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${dateJava.dayOfMonth}"
                    }
                    else -> {
                        dateJava.format(DateTimeFormatter.ofPattern("dd.MM.yy"))
                    }
                }
                ChatCard(
                    chatIcon = chatsPhotos[order],
                    title = chat.title,
                    lastMessage = chat.lastMessage?.content,
                    lastMessageDate = lastMessageDate,
                    unreadCount = chat.unreadCount,
                    unreadMention = chat.unreadMentionCount,
                    unreadReaction = chat.unreadReactionCount > 0,
                    isOutgoing = chat.lastMessage?.isOutgoing ?: true,
                    isRead = if(chat.lastMessage != null){
                        if(
                            chat.lastMessage!!.isOutgoing &&
                            !chat.lastMessage!!.isChannelPost
                        ){
                            chat.lastMessage!!.id <= chat.lastReadOutboxMessageId
                        } else {
                            null
                        }
                    } else null
                )
            }
        } else {
            if(isNewAccount){
                item {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = cnt[0],
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = cnt[1],
                            color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 4.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                repeat(10){
                    item {
                        ChatCard(
                            isLoading = true,
                            chatIcon = null,
                            title = "",
                            lastMessage = null,
                            lastMessageDate = "",
                            unreadCount = 0,
                            unreadMention = 0,
                            unreadReaction = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatsScreen(
    viewModel: TGViewModel,
    paddingValues: PaddingValues
){
    val chatList by viewModel.chats.collectAsState()
    val chatsPhotos by viewModel.chatsPhotos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadChats()
    }

    LaunchedEffect(chatList) {
        viewModel.archiveChats.putAll(chatList.filter { it.value.positions.find { it.list is TdApi.ChatListArchive } != null })
    }

    LaunchedEffect(chatList) {
        viewModel.folderChats.clear()
        val chats = chatList.filter { chat ->
            chat.value.positions.find {
                it.list is TdApi.ChatListFolder
            } != null
        }
        chats.forEach { chat ->
            chat.value.positions.forEach {
                if(it.list is TdApi.ChatListFolder){
                    val idi = (it.list as TdApi.ChatListFolder).chatFolderId
                    if(viewModel.folderChats.containsKey(idi)){
                        val map = viewModel.folderChats[idi]!!.toMutableMap().apply { put(chat.key,chat.value) }
                        viewModel.folderChats[idi] = map
                    } else {
                        viewModel.folderChats[idi] = mapOf(chat.key to chat.value)
                    }
                }
            }
        }
    }

    AnimatedContent(
        targetState = viewModel.targetChatList,
        transitionSpec =
            {
                when(viewModel.animationDirection){
                    Direction.Left -> slideInHorizontally(tween(300,50),{-it}).togetherWith(slideOutHorizontally(tween(300),{it}))
                    Direction.Right -> slideInHorizontally(tween(300,50),{it}).togetherWith(slideOutHorizontally(tween(300),{-it}))
                    Direction.Up -> slideInVertically(tween(300,50),{it}).togetherWith(slideOutVertically(tween(300),{-it}))
                    Direction.Down -> slideInVertically(tween(300,50),{-it}).togetherWith(slideOutVertically(tween(300),{it}))
                }
            }
    ) { state ->
        when(state){
            is TdApi.ChatListMain -> {
                ChatList(
                    chatList = chatList,
                    paddingValues = paddingValues,
                    chatsPhotos = chatsPhotos,
                    isNewAccount = viewModel.isNewAccount
                )
            }
            is TdApi.ChatListFolder -> {
                if(viewModel.folderChats.containsKey(state.chatFolderId)){
                    ChatList(
                        chatList = viewModel.folderChats[state.chatFolderId]!!,
                        paddingValues = paddingValues,
                        chatsPhotos = chatsPhotos,
                        isNewAccount = viewModel.isNewAccount
                    )
                }
            }
            is TdApi.ChatListArchive -> {
                ChatList(
                    chatList = viewModel.archiveChats,
                    paddingValues = paddingValues,
                    chatsPhotos = chatsPhotos,
                    isNewAccount = viewModel.isNewAccount
                )
            }
        }
    }
}