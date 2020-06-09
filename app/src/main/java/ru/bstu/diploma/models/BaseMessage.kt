package ru.bstu.diploma.models

import com.google.firebase.firestore.DocumentId
import ru.bstu.diploma.models.data.Chat
import ru.bstu.diploma.models.data.User
import java.util.*

abstract class BaseMessage(
    @DocumentId
    val id: String,
    val type: String,
    val senderId: String,
    val senderName: String,
    val isIncoming: Boolean = true,
    val date: Date = Date(),
    var isReaded: Boolean = true
)
{
    constructor(): this("", "", "", "",false, Date(), true)

    abstract fun formatMessage() : String

    companion object AbstractFactory
    {
        var lastId = -1
        fun makeMessage(senderId: String, senderName: String, date: Date = Date(), type: String = "text", payload: Any?, isIncoming: Boolean = false) : BaseMessage
        {
            lastId++
            return when(type)
            {
                "text" -> TextMessage(
                    "$lastId",
                    type,
                    senderId,
                    senderName,
                    date = date,
                    text = payload as String,
                    isIncoming = isIncoming
                )
                else -> ImageMessage(
                    "$lastId",
                    type,
                    senderId,
                    senderName,
                    date = date,
                    image = payload as String,
                    isIncoming = isIncoming
                )
            }
        }
    }
}