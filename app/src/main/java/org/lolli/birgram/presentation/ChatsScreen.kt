package org.lolli.birgram.presentation

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.valentinilk.shimmer.shimmer
import org.lolli.birgram.R
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.time.Instant
import kotlin.time.toJavaInstant

@Composable
fun ChatCard(
    chatIcon: Any?,
    title: String,
    lastMessage: String,
    lastMessageColor: Color,
    lastMessageDate: String,
    unreadCount: Int,
    unreadMention: Int,
    unreadReaction: Int,
    isDeleted: Boolean
) {
    val containerHeight = 90.dp
    val contentHeight = 55.dp
    val windowInfo = LocalWindowInfo.current
    val placeHolderColor = MaterialTheme.colorScheme.onBackground.copy(0.4f)
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
            modifier = Modifier.fillMaxWidth().padding(8.dp).padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                isDeleted -> {
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
                    if (title.isNotBlank()) {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(2f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = lastMessageDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End
                        )
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
                Spacer(Modifier.height(if (title.isNotBlank()) 4.dp else 12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (title.isNotBlank()) {
                        Text(
                            text = lastMessage,
                            color = lastMessageColor,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start
                        )
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
                    if (unreadCount > 0 || unreadMention > 0 || unreadReaction > 0) {
                        Box(
                            modifier = if(title.isNotBlank()){
                                Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if(unreadCount > 0) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.primaryContainer
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
                                text = if(title.isNotBlank()){
                                    when {
                                        unreadCount > 0 -> unreadCount.toString()
                                        unreadReaction > 0 -> unreadReaction.toString()
                                        true -> "@${unreadMention}"
                                        else -> ""
                                    }
                                } else "",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.labelSmall,
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
fun ChatsScreen(
    viewModel: TGViewModel,
    paddingValues: PaddingValues
){
    val cnt = stringArrayResource(R.array.chats_cnt)
    val mainChats by viewModel.mainChats.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadChats()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding()),
        verticalArrangement = if(mainChats.isNotEmpty()) Arrangement.spacedBy(2.dp) else Arrangement.Center
    ){
        if(mainChats.isNotEmpty()){
            items(mainChats.toList().sortedByDescending { it.first }){ (_, chat) ->
                var chatIcon by remember { mutableStateOf(
                    when {
                        chat.photo?.small?.local?.path != null && chat.photo?.small?.local?.isDownloadingCompleted == true -> {
                            chat.photo?.small?.local?.path
                        }
                        chat.photo?.minithumbnail != null -> chat.photo?.minithumbnail?.data
                        else -> null
                    }
                ) }
                val rawLastMessageDate = Instant.fromEpochSeconds(chat.lastMessage?.date?.toLong() ?: 0)
                val dateJava = LocalDateTime.ofInstant(
                    rawLastMessageDate.toJavaInstant(),
                    java.time.Clock.systemDefaultZone().zone
                )
                val nowDate = LocalDateTime.now()
                val today = nowDate.minusDays(1) < dateJava
                val thisWeek = nowDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) < nowDate
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
                    chatIcon = chatIcon,
                    title = chat.title,
                    lastMessage = "hello, how are you!",
                    lastMessageColor = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                    lastMessageDate = lastMessageDate,
                    unreadCount = chat.unreadCount,
                    unreadMention = chat.unreadMentionCount,
                    unreadReaction = chat.unreadReactionCount,
                    isDeleted = false
                )
            }
        } else {
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
        }
    }
}