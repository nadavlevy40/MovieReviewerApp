package com.example.myapplication.models

data class User(
    val email: String? = "",
    val firstName: String? = "",
    val lastName: String? ="",
    var imageUri: String? = "",
    val id: String? = ""
) {
    companion object {
        const val EMAIL_KEY = "email"
        const val FIRST_NAME_KEY = "firstName"
        const val LAST_NAME_KEY = "lastName"
        const val IMAGE_URI_KEY = "imageUri"
    }

    fun fromJSON(json: Map<String, Any>): User {
        val email = json[EMAIL_KEY] as? String ?: ""
        val firstName = json[FIRST_NAME_KEY] as? String ?: ""
        val lastName = json[LAST_NAME_KEY] as? String ?: ""
        val imageUri = json[IMAGE_URI_KEY] as? String
        return User(email, firstName, lastName, imageUri)
    }

    val json: HashMap<String, String?>
        get() {
            return hashMapOf(
                EMAIL_KEY to email,
                FIRST_NAME_KEY to firstName,
                LAST_NAME_KEY to lastName,
                IMAGE_URI_KEY to imageUri
            )
        }
}
