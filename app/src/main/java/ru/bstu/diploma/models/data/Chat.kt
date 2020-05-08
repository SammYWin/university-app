package ru.bstu.diploma.models.data

import com.google.firebase.firestore.DocumentId
import ru.bstu.diploma.App
import ru.bstu.diploma.R
import ru.bstu.diploma.extensions.shortFormat
import ru.bstu.diploma.models.BaseMessage
import ru.bstu.diploma.models.TextMessage
import ru.bstu.diploma.utils.Utils
import java.util.*

data class Chat(
    @DocumentId
    val id: String,
    val title: String,
    val members: List<User> = listOf(),
    var messages: MutableList<BaseMessage> = mutableListOf(),
    var isArchived: Boolean = false
) {
    private fun unreadableMessageCount(): Int = messages.filter{ message -> !message.isReaded }.count()

    private fun lastMessageDate(): Date? = if(messages.isEmpty()) null else messages.last().date

    private fun lastMessageShort(): Pair<String, String?>{
        val lastMessage = messages.lastOrNull()
        var first =  App.applicationContext().resources.getString(R.string.chat_no_messages)
        val second = lastMessage?.senderName

        if (lastMessage != null) {
            first = if(lastMessage is TextMessage) lastMessage.text!!
                    else "${lastMessage.senderName} - ${App.applicationContext().resources.getString(R.string.send_photo)}"
        }

        return first to second
    }

    private fun isSingle(): Boolean = members.size == 1


    fun toChatItem(): ChatItem {
        return if (isSingle()) {
            val user = members.first()
            ChatItem(
                id,
                user.avatar,
                Utils.toInitials(user.firstName, user.lastName) ?: "??",
                "${user.firstName ?: ""} ${user.lastName ?: ""}",
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                user.isOnline
            )
        } else {
            ChatItem(
                id,
                null,
                "",
                title,
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                false,
                ChatType.GROUP,
                lastMessageShort().second
            )
        }
    }

    fun toArchiveChatItem(chats : List<Chat>) : ChatItem{
        val archivedChats = chats
            .filter { it.isArchived }
            .sortedBy { it.lastMessageDate()}
        return ChatItem(
            "-1",
            null,
            "",
            App.applicationContext().resources.getString(R.string.item_archive_title),
            archivedChats.last().lastMessageShort().first,
            archivedChats.sumBy { it.unreadableMessageCount() },
            archivedChats.last().lastMessageDate()?.shortFormat(),
            chatType = ChatType.ARCHIVE,
            author = archivedChats.last().lastMessageShort().second
        )
    }
}

enum class ChatType{
    SINGLE,
    GROUP,
    ARCHIVE
}



