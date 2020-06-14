package ru.bstu.diploma.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import ru.bstu.diploma.extensions.mutableLiveData
import ru.bstu.diploma.models.data.Chat
import ru.bstu.diploma.models.data.User
import ru.bstu.diploma.utils.FirestoreUtil

class ProfileInfoViewModel(_chatId: String?, _userId: String?): ViewModel() {
    private val chatId = _chatId
    private val userId = _userId
    private val userInfo = mutableLiveData(loadUserData())
    private val chat = mutableLiveData<Chat>()

    fun getUserInfo(): LiveData<User> = userInfo
    fun getChat(): LiveData<Chat> = chat

    private fun loadUserData(): User {
        var user = User()
        if(chatId != null) {
            FirestoreUtil.loadUserDataFromChat(chatId) {
                user = it
                userInfo.value = it
            }
        }
        else{
            FirestoreUtil.getUserById(userId!!) {
                user = it
                userInfo.value = it
            }
        }
        return user
    }

    fun handleGetOrCreateChat() {
        FirestoreUtil.getOrCreateChat(listOf(userId!!, FirebaseAuth.getInstance().currentUser!!.uid)){
            FirestoreUtil.getChatById(it){_chat ->
                chat.value = _chat
            }
        }
    }
}