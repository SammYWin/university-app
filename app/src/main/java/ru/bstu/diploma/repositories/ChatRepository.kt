package ru.bstu.diploma.repositories

import androidx.lifecycle.MutableLiveData
import ru.bstu.diploma.data.managers.CacheManager
import ru.bstu.diploma.models.data.Chat

object ChatRepository {
    private val chats = CacheManager.loadChats()

    fun loadChats() : MutableLiveData<List<Chat>>{
        return chats
    }

    fun update(chat: Chat) {
        val chatsCopy = chats.value!!.toMutableList()
        val i = chats.value!!.indexOfFirst { it.id == chat.id }
        chatsCopy[i] = chat
        chats.value = chatsCopy
    }

    fun find(id: String): Chat? {
        val i = chats.value!!.indexOfFirst { it.id == id }
        return chats.value!!.getOrNull(i)
    }
}