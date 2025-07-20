package com.example.ecommerce.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import com.example.ecommerce.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _isLoggedIn = MutableLiveData<Boolean>(false)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            _isLoggedIn.value = userPreferencesRepository.isLoggedIn.first()
        }
    }
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Username and pw cant be empty")
            return
        }

        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val user = userRepository.loginUser(username, password)
                if (user != null && user.password == password) {
                    userPreferencesRepository.saveUserId(user.id)
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Invalid username or pw")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Login failed: ${e.message}")
            }
        }
    }
}

sealed class LoginState {
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    fun register(username: String, password: String, confirmPassword: String) {
        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _registerState.value = RegisterState.Error("All fields are req")
            return
        }

        if (password != confirmPassword) {
            _registerState.value = RegisterState.Error("Pws do not match")
            return
        }

        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                val userId = userRepository.registerUser(username, password)
                if (userId != null) {
                    userPreferencesRepository.saveUserId(userId)
                    _registerState.value = RegisterState.Success
                } else {
                    _registerState.value = RegisterState.Error("Username already exists")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Registration failed: ${e.message}")
            }
        }
    }
}

sealed class RegisterState {
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}