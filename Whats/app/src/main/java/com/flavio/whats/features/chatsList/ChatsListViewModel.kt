package com.flavio.whats.features.chatsList

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.absoluteValue
import kotlin.random.Random

sealed class ChatsListScreenState {
    data object Loading : ChatsListScreenState()
    data class Success(
        val currentUser: User,
        val filters: List<String> = emptyList(),
        val chats: List<Chat> = emptyList()
    ) : ChatsListScreenState()
}

class Chat(
    val avatar: String?,
    val name: String,
    val lastMessage: Message,
    val unreadMessages: Int
)

class Message(
    val text: String,
    val date: String,
    val isRead: Boolean,
    val author: User
)

data class User (
    val name: String
)

class ChatsListViewModel : ViewModel() {

    private val _state = MutableStateFlow<ChatsListScreenState>(
        ChatsListScreenState.Loading
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {

            val user = fetchUser()
            val filters = fetchFilter()
            val chats = fetchChats()

            delay(3000)

            _state.update {
                ChatsListScreenState.Success(
                    currentUser = user,
                    filters = filters,
                    chats = chats
                )
            }
        }
    }

    private fun fetchUser(): User {
        return User("Flavio")
    }

    private fun fetchFilter(): List<String> {
        return listOf("All", "Unread", "Groups")
    }

    private fun fetchChats(): List<Chat> {
        val avatarIterator = avatars.shuffled().listIterator()
        return List(10) {
            val localDateTime = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date.minus(it, DateTimeUnit.DAY)
                .atTime(Random.nextInt(0, 23), Random.nextInt(0, 59))
            Chat(
                avatar = if (Random.nextBoolean()) avatarIterator.next() else null,
                name = LoremIpsum(Random.nextInt(1, 10)).values.first(),
                lastMessage = Message(
                    text = LoremIpsum(Random.nextInt(1, 10)).values.first(),
                    date = localDateTime.formattedDateForChatLastMessage(),
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
    }
}

private val avatars = mutableListOf(
    "https://img.freepik.com/psd-gratuitas/renderizacao-3d-de-avatar_23-2150833548.jpg?t=st=1741291224~exp=1741294824~hmac=b7731f18899a6b78a6ff3ce9ac2cbaa15c8cb3ba8edad670c8731e1f86ced45b&w=740",
    "https://img.freepik.com/psd-gratuitas/ilustracao-3d-de-avatar-ou-perfil-humano_23-2150671142.jpg?t=st=1741291363~exp=1741294963~hmac=f5293ca4169d0b4465704ef770a0221aa7a08bbdf1a51f64faf359019e3269f8&w=740",
    "https://img.freepik.com/psd-gratuitas/ilustracao-3d-de-avatar-ou-perfil-humano_23-2150671116.jpg?t=st=1741291394~exp=1741294994~hmac=5b96918ba8e7793f558f77c2499400ad34d4214a9c2584abbba37a8706ca6f9a&w=740",
    "https://img.freepik.com/psd-gratuitas/renderizacao-3d-do-estilo-de-cabelo-para-o-design-do-avatar_23-2151869153.jpg?t=st=1741291401~exp=1741295001~hmac=ce7b4d9b193dbaf3d85fdb3486e79b7821d10c34ee6ca09ea7b7507b59d302c7&w=740",
)

@OptIn(FormatStringsInDatetimeFormats::class)
fun LocalDateTime.formattedDateForChatLastMessage(): String {
    val nowLocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val dayUntil = nowLocalDateTime.date.daysUntil(this.date).absoluteValue
    return when {
        dayUntil == 0 -> {
            this.time.format(LocalTime.Format {
                byUnicodePattern("HH:mm")
            })
        }
        dayUntil < 2 -> {
            "ontem"
        }
        dayUntil < 8 -> {
            this.date.format(LocalDate.Format {
                this.dayOfWeek(
                    names = DayOfWeekNames(
                        monday = "segunda",
                        tuesday = "terça",
                        wednesday = "quarta",
                        thursday = "quinta",
                        friday = "sexta",
                        saturday = "sábado",
                        sunday = "domingo"
                    )
                )
            })
        }

        else -> {
            this.format(LocalDateTime.Format {
                byUnicodePattern("dd/MM/yy")
            })
        }
    }
}