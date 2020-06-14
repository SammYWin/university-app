package ru.bstu.diploma.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.bstu.diploma.extensions.mutableLiveData
import ru.bstu.diploma.models.data.User
import ru.bstu.diploma.utils.FirestoreUtil

class ProfileInfoViewModel(_chatId: String?, _userId: String?): ViewModel() {
    private val chatId = _chatId
    private val userId = _userId
    private val userInfo = mutableLiveData(loadUserData())

    fun getUserInfo(): LiveData<User> = userInfo

    private fun loadUserData(): User {
        var user = User()
        if(userId == null) {
            FirestoreUtil.loadUserDataFromChat(chatId!!) {
                user = it
                userInfo.value = it
            }
        }
        else{
            FirestoreUtil.getUserById(userId) {
                user = it
                userInfo.value = it
            }
        }
        return user
    }
}