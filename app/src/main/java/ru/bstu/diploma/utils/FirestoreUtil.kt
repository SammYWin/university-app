package ru.bstu.diploma.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import ru.bstu.diploma.models.data.User
import java.lang.NullPointerException

object FirestoreUtil {
    private val firestoreInstance by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("user/${FirebaseAuth.getInstance().uid 
                                            ?: throw NullPointerException("UID is null")}")

    fun initCurrentUserIfFirstTime(user: User, onComplete: () -> Unit){
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if(!documentSnapshot.exists()){
                //user.nickName = FirebaseAuth.getInstance().currentUser?.displayName
                val newUser = user
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }else{
                onComplete()
            }
        }
    }

    fun updateCurrentUser(user: User, onComplete: () -> Unit){
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

        currentUserDocRef.update(userFieldMap).addOnSuccessListener {
            onComplete()
        }
    }

    fun getCurrentUser(onComplete: (User) -> Unit){
        currentUserDocRef.get().addOnSuccessListener {
            it.toObject(User::class.java)?.let { user ->
                onComplete(user) }
        }
    }
}