package com.example.sensixpert.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sensixpert.auth.AuthRepository
import kotlinx.coroutines.launch

sealed class AuthState {
    object Loading : AuthState()
    object LoggedOut : AuthState()
    object LoggedIn : AuthState()
}

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    // Form fields
    var loginEmail by mutableStateOf("")
    var loginPassword by mutableStateOf("")

    var registerName by mutableStateOf("")
    var registerPhone by mutableStateOf("")
    var registerEmail by mutableStateOf("")
    var registerPassword by mutableStateOf("")

    // Error / loading states
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isProcessing by mutableStateOf(false)
        private set

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        authState = if (repository.isLoggedIn) {
            AuthState.LoggedIn
        } else {
            AuthState.LoggedOut
        }
    }

    fun login() {
        if (loginEmail.isBlank() || loginPassword.isBlank()) {
            errorMessage = "Please fill all fields"
            return
        }
        errorMessage = null
        isProcessing = true

        viewModelScope.launch {
            val result = repository.login(loginEmail.trim(), loginPassword)
            isProcessing = false
            result.fold(
                onSuccess = {
                    authState = AuthState.LoggedIn
                },
                onFailure = { e ->
                    errorMessage = parseFirebaseError(e.message)
                }
            )
        }
    }

    fun register() {
        if (registerName.isBlank() || registerPhone.isBlank() ||
            registerEmail.isBlank() || registerPassword.isBlank()
        ) {
            errorMessage = "Please fill all fields"
            return
        }
        if (registerPassword.length < 6) {
            errorMessage = "Password must be at least 6 characters"
            return
        }
        if (!registerEmail.contains("@")) {
            errorMessage = "Please enter a valid email"
            return
        }
        if (registerPhone.length < 10) {
            errorMessage = "Please enter a valid phone number"
            return
        }

        errorMessage = null
        isProcessing = true

        viewModelScope.launch {
            val result = repository.register(
                name = registerName.trim(),
                phone = registerPhone.trim(),
                email = registerEmail.trim(),
                password = registerPassword
            )
            isProcessing = false
            result.fold(
                onSuccess = {
                    authState = AuthState.LoggedIn
                },
                onFailure = { e ->
                    errorMessage = parseFirebaseError(e.message)
                }
            )
        }
    }

    fun logout() {
        repository.logout()
        // Clear form fields
        loginEmail = ""
        loginPassword = ""
        registerName = ""
        registerPhone = ""
        registerEmail = ""
        registerPassword = ""
        errorMessage = null
        authState = AuthState.LoggedOut
    }

    fun clearError() {
        errorMessage = null
    }

    fun getUserId(): String? = repository.currentUser?.uid
    fun getUserName(): String? = repository.currentUser?.displayName
    fun getUserEmail(): String? = repository.currentUser?.email

    private fun parseFirebaseError(message: String?): String {
        return when {
            message == null -> "Something went wrong"
            message.contains("INVALID_LOGIN_CREDENTIALS", true) ||
            message.contains("invalid", true) && message.contains("credential", true) ->
                "Invalid email or password"
            message.contains("email address is already", true) ->
                "This email is already registered"
            message.contains("badly formatted", true) ->
                "Please enter a valid email address"
            message.contains("network", true) ->
                "Network error. Please check your connection"
            message.contains("weak password", true) ->
                "Password is too weak"
            else -> message
        }
    }
}
