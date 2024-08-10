package com.example.myapplication.ui.authScreens.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dal.repositories.ReviewsRepository
import com.example.myapplication.models.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel(private val reviewsRepository: ReviewsRepository) : ViewModel() {
    private val _reviews = reviewsRepository.getAllCachedReviews()
    val reviews: LiveData<List<Review>> get() = _reviews

    init {
        fetchReviews()
    }

    private fun fetchReviews() {
        viewModelScope.launch(Dispatchers.IO) {
            reviewsRepository.getAllReviews()
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reviewsRepository.deleteReview(reviewId)
        }
    }
}