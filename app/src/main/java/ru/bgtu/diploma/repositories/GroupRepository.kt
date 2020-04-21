package ru.bgtu.diploma.repositories

import ru.bgtu.diploma.data.managers.CacheManager
import ru.bgtu.diploma.models.data.Chat
import ru.bgtu.diploma.models.data.User
import ru.bgtu.diploma.models.data.UserItem
import ru.bgtu.diploma.utils.DataGenerator

object GroupRepository {
    fun loadUsers(): List<User> = DataGenerator.stabUsers
    fun createChat(items: List<UserItem>?) {
        val ids = items!!.map { it.id }
        val users = CacheManager.findUsersById(ids)
        val title = users.map { it.firstName }.joinToString ( ", " )
        val newChat = Chat(CacheManager.nextChatId(),title, users)
        CacheManager.insertChat(newChat)
    }
}