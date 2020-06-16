package ru.bstu.diploma.utils

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import ru.bstu.diploma.App
import ru.bstu.diploma.models.BaseMessage
import ru.bstu.diploma.models.ImageMessage
import ru.bstu.diploma.models.TextMessage
import ru.bstu.diploma.models.data.Chat
import ru.bstu.diploma.models.data.ChatItem
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
                user.isOnline = it["isOnline"] as Boolean?
                onComplete(user) }
        }
    }

    fun getUserById(id: String, onComplete: (user: User) -> Unit){
        firestoreInstance.collection("user").document(id).get()
            .addOnSuccessListener {
                it.toObject(User::class.java)?.let { user ->
                    user.isOnline = it["isOnline"] as Boolean?
                    onComplete(user) }
            }
    }

    fun getUsersByIds(ids: List<String>, onComplete: (List<User>) -> Unit) {
        val users = mutableListOf<User>()
        for(id in ids)(
            firestoreInstance.collection("user").document(id).get()
                .addOnSuccessListener {
                    it.toObject(User::class.java)?.let { user ->
                        user.isOnline = it["isOnline"] as Boolean?
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
                        users.add(it.toObject(User::class.java)!!.also { user ->  user.isOnline = it["isOnline"] as Boolean? })
                }
                onListen(users)
            }
    }

    fun addChatsListener(onListen: (List<Chat>?) -> Unit): ListenerRegistration{
        return currentUserDocRef.collection("engagedChats")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null){
                    Log.e("FIRESTORE", "Chats listener error", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val chats: MutableList<Chat> = mutableListOf()

                querySnapshot?.documents?.forEach{
                    val id = it["chatId"] as String
                    val title = it["title"] as String
                    val avatar = it["avatar"] as String?
                    val isArchived = it["isArchived"] as Boolean
                    val unreadCount = it["unreadCount"] as Long?

                    val _chat = Chat(
                        id,
                        title,
                        avatar = avatar,
                        isArchived = isArchived,
                        unreadCount = unreadCount?.toInt() ?: 0
                    )

                    chatsCollectionRef.document(id).get().addOnSuccessListener{chat ->
                        val memberIds = chat["memberIds"] as List<String>

                        chatsCollectionRef.document(id).collection("messages").orderBy("date")
                            .addSnapshotListener { querySnapshotMessages, firebaseFirestoreException ->
                                if(firebaseFirestoreException != null){
                                    Log.e("FIRESTORE", "Chats listener error", firebaseFirestoreException)
                                    return@addSnapshotListener
                                }

                                querySnapshotMessages?.documents?.forEach{doc->
                                    doc.toObject(TextMessage::class.java)?.let { message ->
                                        _chat.messages.add(message)
                                    }
                                }

                                getUsersByIds(memberIds){
                                    _chat.members = it.toMutableList()
                                    chats.add(_chat)
                                    if(chats.size == querySnapshot.documents.size)
                                        onListen(chats.toList())
                                }
                            }
                    }
                }
            }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

    fun getOrCreateChat(_ids: List<String>, onComplete: (chatId: String) -> Unit){
        val ids = _ids.toMutableList()
        if(!ids.contains(FirebaseAuth.getInstance().currentUser!!.uid))
            ids.add(FirebaseAuth.getInstance().currentUser!!.uid)

        chatsCollectionRef.get().addOnSuccessListener {
                for(doc in it.documents){
                    val memberIds = doc["memberIds"] as List<String>
                    if(memberIds.toSet() == ids.toSet()){
                        onComplete(doc.id)
                        return@addOnSuccessListener
                    }
                }

                getUsersByIds(ids){ members ->
                    val title = if(members.size > 2)
                        members.map { it.firstName }.joinToString(", ")
                    else
                        members.find { user -> user.id != FirebaseAuth.getInstance().currentUser!!.uid }!!.nickName!!

                    val avatar = if(members.size > 2) null
                    else members.find { user -> user.id != FirebaseAuth.getInstance().currentUser!!.uid }!!.avatar

                    val baseMsgs = mutableListOf<BaseMessage>()
                    val newChat = Chat(
                        "",
                        title,
                        avatar,
                        members.toMutableList(),
                        baseMsgs
                    )

                    val newChatRef = chatsCollectionRef.document()
                    newChatRef.set(mapOf("memberIds" to ids))

                    currentUserDocRef.collection("engagedChats").document()
                        .set(mapOf(
                            "chatId" to newChatRef.id,
                            "title" to newChat.title,
                            "avatar" to avatar,
                            "isArchived" to newChat.isArchived,
                            "unreadCount" to 0
                        ))

                    //setting engagedChats for other users in the chat
                    for(member in members){
                        if(member.id != FirebaseAuth.getInstance().currentUser!!.uid) {
                            val _title = if(members.size > 2)
                                members.map { it.firstName }.joinToString(", ")
                            else
                                members.find { user -> user.id != member.id }!!.nickName!!

                            val _avatar = if(members.size > 2) null
                            else members.find { user -> user.id != member.id }!!.avatar

                            firestoreInstance.collection("user").document(member.id)
                                .collection("engagedChats")
                                .document()
                                .set(
                                    mapOf(
                                        "chatId" to newChatRef.id,
                                        "title" to _title,
                                        "avatar" to _avatar,
                                        "isArchived" to newChat.isArchived,
                                        "unreadCount" to 0
                                    )
                                )
                        }
                    }

                    onComplete(newChatRef.id)

            }
        }
    }

    fun addChatMessagesListener(chatId: String, onListen: (List<BaseMessage>) -> Unit): ListenerRegistration{
        return chatsCollectionRef.document(chatId).collection("messages").orderBy("date")
            .addSnapshotListener{ querrySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null){
                    Log.d("M_FirestoreUtil", "ChatMessages Listener exception", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val messages = mutableListOf<BaseMessage>()
                querrySnapshot?.documents?.forEach {
                    if (it["type"] == "text")
                        it.toObject(TextMessage::class.java)?.let { textMessage -> messages.add(textMessage) }
                    else
                        it.toObject(ImageMessage::class.java)?.let { imageMessage -> messages.add(imageMessage) }
                }

                onListen(messages)
            }
    }

    fun sendMessage(message: BaseMessage, chatId: String, onComplete: () -> Unit){
        chatsCollectionRef.document(chatId)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                onComplete()
            }

        chatsCollectionRef.document(chatId).get().addOnSuccessListener {
            val memberIds = it["memberIds"] as MutableList<String>

            memberIds.forEach{ id ->
                if(id != FirebaseAuth.getInstance().currentUser!!.uid) {
                    firestoreInstance.collection("user")
                        .document(id).collection("engagedChats").whereEqualTo("chatId", chatId)
                        .get().addOnSuccessListener {q->
                            firestoreInstance.collection("user")
                                .document(id).collection("engagedChats").document(q.documents[0].id)
                                .update("unreadCount", FieldValue.increment(1))
                        }
                }
            }
        }

    }

    fun updateUnreadCount(chatId: String) {
        firestoreInstance.collection("user")
            .document(FirebaseAuth.getInstance().currentUser!!.uid).collection("engagedChats")
            .whereEqualTo("chatId", chatId).get().addOnSuccessListener {
                firestoreInstance.collection("user")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .collection("engagedChats").document(it.documents[0].id)
                    .update("unreadCount", 0)
            }
    }

    fun addMembersToChat(chatItem: ChatItem, newMembers: List<UserItem>) {
        val ids = newMembers.map { it.id }.toMutableList()

        chatsCollectionRef.document(chatItem.id).get().addOnSuccessListener {chat ->
            val memberIds = chat["memberIds"] as List<String>
            for(member in memberIds){
                if(ids.contains(member))
                    ids.remove(member)
            }

            val newTitle = chatItem.title + ", " + newMembers.filter { member -> ids.contains(member.id) }
                .map { Utils.parseFullName(it.fullName).first }.joinToString(", ")

            for(id in ids) {
                firestoreInstance.collection("user").document(id)
                    .collection("engagedChats")
                    .document()
                    .set(
                        mapOf(
                            "chatId" to chatItem.id,
                            "title" to newTitle,
                            "avatar" to chatItem.avatar,
                            "isArchived" to false,
                            "unreadCount" to 0
                        )
                    )
            }
            ids.forEach { id-> chatsCollectionRef.document(chatItem.id).update("memberIds", FieldValue.arrayUnion(id)) }
        }
    }

    fun exitGroupChat(chatId: String) {
        firestoreInstance.collection("user").document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("engagedChats").whereEqualTo("chatId", chatId).get().addOnSuccessListener {
                it.documents[0].reference.delete()
            }

        chatsCollectionRef.document(chatId).update("memberIds", FieldValue.arrayRemove(
            FirebaseAuth.getInstance().currentUser!!.uid))
    }

    fun loadUserDataFromChat(chatId: String, onComplete: (user: User) -> Unit) {
        chatsCollectionRef.document(chatId).get().addOnSuccessListener {chat ->
            val memberIds = chat["memberIds"] as List<String>
            memberIds.forEach {
                if(it != FirebaseAuth.getInstance().currentUser!!.uid)
                    getUserById(it){user -> onComplete(user)}
            }
        }
    }

    fun getChatById(chatId: String, onComplete: (chat: Chat) -> Unit) {
        firestoreInstance.collection("user").document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("engagedChats").whereEqualTo("chatId", chatId).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                querySnapshot!!.forEach{
                    val id = it["chatId"] as String
                    val title = it["title"] as String
                    val avatar = it["avatar"] as String?
                    val isArchived = it["isArchived"] as Boolean
                    val unreadCount = it["unreadCount"] as Long?

                    val _chat = Chat(
                        id,
                        title,
                        avatar = avatar,
                        isArchived = isArchived,
                        unreadCount = unreadCount?.toInt() ?: 0
                    )

                    chatsCollectionRef.document(id).get().addOnSuccessListener{chat ->
                        val memberIds = chat["memberIds"] as List<String>

                        chatsCollectionRef.document(id).collection("messages").orderBy("date")
                            .addSnapshotListener { querySnapshotMessages, firebaseFirestoreException ->
                                if(firebaseFirestoreException != null){
                                    Log.e("FIRESTORE", "Get chat by id error", firebaseFirestoreException)
                                    return@addSnapshotListener
                                }

                                querySnapshotMessages?.documents?.forEach{doc->
                                    doc.toObject(TextMessage::class.java)?.let { message ->
                                        _chat.messages.add(message)
                                    }
                                }

                                getUsersByIds(memberIds){
                                    _chat.members = it.toMutableList()
                                    onComplete(_chat)
                                }
                            }
                    }
                }
            }
    }
}