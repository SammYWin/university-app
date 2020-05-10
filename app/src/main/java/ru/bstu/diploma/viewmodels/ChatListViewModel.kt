package ru.bstu.diploma.viewmodels

import androidx.lifecycle.*
import com.google.firebase.firestore.ListenerRegistration
import ru.bstu.diploma.extensions.mutableLiveData
import ru.bstu.diploma.models.data.Chat
import ru.bstu.diploma.models.data.ChatItem
import ru.bstu.diploma.utils.FirestoreUtil

class ChatListViewModel : ViewModel() {
    private lateinit var chatsListenerRegistration: ListenerRegistration
    private val query = mutableLiveData("")
    private val _chats = mutableLiveData(loadChats())
    private val chats = Transformations.map(_chats){ chats ->
            val archivedChats = chats
                .filter { it.isArchived }
                .map { it.toArchiveChatItem(chats) }
                .sortedBy { it.lastMessageDate }
            if(archivedChats.isEmpty()){
                return@map chats
                    .map { it.toChatItem() }
                    .sortedBy { it.lastMessageDate }
            } else{
                val chatsWithArchiveItem = mutableListOf<ChatItem>()
                chatsWithArchiveItem.add(0, archivedChats.last())
                chatsWithArchiveItem.addAll((chats.filter { !it.isArchived }.map { it.toChatItem() }))
                return@map chatsWithArchiveItem
            }
    }

    fun getChatData() : LiveData<List<ChatItem>?>{
        val result = MediatorLiveData<List<ChatItem>?>()

        val FilterF = {
            val queryStr = query.value!!
            val resChats =  chats.value

            if (resChats != null) {
                result.value = if(queryStr.isEmpty()) resChats
                else resChats.filter { it.title.contains(queryStr, true) }
            }

        }

        result.addSource(chats){FilterF.invoke()}
        result.addSource(query){FilterF.invoke()}

        return result
    }

    private fun loadChats(): List<Chat>{
        var loadedChats:  List<Chat> = listOf()
        chatsListenerRegistration = FirestoreUtil.addChatsListener {
            if (it != null) {
                loadedChats = it
            }
            _chats.value = it
        }

        return loadedChats
    }

//    fun addToArchive(id: String) {
//        val chat = chatRepository.find(id)
//        chat ?: return
//        chatRepository.update(chat.copy(isArchived = true))
//    }
//
//    fun restoreFromArchive(id: String) {
//        val chat = chatRepository.find(id)
//        chat ?: return
//        chatRepository.update(chat.copy(isArchived = false))
//    }

    fun handleSearchQuery(text: String?) {
        query.value = text
    }

    override fun onCleared() {
        super.onCleared()
       FirestoreUtil.removeListener(chatsListenerRegistration)
    }
}