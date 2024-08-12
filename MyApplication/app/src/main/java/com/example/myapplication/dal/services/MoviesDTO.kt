package com.example.myapplication.dal.services

import com.example.myapplication.models.Movie

data class MoviesDTO(val page: Int = 0, val results: List<Movie> = emptyList(), val totalResults: Int = 0, val totalPages: Int = 0) {
    companion object {
        const val PAGE_KEY = "page"
        const val RESULTS_KEY = "results"
        const val TOTAL_RESULTS_KEY = "total_results"
        const val TOTAL_PAGES_KEY = "total_pages"
    }

    fun fromJSON(json: Map<String, Any>): MoviesDTO {
        val page = json[PAGE_KEY] as? Int ?: 0
        val results = (json[RESULTS_KEY] as? List<Map<String, Any>> ?: emptyList()).map { Movie.fromJSON(it) }
        val totalResults = json[TOTAL_RESULTS_KEY] as? Int ?: 0
        val totalPages = json[TOTAL_PAGES_KEY] as? Int ?: 0
        return MoviesDTO(page, results, totalResults, totalPages)
    }

    val json: HashMap<String, Any?>
        get() {
            return hashMapOf(
                PAGE_KEY to page,
                RESULTS_KEY to results.map { it.json },
                TOTAL_RESULTS_KEY to totalResults,
                TOTAL_PAGES_KEY to totalPages
            )
        }

    fun toMovies(): List<Movie> {
        return results
    }
}
