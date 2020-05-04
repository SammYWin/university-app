package ru.bstu.diploma.models

import ru.bstu.diploma.models.data.Chat
import ru.bstu.diploma.models.data.User
import java.util.*

/**
 * Created by Makweb on 24.06.2019.
 */
abstract class BaseMessage(
    val id: String,
    val from: User,
    val chat: Chat,
    val isIncoming: Boolean = true,
    val date: Date = Date(),
    var isReaded: Boolean = false

)
{
    abstract fun formatMessage() : String

    companion object AbstractFactory
    {
        var lastId = -1
        fun makeMessage(from: User, chat: Chat, date: Date = Date(), type: String = "text", payload: Any?, isIncoming: Boolean = false) : BaseMessage
        {
            lastId++
            return when(type)
            {
                "image" -> ImageMessage(
                    "$lastId",
                    from,
                    chat,
                    date = date,
                    image = payload as String,
                    isIncoming = isIncoming
                )
                else -> TextMessage(
                    "$lastId",
                    from,
                    chat,
                    date = date,
                    text = payload as String,
                    isIncoming = isIncoming
                )
            }
        }
    }
}