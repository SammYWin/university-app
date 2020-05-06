package ru.bstu.diploma.models

data class Profile(
    val firstName: String,
    val lastName: String,
    val nickName: String = "$firstName $lastName",
    val avatar: String = "",
    val about: String,
    var group: String
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