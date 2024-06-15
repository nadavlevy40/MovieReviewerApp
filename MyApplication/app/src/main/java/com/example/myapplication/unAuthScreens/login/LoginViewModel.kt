package com.example.myapplication.unAuthScreens.login

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dal.firebase.UserRepository
import com.example.myapplication.utils.Validator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {
    val email = ObservableField("")
    val password = ObservableField("")

    val isEmailValid = ObservableField(true)
    val isPasswordValid = ObservableField(true)

    val isFormValid: Boolean
        get() = isEmailValid.get()!! && isPasswordValid.get()!!
    private val validator = Validator()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(onSuccess: () -> Unit, onFailure: (error: Exception?) -> Unit) {
        validateForm()
        if (!isFormValid) {
            onFailure(null)
            return
        }

        signInUserConcurrently(onSuccess, onFailure)
    }

    private fun signInUserConcurrently(
        onSuccess: () -> Unit,
        onFailure: (error: Exception?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    auth.signInWithEmailAndPassword(email.get()!!, password.get()!!).await()
                }
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.e("Login", "Error signing in user", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            }
        }
    }

    private fun validateForm() {
        isEmailValid.set(validator.validateEmail(email.get()!!))
        isPasswordValid.set(validator.validatePassword(password.get()!!))
    }
}