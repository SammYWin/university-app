package ru.bstu.diploma.models

import ru.bstu.diploma.extensions.humanizeDiff
import java.util.*

class TextMessage(
    id: String,
    type: String,
    senderId: String,
    isIncoming: Boolean = false,
    date: Date = Date(),
    isReaded: Boolean = true,
    var text: String?
) : BaseMessage(id, type, senderId, isIncoming, date, isReaded) {
    constructor(): this("", "", "", false, Date(), true, "")

    override fun formatMessage(): String = "id:$id $senderId" +
            " ${if(isIncoming) "получил" else "отправил"} сообщение \"$text\" ${date.humanizeDiff()}"

}
