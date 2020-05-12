package ru.bstu.diploma.models

import com.google.firebase.firestore.DocumentId
import ru.bstu.diploma.extensions.humanizeDiff
import java.util.*

class ImageMessage (
    id: String,
    type: String,
    senderId: String,
    senderFirstName: String,
    isIncoming : Boolean = false,
    date: Date = Date(),
    isReaded: Boolean = true,
    var image: String
) : BaseMessage(id, type, senderId, senderFirstName, isIncoming, date,isReaded)
{
    constructor(): this("", "", "", "",false, Date(), true, "")

    override fun formatMessage(): String = "id:$id $senderFirstName" +
            " ${if(isIncoming) "получил" else "отправил"} изображение \"$image\" ${date.humanizeDiff()}"

}