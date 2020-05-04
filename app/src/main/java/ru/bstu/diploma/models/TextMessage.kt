package ru.bstu.diploma.models

import ru.bstu.diploma.extensions.humanizeDiff
import ru.bstu.diploma.models.data.Chat
import ru.bstu.diploma.models.data.User
import java.util.*

/**
 * Created by Makweb on 24.06.2019.
 */
class TextMessage(
    id: String,
    from: User,
    chat: Chat,
    isIncoming: Boolean = false,
    date: Date = Date(),
    isReaded:Boolean = false,
    var text: String?
) : BaseMessage(id, from, chat, isIncoming, date, isReaded) {
    override fun formatMessage(): String = "id:$id ${from.firstName}" +
            " ${if(isIncoming) "получил" else "отправил"} сообщение \"$text\" ${date.humanizeDiff()}"

}
