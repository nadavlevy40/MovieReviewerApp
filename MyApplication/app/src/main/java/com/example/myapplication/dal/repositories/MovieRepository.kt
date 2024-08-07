package com.example.myapplication.dal.repositories

import com.example.myapplication.dal.services.MoviesApiService
import com.example.myapplication.models.Movie

class MovieRepository {
    private val apiService: MoviesApiService = MoviesApiService.create()

    suspend fun discoverMovies(): List<Movie> {
        val movieDTOs = apiService.discoverMovies()
        return movieDTOs.toMovies()
    }
}