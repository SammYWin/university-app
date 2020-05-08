package ru.bstu.diploma.models

import ru.bstu.diploma.extensions.humanizeDiff
import ru.bstu.diploma.models.data.User
import java.util.*

class TextMessage(
    id: String,
    senderName: String,
    isIncoming: Boolean = false,
    date: Date = Date(),
    isReaded:Boolean = false,
    var text: String?
) : BaseMessage(id, senderName, isIncoming, date, isReaded) {
    override fun formatMessage(): String = "id:$id $senderName" +
            " ${if(isIncoming) "получил" else "отправил"} сообщение \"$text\" ${date.humanizeDiff()}"

}
