package ru.bstu.diploma.models.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatItem (
    val id: String,
    val avatar: String?,
    val initials: String,
    var title: String,
    val lastMessageSenderId: String?,
    val shortDescription: String?,
    val unreadCount: Int = 0,
    val lastMessageDate: String?,
    val group: String?,
    val isProfessor: Boolean? = false,
    val isGroupLeader: Boolean? = false,
    val isOnline: Boolean? = false,
    val chatType : ChatType = ChatType.SINGLE,
    var author :String? = null
): Parcelable