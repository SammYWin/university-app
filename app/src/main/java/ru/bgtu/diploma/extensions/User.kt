package ru.bgtu.diploma.extensions

import ru.bgtu.diploma.models.data.User
import ru.bgtu.diploma.models.UserView
import ru.bgtu.diploma.utils.Utils


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


