package ru.bgtu.diploma.models.data


data class UserItem (
    val id: String,
    val fullName: String,
    val initials : String?,
    val avatar: String?,
    var lastActivity:String,
    var isSelected : Boolean = false,
    var isOnline: Boolean? = false
)