package com.example.myapplication.dal.repositories

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.myapplication.dal.room.AppDatabase
import com.example.myapplication.models.Image
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ImageRepository(private val context: Context) {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val localDb = AppDatabase.getDatabase(context)

    companion object {
        const val IMAGES_REF = "images"
    }

    suspend fun uploadImage(imageUri: Uri, imageId: String) {
        val imageRef = storage.reference.child("$IMAGES_REF/$imageId")
        imageRef.putFile(imageUri).await()

        localDb.imageDao().insertAll(Image(imageId, imageUri.toString()))
    }

    suspend fun getImageUri(imageId: String): Uri {
        val imageRef = storage.reference.child("$IMAGES_REF/$imageId")

        return downloadAndCacheImage(imageRef.downloadUrl.await(), imageId).toUri()
    }

    private suspend fun downloadAndCacheImage(uri: Uri, imageId: String): String {
        return withContext(Dispatchers.IO) {
            val file = Glide.with(context)
                .asFile()
                .load(uri)
                .submit()
                .get()

            localDb.imageDao().insertAll(Image(imageId, file.absolutePath))

            return@withContext file.absolutePath
        }
    }
}