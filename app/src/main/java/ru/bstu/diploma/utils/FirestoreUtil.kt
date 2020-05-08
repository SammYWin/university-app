package ru.bstu.diploma.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import ru.bstu.diploma.models.BaseMessage
import ru.bstu.diploma.models.ImageMessage
import ru.bstu.diploma.models.TextMessage
import ru.bstu.diploma.models.data.Chat
import ru.bstu.diploma.models.data.User
import ru.bstu.diploma.models.data.UserItem
import java.lang.NullPointerException

object FirestoreUtil {
    private val firestoreInstance by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("user/${FirebaseAuth.getInstance().uid
                                            ?: throw NullPointerException("UID is null")}")

    private val chatsCollectionRef = firestoreInstance.collection("chats")

    fun initCurrentUserIfFirstTime(user: User, onComplete: () -> Unit){
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if(!documentSnapshot.exists()){
                val newUser = user
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }else{
                onComplete()
            }
        }
    }

    fun updateCurrentUser(user: User){
        val userFieldMap = mutableMapOf<String, Any>()
        if(user.firstName != null) userFieldMap["firstName"] = user.firstName!!
        if(user.lastName != null) userFieldMap["lastName"] = user.lastName!!
        if(user.nickName != null) userFieldMap["nickName"] = user.nickName!!
        if(user.about != null) userFieldMap["about"] = user.about!!
        if(user.avatar != null) userFieldMap["avatar"] = user.avatar!!
        if(user.group != null) userFieldMap["group"] = user.group!!
        if(user.lastVisit != null) userFieldMap["lastVisit"] = user.lastVisit
        if(user.isOnline != null) userFieldMap["isOnline"] = user.isOnline!!
        if(user.isProfessor != null) userFieldMap["isProfessor"] = user.isProfessor!!
        if(user.isGroupLeader != null) userFieldMap["isGroupLeader"] = user.isGroupLeader!!
        if(user.isActivated != null) userFieldMap["isActivated"] = user.isActivated!!

        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit){
        currentUserDocRef.get().addOnSuccessListener {
            it.toObject(User::class.java)?.let { user ->
                onComplete(user) }
        }
    }

    fun getUsersByIds(ids: List<String>, onComplete: (List<User>) -> Unit) {
        val users = mutableListOf<User>()
        for(id in ids)(
            firestoreInstance.collection("user").document(id).get()
                .addOnSuccessListener {
                    it.toObject(User::class.java)?.let { user ->
                        users.add(user)
                        if(users.size == ids.size)
                            onComplete(users)
                    }
                })
    }


    fun addUsersListener(onListen: (List<User>) -> Unit): ListenerRegistration{
        return firestoreInstance.collection("user")
            .addSnapshotListener{querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null){
                    Log.e("FIRESTORE", "Users listener error", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val users = mutableListOf<User>()
                querySnapshot?.documents?.forEach {
                    if(it.id != FirebaseAuth.getInstance().currentUser?.uid)
                        users.add(it.toObject(User::class.java)!!)
                }
                onListen(users)
            }
    }
    fun removeListener(registration: ListenerRegistration) = registration.remove()

    fun createChat(items: List<UserItem>?, onComplete: () -> Unit){
        val ids = items!!.map { it.id }
        getUsersByIds(ids){
            val users = it
            val title = if(it.size > 1) {
                users.map { it.firstName }.joinToString(", ")
            }else users.first().nickName!!

            val baseMsgs = mutableListOf<BaseMessage>()

            val _newChat = Chat("", title, users, baseMsgs)
            val newChat = hashMapOf(
                "title" to _newChat.title,
                "isArchived" to _newChat.isArchived
            )

            val newChatRef = chatsCollectionRef.document()
                newChatRef.set(newChat)
            for(member in users){
                newChatRef.collection("members").document().set(member)
            }
            for(msg in baseMsgs){
                newChatRef.collection("messages").document().set(msg)
            }

        }

    }

}