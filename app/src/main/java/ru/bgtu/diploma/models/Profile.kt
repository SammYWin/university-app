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
    val fullName: String = Utils.transliteration("$firstName $lastName")

    val rank: String = "16-ИВТ-1"

    fun toMap(): Map<String, Any> = mapOf(
        "nickName" to fullName,
        "rank" to rank,
        "rating" to rating,
        "respect" to respect,
        "firstName" to firstName,
        "lastName" to lastName,
        "about" to about,
        "repository" to repository
    )
}