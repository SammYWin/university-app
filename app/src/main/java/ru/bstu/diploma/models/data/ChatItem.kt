package ru.bstu.diploma.models.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.bstu.diploma.models.BaseMessage

@Parcelize
data class ChatItem (
    val id: String,
    val avatar: String?,
    val initials: String,
    val title: String,
    val lastMessageSenderId: String?,
    val shortDescription: String?,
    val unreadCount: Int = 0,
    val lastMessageDate: String?,
    val isOnline: Boolean? = false,
    val chatType : ChatType = ChatType.SINGLE,
    var author :String? = null
): Parcelable