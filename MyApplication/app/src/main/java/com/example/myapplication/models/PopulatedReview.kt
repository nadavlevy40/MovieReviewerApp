package com.example.myapplication.models


data class PopulatedReview(
    var id: String = "",
    val user: User,
    val title: String = "",
    val content: String = "",
    val timestamp: Long,
    val movie: Movie
) {
    companion object {
        fun construct(review: Review, user: User, movie: Movie): PopulatedReview {
            return PopulatedReview(
                id = review.id,
                user = user,
                title = review.title,
                content = review.content,
                timestamp = review.timestamp,
                movie = movie
            )
        }
    }
}
