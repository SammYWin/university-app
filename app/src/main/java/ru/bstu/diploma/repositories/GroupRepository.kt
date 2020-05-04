package ru.bstu.diploma.repositories

import ru.bstu.diploma.data.managers.CacheManager
import ru.bstu.diploma.models.data.Chat
import ru.bstu.diploma.models.data.User
import ru.bstu.diploma.models.data.UserItem
import ru.bstu.diploma.utils.DataGenerator

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