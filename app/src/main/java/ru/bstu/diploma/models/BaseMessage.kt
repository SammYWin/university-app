package ru.bstu.diploma.models

import com.google.firebase.firestore.DocumentId
import ru.bstu.diploma.models.data.Chat
import ru.bstu.diploma.models.data.User
import java.util.*

abstract class BaseMessage(
    @DocumentId
    val id: String,
    val senderName: String,
    val isIncoming: Boolean = true,
    val date: Date = Date(),
    var isReaded: Boolean = false
)
{
    abstract fun formatMessage() : String

    companion object AbstractFactory
    {
        var lastId = -1
        fun makeMessage(senderName: String, date: Date = Date(), type: String = "text", payload: Any?, isIncoming: Boolean = false) : BaseMessage
        {
            lastId++
            return when(type)
            {
                "image" -> ImageMessage(
                    "$lastId",
                    senderName,
                    date = date,
                    image = payload as String,
                    isIncoming = isIncoming
                )
                else -> TextMessage(
                    "$lastId",
                    senderName,
                    date = date,
                    text = payload as String,
                    isIncoming = isIncoming
                )
            }
        }
    }
}