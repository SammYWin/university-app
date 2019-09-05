package ru.skillbranch.devintensive.models.data

import ru.skillbranch.devintensive.extensions.humanizeDiff
import ru.skillbranch.devintensive.models.data.BaseMessage
import java.util.*

/**
 * Created by Makweb on 24.06.2019.
 */
class ImageMessage (
    id:String,
    from: User,
    chat: Chat,
    isIncoming : Boolean = false,
    date: Date = Date(),
    isReaded:Boolean = false,
    var image:String
) : BaseMessage(id, from, chat, isIncoming, date,isReaded)
{
    override fun formatMessage(): String = "id:$id ${from.firstName}" +
            " ${if(isIncoming) "получил" else "отправил"} изображение \"$image\" ${date.humanizeDiff()}"

}