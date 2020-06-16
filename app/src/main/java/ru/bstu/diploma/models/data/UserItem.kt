package ru.bstu.diploma.models.data


data class UserItem (
    val id: String,
    var fullName: String,
    val initials : String?,
    val avatar: String?,
    val group: String?,
    var lastActivity:String,
    var isSelected : Boolean = false,
    var isOnline: Boolean? = false,
    val isProfessor: Boolean? = false,
    val isGroupLeader: Boolean? = false
)