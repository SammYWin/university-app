package ru.bstu.diploma.extensions

import ru.bstu.diploma.models.data.User
import ru.bstu.diploma.models.UserView
import ru.bstu.diploma.utils.Utils


fun User.toUserView() : UserView
{
    val nickName = Utils.transliteration("$firstName $lastName")
    val initials = Utils.toInitials(firstName, lastName)
    val status = if(lastVisit == null) "Ещё ни разу не был" else if(isOnline == true) "Online" else "Последний раз был ${lastVisit.humanizeDiff()}"

    return UserView(
        id,
        fullName = "$firstName $lastName",
        avatar = avatar,
        nickName =  nickName ,
        initials =  initials ,
        status = status
    )
}


