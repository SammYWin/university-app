package ru.bstu.diploma.data.managers

import androidx.lifecycle.MutableLiveData
import ru.bstu.diploma.extensions.mutableLiveData
import ru.bstu.diploma.models.data.Chat
import ru.bstu.diploma.models.data.User
import ru.bstu.diploma.utils.DataGenerator

object CacheManager {
    private val chats = mutableLiveData(DataGenerator.stabChats)
    private val users = mutableLiveData(DataGenerator.stabUsers)

    fun loadChats() : MutableLiveData<List<Chat>>{
        return chats
    }

    fun findUsersById(ids: List<String>) : List<User> {
        return users.value!!.filter { ids.contains(it.id) }
    }

    fun nextChatId() : String{
        return "${chats.value!!.size}"
    }

    fun insertChat(chat : Chat){
        val copy = chats.value!!.toMutableList()
        copy.add(chat)
        chats.value = copy
    }
}