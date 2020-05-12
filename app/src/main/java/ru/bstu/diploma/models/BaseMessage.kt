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
    val isIncoming: Boolean = true,
    val date: Date = Date(),
    var isReaded: Boolean = true
)
{
    abstract fun formatMessage() : String

    companion object AbstractFactory
    {
        var lastId = -1
        fun makeMessage(senderId: String, date: Date = Date(), type: String = "text", payload: Any?, isIncoming: Boolean = false) : BaseMessage
        {
            lastId++
            return when(type)
            {
                "text" -> TextMessage(
                    "$lastId",
                    type,
                    senderId,
                    date = date,
                    text = payload as String,
                    isIncoming = isIncoming
                )
                else -> ImageMessage(
                    "$lastId",
                    type,
                    senderId,
                    date = date,
                    image = payload as String,
                    isIncoming = isIncoming
                )
            }
        }
    }
}