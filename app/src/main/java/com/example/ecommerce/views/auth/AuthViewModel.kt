package com.example.ecommerce.views.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import com.example.ecommerce.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState?>(null)
    val loginState: StateFlow<AuthState?> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthState?>(null)
    val registerState: StateFlow<AuthState?> = _registerState.asStateFlow()

    private val _loggedIn = MutableStateFlow<Boolean>(false)
    val loggedIn: StateFlow<Boolean> = _loggedIn.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            userPreferencesRepository.isLoggedIn.collect { isLoggedIn ->
                _loggedIn.value = isLoggedIn
            }
        }
    }

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = AuthState.Error("Username and password can't be empty")
            return
        }

        _loginState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val user = userRepository.loginUser(username)
                if (user != null && user.password == password) {
                    userPreferencesRepository.saveUserId(user.id)
                    _loginState.value = AuthState.Success
                    _loggedIn.value = true
                } else {
                    _loginState.value = AuthState.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                _loginState.value = AuthState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun register(username: String, password: String, confirmPassword: String) {
        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _registerState.value = AuthState.Error("All fields are required")
            return
        }

        if (password != confirmPassword) {
            _registerState.value = AuthState.Error("Passwords do not match")
            return
        }

        _registerState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val userId = userRepository.registerUser(username, password)
                if (userId != null) {
                    userPreferencesRepository.saveUserId(userId)
                    _registerState.value = AuthState.Success
                    _loggedIn.value = true
                } else {
                    _registerState.value = AuthState.Error("Username already exists")
                }
            } catch (e: Exception) {
                _registerState.value = AuthState.Error("Registration failed: ${e.message}")
            }
        }
    }

}

sealed class AuthState {
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}