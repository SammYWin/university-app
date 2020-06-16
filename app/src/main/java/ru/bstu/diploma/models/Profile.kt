package ru.bstu.diploma.models

data class Profile(
    val firstName: String,
    val lastName: String,
    val nickName: String = "$firstName $lastName",
    val avatar: String = "",
    val about: String,
    var group: String,
    val isGroupLeader: Boolean? = false,
    val isProfessor: Boolean? = false
)
{
    fun toMap(): Map<String, Any> = mapOf(
        "firstName" to firstName,
        "lastName" to lastName,
        "nickName" to nickName,
        "about" to about,
        "group" to group
    )
}