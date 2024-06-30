package com.example.myapplication.dal.repositories

import com.example.myapplication.models.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MyReviewsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getMyReviews(): List<Review> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        val snapshot = db.collection("reviews")
            .whereEqualTo("userId", userId)
            .get()
            .await()
        return snapshot.toObjects(Review::class.java)
    }

    suspend fun deleteReview(review: Review): Boolean {
        return try {
            db.collection("reviews").document(review.id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
