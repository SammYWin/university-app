package ru.bstu.diploma.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import ru.bstu.diploma.extensions.mutableLiveData
import ru.bstu.diploma.models.BaseMessage
import ru.bstu.diploma.models.data.ChatItem
import ru.bstu.diploma.repositories.PreferencesRepository
import ru.bstu.diploma.utils.FirestoreUtil

class ChatRoomViewModel(chatItem: ChatItem): ViewModel() {
    private val chat = chatItem
    private val messages = mutableLiveData(loadMessages())
    private val isMessageSent = MutableLiveData<Boolean>()
    private lateinit var messagesListenerRegistration: ListenerRegistration

    fun getMessagesData(): LiveData<List<BaseMessage>> = messages

    fun getIsMessageSent(): LiveData<Boolean> = isMessageSent

    private fun loadMessages(): List<BaseMessage> {
        var _messages = listOf<BaseMessage>()
        messagesListenerRegistration = FirestoreUtil.addChatMessagesListener(chat.id){
            _messages = it
            messages.value = it
        }

        return _messages
    }

    fun handleSendMessage(text: String){
        val newMessage = BaseMessage.makeMessage(
            FirebaseAuth.getInstance().currentUser!!.uid,
            senderName = PreferencesRepository.getProfileName(),
            payload = text
        )
        FirestoreUtil.sendMessage(newMessage, chat.id){
            isMessageSent.value = true
        }
    }

    fun reloadIsMessageSent(){ isMessageSent.value = false }

    fun handleExitGroupChat(chatId: String) {
        FirestoreUtil.exitGroupChat(chatId)
    }

}