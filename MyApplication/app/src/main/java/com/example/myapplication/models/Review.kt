package com.example.myapplication.models

data class Review(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        const val ID_KEY = "id"
        const val USER_ID_KEY = "userId"
        const val TITLE_KEY = "title"
        const val CONTENT_KEY = "content"
        const val IMAGE_URL_KEY = "imageUrl"
        const val TIMESTAMP_KEY = "timestamp"
    }

    fun fromJSON(json: Map<String, Any>): Review {
        val id = json[ID_KEY] as? String ?: ""
        val userId = json[USER_ID_KEY] as? String ?: ""
        val title = json[TITLE_KEY] as? String ?: ""
        val content = json[CONTENT_KEY] as? String ?: ""
        val imageUrl = json[IMAGE_URL_KEY] as? String ?: ""
        val timestamp = json[TIMESTAMP_KEY] as? Long ?: System.currentTimeMillis()
        return Review(id, userId, title, content, imageUrl, timestamp)
    }

    val json: HashMap<String, Any?>
        get() {
            return hashMapOf(
                ID_KEY to id,
                USER_ID_KEY to userId,
                TITLE_KEY to title,
                CONTENT_KEY to content,
                IMAGE_URL_KEY to imageUrl,
                TIMESTAMP_KEY to timestamp
            )
        }
}
