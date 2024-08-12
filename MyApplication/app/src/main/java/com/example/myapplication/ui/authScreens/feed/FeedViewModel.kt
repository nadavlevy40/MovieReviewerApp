package com.example.myapplication.ui.authScreens.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dal.repositories.ImageRepository
import com.example.myapplication.dal.repositories.MovieRepository
import com.example.myapplication.dal.repositories.ReviewsRepository
import com.example.myapplication.dal.repositories.UserRepository
import com.example.myapplication.models.PopulatedReview
import com.example.myapplication.models.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel(
    private val isMyReviews: Boolean,
    private val reviewsRepository: ReviewsRepository,
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository,
    private val movieRepository: MovieRepository
) : ViewModel() {
    private val _reviews = reviewsRepository.getAllCachedReviews(isMyReviews)
    val reviews: LiveData<List<Review>> get() = _reviews

    init {
        fetchReviews()
    }

    private fun fetchReviews() {
        viewModelScope.launch(Dispatchers.IO) {
            reviewsRepository.getAllReviews(isMyReviews)
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reviewsRepository.deleteReview(reviewId)
        }
    }

    fun getPopulatedReview(
        review: Review,
        onPopulatedReviewFetched: (populatedReview: PopulatedReview) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.getUserById(review.userId)
            val movie = movieRepository.getMovieById(review.movieId)
            val imageUri = imageRepository.getImagePathById(review.id)


            withContext(Dispatchers.Main) {
                onPopulatedReviewFetched(
                    PopulatedReview(
                        review.apply { this.imageUri = imageUri },
                        user,
                        movie
                    )
                )
            }
        }
    }
}