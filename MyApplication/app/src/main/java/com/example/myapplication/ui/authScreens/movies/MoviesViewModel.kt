package com.example.myapplication.ui.authScreens.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dal.repositories.MovieRepository
import com.example.myapplication.models.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoviesViewModel(private val movieRepository: MovieRepository) : ViewModel() {
    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> get() = _movies
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchMovies()
    }

    private fun fetchMovies() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val movieList = movieRepository.discoverMovies()
            _movies.postValue(movieList)
            withContext(Dispatchers.Main) { _isLoading.value = false }
        }
    }

    fun loadMoreMovies() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val currentMovies = _movies.value ?: emptyList()
            val movieList = movieRepository.discoverMovies(page = currentMovies.size / 15 + 1)
            _movies.postValue(currentMovies + movieList)
            withContext(Dispatchers.Main) { _isLoading.value = false }
        }
    }
}