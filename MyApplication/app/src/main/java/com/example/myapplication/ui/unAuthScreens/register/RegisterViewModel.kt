package com.example.myapplication.ui.unAuthScreens.register

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dal.repositories.UserRepository
import com.example.myapplication.models.User
import com.example.myapplication.utils.Validator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    val firstName = ObservableField("")
    val lastName = ObservableField("")
    val email = ObservableField("")
    val password = ObservableField("")
    val confirmPassword = ObservableField("")
    val imageUri = ObservableField("")


    val isFirstNameValid = ObservableField(true)
    val isLastNameValid = ObservableField(true)
    val isEmailValid = ObservableField(true)
    val isPasswordValid = ObservableField(true)
    val isConfirmPasswordValid = ObservableField(true)
    val isImageUriValid = ObservableField(true)

    val isFormValid: Boolean
        get() = isFirstNameValid.get()!! && isLastNameValid.get()!! && isEmailValid.get()!!
                && isPasswordValid.get()!! && isConfirmPasswordValid.get()!! && isImageUriValid.get()!!

    private val validator = Validator()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun register(onSuccess: () -> Unit, onFailure: (error: Exception?) -> Unit) {
        validateForm()

        if (!isFormValid) {
            onFailure(null)
            return
        }

        try {
            createUserConcurrently(onSuccess, onFailure)
        } catch (e: Exception) {
            Log.e("Register", "Error registering user", e)
            onFailure(e)

        }
    }

    private fun createUserConcurrently(
        onSuccess: () -> Unit,
        onFailure: (error: Exception?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                createAuthUser(email.get()!!, password.get()!!)
                saveUser(constructUserFromFields())
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.e("Register", "Error registering user", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            }
        }
    }

    private fun validateForm() {
        isFirstNameValid.set(validator.validateName(firstName.get()!!))
        isLastNameValid.set(validator.validateName(lastName.get()!!))
        isEmailValid.set(validator.validateEmail(email.get()!!))
        isPasswordValid.set(validator.validatePassword(password.get()!!))
        isConfirmPasswordValid.set(
            validator.validateConfirmPassword(
                password.get()!!,
                confirmPassword.get()!!
            )
        )
        isImageUriValid.set(validator.validateImageUri(imageUri.get()!!))
    }

    private fun constructUserFromFields(): User {
        val user = User(
            firstName = firstName.get()!!,
            lastName = lastName.get()!!,
            email = email.get()!!,
            id = auth.currentUser!!.uid
        )
        user.imageUri = imageUri.get()!!
        return user
    }

    private suspend fun createAuthUser(email: String, password: String) {
        val task = auth.createUserWithEmailAndPassword(email, password).await()
        if (task.user?.uid == null) throw Exception("User not created")
    }

    private suspend fun saveUser(user: User) {
        try {
            userRepository.saveUserInDB(user)
            userRepository.saveUserImage(user.imageUri!!, user.id)
        } catch (e: Exception) {
            Log.e("Register", "Error saving user", e)
            throw e
        }


        Log.i("UserRepository", "User ${user.json} saved in DB")
    }
}