package ru.bgtu.diploma.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.bgtu.diploma.extensions.mutableLiveData
import ru.bgtu.diploma.models.data.ChatItem
import ru.bgtu.diploma.repositories.ChatRepository

class MainViewModel : ViewModel() {
    private val query = mutableLiveData("")
    private val chatRepository = ChatRepository
    private val chats = Transformations.map(chatRepository.loadChats()){ chats->
            val archivedChats = chats
                .filter { it.isArchived }
                .map { it.toArchiveChatItem(chats) }
                .sortedBy { it.lastMessageDate }
            if(archivedChats.isEmpty()){
                return@map chats
                    .map { it.toChatItem() }
                    .sortedBy { it.id.toInt() }
            } else{
                val chatsWithArchiveItem = mutableListOf<ChatItem>()
                chatsWithArchiveItem.add(0, archivedChats.last())
                chatsWithArchiveItem.addAll((chats.filter { !it.isArchived }.map { it.toChatItem() }))
                return@map chatsWithArchiveItem
            }
    }

    fun getChatData() : LiveData<List<ChatItem>>{
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

    fun addToArchive(id: String) {
        val chat = chatRepository.find(id)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(id: String) {
        val chat = chatRepository.find(id)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

    fun handleSearchQuery(text: String?) {
        query.value = text
    }
}