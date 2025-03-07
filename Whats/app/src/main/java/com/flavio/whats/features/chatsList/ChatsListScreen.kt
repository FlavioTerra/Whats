package com.flavio.whats.features.chatsList

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.flavio.whats.ui.theme.WhatsTheme
import kotlin.random.Random

@Composable
fun ChatsListScreen(
    state: ChatsListScreenState,
    modifier: Modifier = Modifier
) {
    when (state) {
        ChatsListScreenState.Loading -> {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }

        is ChatsListScreenState.Success -> {
            LazyColumn(
                modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        Modifier
                            .clip(CircleShape)
                            .fillMaxWidth()
                            .background(Color.Gray)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Search...")
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        state.filters.forEach { filter ->
                            Box(
                                Modifier
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                                    .padding(16.dp, 8.dp)
                            ) {
                                Text(filter)
                            }
                        }
                    }
                }

                items (state.chats) { chat ->
                    Row(Modifier.fillMaxWidth()) {
                        chat.avatar?.let { avatar ->
                            SubcomposeAsyncImage(
                                avatar,
                                contentDescription = null,
                                Modifier.size(54.dp).clip(CircleShape),
                                loading = {
                                    Box(Modifier
                                        .fillMaxSize()
                                        .shimmer()
                                    )
                                }
                            )

                        } ?: Box(
                            Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(
                                    Color(
                                        Random.nextInt(1, 255),
                                        Random.nextInt(1, 255),
                                        Random.nextInt(1, 255)
                                    )
                                )

                        ) {
                            Text(
                                chat.name.first().toString(),
                                Modifier.align(Alignment.Center),
                                style = TextStyle.Default.copy(
                                    fontSize = 24.sp
                                )
                            )
                        }
                        Spacer(Modifier.size(16.dp))
                        Column(Modifier.align(alignment = Alignment.CenterVertically)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    chat.name,
                                    Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle.Default.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                )
                                Spacer(Modifier.size(8.dp))
                                Text(
                                    chat.lastMessage.date,
                                    style = TextStyle.Default.copy(
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(Modifier.weight(1f)) {
                                    if (chat.lastMessage.author != state.currentUser) {
                                        Icon(Icons.Default.DoneAll, contentDescription = null)
                                        Spacer(Modifier.size(4.dp))
                                    }
                                    Text(
                                        chat.lastMessage.text,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Box(
                                    Modifier
                                        .size(26.dp)
                                        .background(Color.Green, CircleShape)
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                ) {
                                    Text(
                                        chat.unreadMessages.toString(),
                                        Modifier.align(Alignment.Center),
                                        style = TextStyle.Default.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun Modifier.shimmer(
    colors: List<Color> =
        listOf(
            Color.Gray.copy(alpha = 0.5f),
            Color.Gray.copy(alpha = 0.1f),
            Color.Gray.copy(alpha = 0.5f),
        )
) : Modifier {
    val infiniteTransition =
        rememberInfiniteTransition(label = "infiniteTransition")
    val localConfig = LocalConfiguration.current
    val target = (localConfig.screenWidthDp * 3).toFloat()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = target,
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ),
        label = "shimmer"
    )

    return this.then(
        Modifier.background(
            Brush.linearGradient(
                colors = colors,
                end = Offset(x = scale, y = scale)
            )
        )
    )
}

@Preview
@Composable
private fun ChatsListScreenPreview() {
    WhatsTheme {
        Surface {
            ChatsListScreen(
                state = ChatsListScreenState.Success(
                    currentUser = User("Flavio"),
                    filters = listOf("All", "Unread", "Groups"),
                    chats = List(10) {
                        Chat(
                            avatar = if (Random.nextBoolean()) "Avatar" else null,
                            name = LoremIpsum(Random.nextInt(1, 10)).values.first(),
                            lastMessage = Message(
                                text = LoremIpsum(Random.nextInt(1, 10)).values.first(),
                                date = "01/01/25",
                                isRead = true,
                                author = if (Random.nextBoolean()) {
                                    User("Flavio")
                                } else {
                                    User("José")
                                }
                            ),
                            unreadMessages = Random.nextInt(1, 20)
                        )
                    }
                )
            )
        }
    }
}

@Preview
@Composable
private fun ChatsListScreenWithLoadingStatePreview() {
    WhatsTheme {
        Surface {
            ChatsListScreen(
                state = ChatsListScreenState.Loading
            )
        }
    }
}
