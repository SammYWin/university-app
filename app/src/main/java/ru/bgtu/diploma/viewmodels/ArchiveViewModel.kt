package ru.bgtu.diploma.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.bgtu.diploma.extensions.mutableLiveData
import ru.bgtu.diploma.models.data.ChatItem
import ru.bgtu.diploma.repositories.ChatRepository

class ArchiveViewModel : ViewModel() {
    private val query = mutableLiveData("")
    private val chatRepository = ChatRepository
    private val chats = Transformations.map(chatRepository.loadChats()){chats ->
        return@map chats
            .filter { it.isArchived }
            .map { it.toChatItem() }
            .sortedBy { it.id.toInt() }
    }

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()

        val FilterF = {
            val queryStr = query.value!!
            val resChats =  chats.value!!

            result.value = if(queryStr.isEmpty()) resChats
            else resChats.filter { it.title.contains(queryStr, true) }

        }

        result.addSource(chats){FilterF.invoke()}
        result.addSource(query){FilterF.invoke()}

        return result
    }

    fun restoreFromArchive(id: String) {
        val chat = chatRepository.find(id)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

    fun addToArchive(id: String) {
        val chat = chatRepository.find(id)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun handleSearchQuery(text: String?) {
        query.value = text
    }
}