package ru.bgtu.diploma.models.data

import ru.bgtu.diploma.extensions.humanizeDiff
import ru.bgtu.diploma.utils.Utils
import java.util.*

data class User
    (
    val id:String,
    var firstName: String?,
    var lastName: String?,
    var avatar: String?,
    var rating: Int = 0,
    var respect: Int = 0,
    val lastVisit: Date? = Date(),
    var isOnline: Boolean = false

    )
{
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

    constructor(id:String, firstName: String?, lastName: String?) : this
        (
        id= id,
        firstName= firstName,
        lastName= lastName,
        avatar= null
        )

    constructor(id: String) : this
        ( id, "John", "Doe" )

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


    class Builder()
    {
        companion object Builder
        {
            private var lastId: Int = Factory.lastId

            private var id: String = "${++lastId}"
            private var firstName: String? = null
            private var lastName: String? = null
            private var avatar: String? = null
            private var rating: Int = 0
            private var respect: Int = 0
            private var lastVisit: Date? = Date()
            private var isOnline: Boolean = false
        }

        fun id(value: String) = apply { id = value }
        fun firstName(value: String?) = apply { firstName = value }
        fun lastName(value: String?) = apply { lastName = value }
        fun avatar(value: String?) = apply { avatar = value }
        fun rating(value: Int) = apply { rating = value }
        fun respect(value: Int) = apply { respect = value }
        fun lastVisit(value: Date?) = apply { lastVisit = value }
        fun isOnline(value: Boolean) = apply { isOnline = value }

        fun build() : User = User(
            id,
            firstName,
            lastName,
            avatar,
            rating,
            respect,
            lastVisit,
            isOnline
        )
    }
}