package com.example.ecommerce.views.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import com.example.ecommerce.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


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
                val user = userRepository.loginUser(username)
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