package com.example.myapplication.dal.repositories

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.example.myapplication.dal.room.AppDatabase
import com.example.myapplication.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await

class UserRepository(private val context: Context) {
    companion object {
        const val USERS_COLLECTION = "users"
        const val IMAGES_REF = "images"
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val localDb = AppDatabase.getDatabase(context)
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val imageRepository = ImageRepository(context)

    suspend fun saveUserInDB(user: User) {
        val newUser = user.copy()
        newUser.imageUri = null

        db.collection(USERS_COLLECTION)
            .document(newUser.id)
            .set(newUser.json)
            .await()

        localDb.userDao().insertAll(newUser)
    }

    suspend fun saveUserImage(imageUri: String, userId: String) =
        imageRepository.uploadImage(imageUri.toUri(), userId)

    suspend fun getUserById(userId: String): User {
        val user = db.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)

        user?.imageUri = getUserImageUri(userId).toString()
        user?.localImageUri = null

        return user!!
    }

    private suspend fun getUserImageUri(userId: String): Uri = imageRepository.getImageUri(userId)
}