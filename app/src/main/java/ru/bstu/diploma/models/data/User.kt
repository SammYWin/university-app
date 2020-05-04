package ru.bstu.diploma.models.data

import com.google.firebase.firestore.DocumentId
import ru.bstu.diploma.extensions.humanizeDiff
import ru.bstu.diploma.utils.Utils
import java.util.*

data class User
    (
    @DocumentId
    val id:String,
    var firstName: String?,
    var lastName: String?,
    var nickName: String?,
    var about: String?,
    var avatar: String?,
    var group: String?,
    val lastVisit: Date? = Date(),
    var isOnline: Boolean? = false,
    var isProfessor: Boolean? = false,
    var isGroupLeader: Boolean? = false,
    var isActivated: Boolean? = true
    )
{
    constructor(): this(
        "", null, null, null, null,null, null, null, null, null, null, null
    )

    constructor(id: String, firstName: String?, lastName: String?) : this(
        id= id,
        firstName= firstName,
        lastName= lastName,
        nickName = "$firstName $lastName",
        about = null,
        avatar= null,
        group = null
    )

    companion object Factory {
        private var lastId : Int = -1
        fun makeUser(fullName : String?) : User
        {
            lastId++
            val newFullName : String? = fullName?.trim()
            val(firstName, lastName) = Utils.parseFullName(newFullName)
            return User(
                id = "$lastId",
                firstName = firstName,
                lastName = lastName
            )
        }
    }

    fun toUserItem(): UserItem {
        val lastActivity = when{
            lastVisit == null -> "Ещё ни разу не заходил"
            isOnline == true -> "online"
            else -> "Последний раз был ${lastVisit.humanizeDiff()}"
        }

        return UserItem(
            id,
            "${firstName.orEmpty()} ${lastName.orEmpty()}",
            Utils.toInitials(firstName, lastName),
            avatar,
            lastActivity,
            false,
            isOnline
        )
    }

}