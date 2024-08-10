// File: MyApplication/app/src/main/java/com/example/myapplication/ui/components/MovieCard.kt

package com.example.myapplication.ui.components

import MovieDiffCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.models.Movie
import com.example.myapplication.ui.authScreens.movies.MoviesFragment
import com.example.myapplication.ui.authScreens.movies.MoviesFragmentDirections
import com.google.android.material.card.MaterialCardView


class MovieCardAdapter(private var movies: List<Movie>) :
    RecyclerView.Adapter<MovieCardAdapter.MovieCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_card, parent, false)
        return MovieCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieCardViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        val diffCallback = MovieDiffCallback(movies, newMovies)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        movies = newMovies
        diffResult.dispatchUpdatesTo(this)
    }

    class MovieCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie) {
            itemView.findViewById<TextView>(R.id.movieTitle).text = movie.title
            itemView.findViewById<TextView>(R.id.movieOverview).text = movie.overview
            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w500/${movie.posterPath}")
                .into(itemView.findViewById(R.id.moviePoster))

            itemView.findViewById<Button>(R.id.addReviewButton).setOnClickListener {
                val action =
                    MoviesFragmentDirections.actionMoviesFragmentToAddNewReviewFragment(
                        movie.id,
                        ""
                    )
                itemView.findNavController().navigate(action)
            }
        }
    }
}