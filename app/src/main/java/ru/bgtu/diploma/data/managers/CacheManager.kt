package ru.bgtu.diploma.data.managers

import androidx.lifecycle.MutableLiveData
import ru.bgtu.diploma.extensions.mutableLiveData
import ru.bgtu.diploma.models.data.Chat
import ru.bgtu.diploma.models.data.User
import ru.bgtu.diploma.utils.DataGenerator

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