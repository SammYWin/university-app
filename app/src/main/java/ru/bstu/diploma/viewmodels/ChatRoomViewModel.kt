package ru.bstu.diploma.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import ru.bstu.diploma.extensions.mutableLiveData
import ru.bstu.diploma.models.BaseMessage
import ru.bstu.diploma.models.TextMessage
import ru.bstu.diploma.models.data.ChatItem
import ru.bstu.diploma.utils.FirestoreUtil

class ChatRoomViewModel(chatItem: ChatItem): ViewModel() {
    private val chat = chatItem
    private val messages = mutableLiveData(loadMessages())
    private val isMessageSend = MutableLiveData<Boolean>()
    private lateinit var messagesListenerRegistration: ListenerRegistration

    fun getMessagesData(): LiveData<List<BaseMessage>> = messages

    fun getIsMessageSend(): LiveData<Boolean> = isMessageSend

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
            payload = text
        )
        FirestoreUtil.sendMessage(newMessage, chat.id){
            isMessageSend.value = true
        }
    }

    fun reloadIsMessageSend(){ isMessageSend.value = false }

}