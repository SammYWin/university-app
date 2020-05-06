package ru.bstu.diploma.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.NullPointerException
import java.util.*

object StorageUtil {
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private val currentUserRef: StorageReference
        get() = storageInstance.reference
            .child(FirebaseAuth.getInstance().uid ?: throw NullPointerException("UID is null"))

    fun uploadProfileAvatar(imageBytes: ByteArray, onSuccess: (imagePath: String) -> Unit){
        val ref = currentUserRef.child("profileAvatars/${UUID.nameUUIDFromBytes(imageBytes)}")
        ref.putBytes(imageBytes)
            .addOnSuccessListener {
                val path = ref.path
                onSuccess(path)
            }
    }

    fun pathToReference(path: String) = storageInstance.getReference(path)
}