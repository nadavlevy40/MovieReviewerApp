package com.example.myapplication.models


data class PopulatedReview(
    var id: String = "",
    val user: User,
    val title: String = "",
    val content: String = "",
    val timestamp: Long,
    val movie: Movie,
    val imageUri: String = ""
) {
    constructor(review: Review, user: User, movie: Movie) : this(
        review.id,
        user,
        review.title,
        review.content,
        review.timestamp,
        movie,
        imageUri = review.imageUri ?: ""
    )
}
