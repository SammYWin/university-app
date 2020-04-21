package ru.bgtu.diploma.viewmodels

import androidx.lifecycle.*
import ru.bgtu.diploma.extensions.mutableLiveData
import ru.bgtu.diploma.models.data.UserItem
import ru.bgtu.diploma.repositories.GroupRepository

class GroupViewModel : ViewModel(){
    private val query = mutableLiveData("")
    private val groupRepository = GroupRepository
    private val userItems = mutableLiveData(loadUsers())
    private val selectedItems = Transformations.map(userItems){users -> users.filter { it.isSelected }}

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

    fun handleCreateGroup() {
        groupRepository.createChat(selectedItems.value)
    }

    private fun loadUsers(): List<UserItem> = groupRepository.loadUsers().map{it.toUserItem()}

}