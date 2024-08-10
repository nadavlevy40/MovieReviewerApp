package com.example.myapplication.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.dal.repositories.ImageRepository
import com.example.myapplication.dal.repositories.MovieRepository
import com.example.myapplication.dal.repositories.UserRepository
import com.example.myapplication.models.Review
import com.example.myapplication.ui.authScreens.feed.FeedDirections
import com.example.myapplication.ui.authScreens.feed.FeedViewModel
import com.example.myapplication.ui.authScreens.movies.MoviesFragmentDirections
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewCardAdapter(
    private var reviews: List<Review>,
    private val movieRepository: MovieRepository,
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository,
    private val feedViewModel: FeedViewModel
) :
    RecyclerView.Adapter<ReviewCardAdapter.ReviewCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_card, parent, false)
        return ReviewCardViewHolder(
            view,
            userRepository,
            movieRepository,
            imageRepository,
            feedViewModel
        )
    }

    override fun onBindViewHolder(holder: ReviewCardViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size

    fun updateReviews(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }

    class ReviewCardViewHolder(
        itemView: View,
        private val userRepository: UserRepository,
        private val movieRepository: MovieRepository,
        private val imageRepository: ImageRepository,
        private val feedViewModel: FeedViewModel
    ) : RecyclerView.ViewHolder(itemView) {
        private val reviewTitle: TextView = itemView.findViewById(R.id.reviewTitle)
        private val timestamp: TextView = itemView.findViewById(R.id.timestamp)
        private val reviewContent: TextView = itemView.findViewById(R.id.reviewContent)
        private val reviewUser: TextView = itemView.findViewById(R.id.reviewUser)
        private val reviewMovie: TextView = itemView.findViewById(R.id.reviewMovie)
        private val reviewImage: ImageView = itemView.findViewById(R.id.reviewImage)
        private var reviewImageUri: String = ""
        private val progressBar: View = itemView.findViewById(R.id.progress_bar)
        private val deleteReviewButton: View = itemView.findViewById(R.id.deleteReviewButton)
        private val editReviewButton: View = itemView.findViewById(R.id.editReviewButton)

        fun bind(review: Review) {
            setupDeleteReviewButton(review.id)
            setupEditReviewButton(review.id, review.movieId)
            setFieldsVisibility(View.GONE, review.userId)
            progressBar.visibility = View.VISIBLE
            reviewTitle.text = review.title
            reviewContent.text = review.content
            timestamp.text = convertMillisToDateString(review.timestamp)

            CoroutineScope(Dispatchers.IO).launch {
                val user = userRepository.getUserById(review.userId)
                val movie = movieRepository.getMovieById(review.movieId)
                reviewImageUri = imageRepository.getImagePathById(review.id)

                withContext(Dispatchers.Main) {
                    reviewUser.text = "${user.firstName} ${user.lastName}"
                    reviewMovie.text = "Movie: ${movie.title}"
                    Glide.with(itemView.context)
                        .load(reviewImageUri)
                        .into(reviewImage)
                    progressBar.visibility = View.GONE
                    setFieldsVisibility(View.VISIBLE, review.userId)
                }
            }
        }

        private fun convertMillisToDateString(
            millis: Long,
            pattern: String = "MM/dd/yyyy HH:mm"
        ): String {
            val date = Date(millis)
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            return sdf.format(date)
        }

        private fun setFieldsVisibility(visibility: Int, userId: String) {
            reviewTitle.visibility = visibility
            timestamp.visibility = visibility
            reviewContent.visibility = visibility
            reviewUser.visibility = visibility
            reviewMovie.visibility = visibility
            reviewImage.visibility = visibility
            val userButtonsVisibility = if (canShowUserButtons(userId)) visibility else View.GONE
            deleteReviewButton.visibility = userButtonsVisibility
            editReviewButton.visibility = userButtonsVisibility
        }

        private fun setupDeleteReviewButton(reviewId: String) {
            deleteReviewButton.setOnClickListener {
                feedViewModel.deleteReview(reviewId)
            }
        }

        private fun setupEditReviewButton(reviewId: String, movieId: Int) {
            editReviewButton.setOnClickListener {
                val action =
                    FeedDirections.actionFeedFragmentToAddNewReviewFragment(
                        movieId,
                        reviewId
                    )
                itemView.findNavController().navigate(action)
            }
        }

        private fun canShowUserButtons(userId: String): Boolean {
            return Firebase.auth.currentUser?.uid == userId
        }
    }
}