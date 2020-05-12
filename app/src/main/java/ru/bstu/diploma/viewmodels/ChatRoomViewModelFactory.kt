package ru.bstu.diploma.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.bstu.diploma.models.data.ChatItem

class ChatRoomViewModelFactory(private val chatItem: ChatItem): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatRoomViewModel::class.java)) {
            return ChatRoomViewModel(chatItem) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}