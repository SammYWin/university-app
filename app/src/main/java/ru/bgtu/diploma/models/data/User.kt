package ru.bgtu.diploma.models.data

import ru.bgtu.diploma.extensions.humanizeDiff
import ru.bgtu.diploma.utils.Utils
import java.util.*

data class User
    (
    val id:String,
    var firstName: String?,
    var lastName: String?,
    var nickName: String?,
    var avatar: String?,
//    var rating: Int = 0,
//    var respect: Int = 0,
    val lastVisit: Date? = Date(),
    var isOnline: Boolean = false

    )
{
    constructor(id:String, firstName: String?, lastName: String?) : this
        (
        id= id,
        firstName= firstName,
        lastName= lastName,
        nickName = null,
        avatar= null
        )

    companion object Factory
    {
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
            isOnline -> "online"
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