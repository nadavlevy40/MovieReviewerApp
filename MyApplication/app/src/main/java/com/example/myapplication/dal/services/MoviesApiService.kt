package com.example.myapplication.dal.services

import com.example.myapplication.models.Movie
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApiService {
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_video") includeVideo: Boolean = false,
        @Query("language") language: String = "en-US"
    ): MoviesDTO

    @GET("movie/{id}")
    suspend fun getMovieById(@Path("id") id: Int): Movie

    companion object {
        fun create(): MoviesApiService {
            return NetworkModule().retrofit.create(MoviesApiService::class.java)
        }
    }
}