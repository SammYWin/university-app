package ru.bgtu.diploma.models

import ru.bgtu.diploma.utils.Utils

data class Profile(
    val rating: Int = 0,
    val respect: Int = 0,
    val firstName: String,
    val lastName: String,
    val about: String,
    val repository: String
)
{
    val nickName: String = Utils.transliteration("$firstName $lastName","_")

    val rank: String = "Junior Android Developer"

    fun toMap(): Map<String, Any> = mapOf(
        "nickName" to nickName,
        "rank" to rank,
        "rating" to rating,
        "respect" to respect,
        "firstName" to firstName,
        "lastName" to lastName,
        "about" to about,
        "repository" to repository
    )
}