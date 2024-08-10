package com.example.myapplication.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    @SerializedName("id")
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String = "",
    @SerializedName("poster_path")
    @ColumnInfo(name = "poster_path")
    val posterPath: String = "",
    @SerializedName("release_date")
    @ColumnInfo(name = "release_date")
    val releaseDate: String= "",
    @ColumnInfo(name = "overview")
    val overview: String="",
    @SerializedName("vote_average")
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double=0.0,
    @ColumnInfo(name = "popularity")
    val popularity: Double,
    @SerializedName("original_language")
    @ColumnInfo(name = "original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    @ColumnInfo(name = "original_title")
    val originalTitle: String,
    @SerializedName("backdrop_path")
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String,
    @ColumnInfo(name = "adult")
    val adult: Boolean,
    @ColumnInfo(name = "video")
    val video: Boolean,
    @SerializedName("vote_count")
    @ColumnInfo(name = "vote_count")
    val voteCount: Int
) {
    companion object {
        const val ID_KEY = "id"
        const val TITLE_KEY = "title"
        const val POSTER_PATH_KEY = "posterPath"
        const val RELEASE_DATE_KEY = "releaseDate"
        const val OVERVIEW_KEY = "overview"
        const val VOTE_AVERAGE_KEY = "voteAverage"
        const val POPULARITY_KEY = "popularity"
        const val ORIGINAL_LANGUAGE_KEY = "originalLanguage"
        const val ORIGINAL_TITLE_KEY = "originalTitle"
        const val BACKDROP_PATH_KEY = "backdropPath"
        const val ADULT_KEY = "adult"
        const val VIDEO_KEY = "video"
        const val VOTE_COUNT_KEY = "voteCount"

        fun fromJSON(json: Map<String, Any>): Movie {
            val id = json[ID_KEY] as? Int ?: 0
            val title = json[TITLE_KEY] as? String ?: ""
            val posterPath = json[POSTER_PATH_KEY] as? String ?: ""
            val releaseDate = json[RELEASE_DATE_KEY] as? String ?: ""
            val overview = json[OVERVIEW_KEY] as? String ?: ""
            val voteAverage = json[VOTE_AVERAGE_KEY] as? Double ?: 0.0
            val popularity = json[POPULARITY_KEY] as? Double ?: 0.0
            val originalLanguage = json[ORIGINAL_LANGUAGE_KEY] as? String ?: ""
            val originalTitle = json[ORIGINAL_TITLE_KEY] as? String ?: ""
            val backdropPath = json[BACKDROP_PATH_KEY] as? String ?: ""
            val adult = json[ADULT_KEY] as? Boolean ?: false
            val video = json[VIDEO_KEY] as? Boolean ?: false
            val voteCount = json[VOTE_COUNT_KEY] as? Int ?: 0
            return Movie(
                id,
                title,
                posterPath,
                releaseDate,
                overview,
                voteAverage,
                popularity,
                originalLanguage,
                originalTitle,
                backdropPath,
                adult,
                video,
                voteCount
            )
        }

    }

    val json: HashMap<String, Any?>
        get() {
            return hashMapOf(
                ID_KEY to id,
                TITLE_KEY to title,
                POSTER_PATH_KEY to posterPath,
                RELEASE_DATE_KEY to releaseDate,
                OVERVIEW_KEY to overview,
                VOTE_AVERAGE_KEY to voteAverage,
                POPULARITY_KEY to popularity,
                ORIGINAL_LANGUAGE_KEY to originalLanguage,
                ORIGINAL_TITLE_KEY to originalTitle,
                BACKDROP_PATH_KEY to backdropPath,
                ADULT_KEY to adult,
                VIDEO_KEY to video,
                VOTE_COUNT_KEY to voteCount
            )
        }
}
