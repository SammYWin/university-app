package ru.bstu.diploma.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProfileInfoViewModelFactory(private val chatId: String?, private val userId: String?): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileInfoViewModel::class.java)) {
            return ProfileInfoViewModel(chatId, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}