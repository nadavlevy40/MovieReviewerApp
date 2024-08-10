package com.example.myapplication.ui.authScreens.movies

import PaginationScrollListener
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.dal.repositories.MovieRepository
import com.example.myapplication.databinding.FragmentMoviesBinding
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.ui.components.MovieCardAdapter

class MoviesFragment : Fragment() {

    companion object {
        fun newInstance() = MoviesFragment()
    }

    private lateinit var viewModel: MoviesViewModel
    private lateinit var movieRecyclerView: RecyclerView
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentMoviesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_movies, container, false
        )
        viewModel = MoviesViewModel(MovieRepository(requireContext()))
        bindViews(binding)
        setupRecyclerView(binding)
        setupLoading(binding)

        return binding.root
    }

    private fun bindViews(binding: FragmentMoviesBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupRecyclerView(binding: FragmentMoviesBinding) {
        movieRecyclerView = binding.root.findViewById(R.id.movieRecyclerView)
        val layoutManager = LinearLayoutManager(context)
        movieRecyclerView.layoutManager = layoutManager
        val movieAdapter = MovieCardAdapter(emptyList())
        movieRecyclerView.adapter = movieAdapter

        movieRecyclerView.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                viewModel.loadMoreMovies()
            }

            override fun isLastPage(): Boolean {
                return false
            }

            override fun isLoading(): Boolean {
                return viewModel.isLoading.value ?: false
            }
        })

        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            movieAdapter.updateMovies(movies)
        }
    }

    private fun setupLoading(binding: FragmentMoviesBinding) {
        progressBar = binding.root.findViewById(R.id.progress_bar)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showProgressBar()
            else hideProgressBar()
        }
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }
}