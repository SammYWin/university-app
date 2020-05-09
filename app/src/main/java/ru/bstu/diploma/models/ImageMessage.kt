package ru.bstu.diploma.models

import ru.bstu.diploma.extensions.humanizeDiff
import ru.bstu.diploma.models.data.User
import java.util.*

class ImageMessage (
    id: String,
    type: String,
    senderName: String,
    isIncoming : Boolean = false,
    date: Date = Date(),
    isReaded: Boolean = false,
    var image: String
) : BaseMessage(id, type, senderName, isIncoming, date,isReaded)
{
    override fun formatMessage(): String = "id:$id $senderName" +
            " ${if(isIncoming) "получил" else "отправил"} изображение \"$image\" ${date.humanizeDiff()}"

}