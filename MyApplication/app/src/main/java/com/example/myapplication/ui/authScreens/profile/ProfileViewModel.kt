package com.example.myapplication.ui.authScreens.profile

import android.util.Log
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dal.firebase.UserRepository
import com.example.myapplication.models.User
import com.example.myapplication.utils.Validator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val validator = Validator()

    val firstName = MutableLiveData("")
    val lastName = MutableLiveData("")
    val imageUri = MutableLiveData("")

    val isFirstNameValid = MutableLiveData(true)
    val isLastNameValid = MutableLiveData(true)
    val isImageUriValid = MutableLiveData(true)

    val isFormValid: Boolean
        get() = isFirstNameValid.value!! && isLastNameValid.value!! && isImageUriValid.value!!

    init {
        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val user = userRepository.getUserById(userId)
            withContext(Dispatchers.Main) { setUserFields(user) }
        }
    }

    private fun setUserFields(user: User) {
        firstName.value = user.firstName!!
        lastName.value = user.lastName!!
        imageUri.value = user.imageUri!!
    }

    fun saveChanges(onSuccess: () -> Unit, onFailure: (error: Exception?) -> Unit) {
        validateForm()

        if (!isFormValid) {
            onFailure(null)
            return
        }

        try {
            updateUserConcurrently(onSuccess, onFailure)
        } catch (e: Exception) {
            Log.e("Profile", "Error updating user", e)
            onFailure(e)
        }
    }

    private fun updateUserConcurrently(
        onSuccess: () -> Unit,
        onFailure: (error: Exception?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = constructUserFromFields()
                userRepository.saveUserInDB(user).await()
                if (user.imageUri != imageUri.value) {
                    userRepository.saveUserImage(user.imageUri!!, user.id!!)
                }
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.e("Profile", "Error updating user", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            }
        }
    }

    private fun validateForm() {
        isFirstNameValid.value = validator.validateName(firstName.value!!)
        isLastNameValid.value = validator.validateName(lastName.value!!)
        isImageUriValid.value = validator.validateImageUri(imageUri.value!!)
    }

    private fun constructUserFromFields(): User {
        return User(
            firstName = firstName.value!!,
            lastName = lastName.value!!,
            email = auth.currentUser?.email!!,
            imageUri = imageUri.value!!,
            id = auth.currentUser!!.uid
        )
    }
}