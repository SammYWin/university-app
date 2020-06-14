package ru.bstu.diploma.viewmodels

import androidx.lifecycle.*
import com.google.firebase.firestore.ListenerRegistration
import ru.bstu.diploma.extensions.mutableLiveData
import ru.bstu.diploma.models.data.ChatItem
import ru.bstu.diploma.models.data.UserItem
import ru.bstu.diploma.utils.FirestoreUtil

class UsersViewModel : ViewModel(){
    private val query = mutableLiveData("")
    private val userItems = mutableLiveData(loadUsers())
    private val selectedItems = Transformations.map(userItems){users -> users.filter { it.isSelected }}

    private lateinit var userListenerRegistration: ListenerRegistration

    fun getUsersData() : LiveData<List<UserItem>>{
        val result = MediatorLiveData<List<UserItem>>()

        val FilterF = {
            val queryStr = query.value!!
            val users = userItems.value!!

            result.value = if(queryStr.isEmpty()) users
                            else users.filter { it.fullName.contains(queryStr, true) }
        }

        result.addSource(userItems){FilterF.invoke()}
        result.addSource(query){FilterF.invoke()}

        return result
    }

    fun getSelectedData() : LiveData<List<UserItem>> = selectedItems

    fun handleSelectedItem(userId: String){
        userItems.value = userItems.value!!.map {
            if(it.id == userId)
                it.copy(isSelected = !it.isSelected)
            else it
        }

    }

    fun handleRemoveChip(userId: String) {
        userItems.value = userItems.value!!.map {
            if (it.id == userId)
                it.copy(isSelected = false)
            else it
        }
    }

    fun handleSearchQuery(text: String?) {
        query.value = text
    }

    fun handleCreateChat() {
        FirestoreUtil.getOrCreateChat(selectedItems.value!!.map { it.id }){}
    }

    fun handleAddMembersToChat(chatItem: ChatItem) {
        FirestoreUtil.addMembersToChat(chatItem, selectedItems.value!!)
    }

    private fun loadUsers(): List<UserItem> {
        var _userItems = listOf<UserItem>()
        userListenerRegistration = FirestoreUtil.addUsersListener{
            _userItems =  it.map { it.toUserItem() }
            userItems.value = _userItems
        }

        return _userItems
    }

    override fun onCleared() {
        super.onCleared()
        FirestoreUtil.removeListener(userListenerRegistration)
    }

}