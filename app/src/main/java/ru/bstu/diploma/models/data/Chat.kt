package ru.bstu.diploma.models.data

import com.google.firebase.auth.FirebaseAuth
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
    var title: String,
    var avatar: String?,
    var members: MutableList<User> = mutableListOf(), //mutable or not mutable?
    var messages: MutableList<BaseMessage> = mutableListOf(),
    var isArchived: Boolean = false,
    var unreadCount: Int = 0
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

    private fun isSingle(): Boolean = members.size == 2


    fun toChatItem(): ChatItem {
        return if (isSingle()) {
            val otherUser = members.find { user -> user.id != FirebaseAuth.getInstance().currentUser!!.uid }!!
            ChatItem(
                id,
                otherUser.avatar,
                Utils.toInitials(otherUser.firstName, otherUser.lastName) ?: "??",
                "${otherUser.firstName ?: ""} ${otherUser.lastName ?:""}", //+
                        //" ${if(otherUser.isGroupLeader == true) "(ст. ${otherUser.group})" else ""}",
                messages.lastOrNull()?.senderId ?: "",
                lastMessageShort().first,
                unreadCount,
                lastMessageDate()?.shortFormat(),
                otherUser.group,
                otherUser.isProfessor,
                otherUser.isGroupLeader,
                otherUser.isOnline
            )
        } else {
            ChatItem(
                id,
                null,
                "",
                members.map { it.firstName }.joinToString(", "),
                messages.lastOrNull()?.senderId ?: "",
                lastMessageShort().first,
                unreadCount,
                lastMessageDate()?.shortFormat(),
                null,
                false,
                false,
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
            messages.lastOrNull()?.senderId ?: "",
            archivedChats.last().lastMessageShort().first,
            archivedChats.sumBy { it.unreadCount },
            archivedChats.last().lastMessageDate()?.shortFormat(),
            chatType = ChatType.ARCHIVE,
            author = archivedChats.last().lastMessageShort().second,
            group = null
        )
    }
}

enum class ChatType{
    SINGLE,
    GROUP,
    ARCHIVE
}



