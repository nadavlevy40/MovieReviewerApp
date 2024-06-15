package com.example.myapplication.dal.firebase

import androidx.core.net.toUri
import com.example.myapplication.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class UserRepository {
    companion object {
        const val USERS_COLLECTION = "users"
        const val IMAGES_REF = "images"
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    fun saveUserInDB(user: User): Task<Void> {
        return db.collection(USERS_COLLECTION)
            .document(user.id!!)
            .set(user.json)
    }

    fun saveUserImage(imageUri: String, userId: String): UploadTask {
        val imageRef = storage.reference.child("$IMAGES_REF/$userId")
        return imageRef.putFile(imageUri.toUri())
    }
}