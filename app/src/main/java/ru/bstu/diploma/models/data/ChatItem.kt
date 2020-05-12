package ru.bstu.diploma.models.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatItem (
    val id: String,
    val avatar: String?,
    val initials: String,
    val title: String,
    val shortDescription: String?,
    val messageCount: Int = 0,
    val lastMessageDate: String?,
    val isOnline: Boolean? = false,
    val chatType : ChatType = ChatType.SINGLE,
    var author :String? = null
): Parcelable