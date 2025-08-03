package com.example.ecommerce.views.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.db.entity.Order
import com.example.ecommerce.data.db.entity.User
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import com.example.ecommerce.data.repository.OrdersRepository
import com.example.ecommerce.data.repository.ProductRepository
import com.example.ecommerce.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MyProfileViewModel(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val ordersRepository: OrdersRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadUserData()
    }

    fun loadUserData() {
        viewModelScope.launch {
            try {
                val userId = userPreferencesRepository.userId.firstOrNull()
                if (userId != null) {
                    _currentUser.value = userRepository.getUserById(userId)
                } else {
                    _currentUser.value = null
                }
            } catch (e: Exception) {
                _currentUser.value = null
                _error.value = "Failed to load user data: ${e.message}"
            }
        }
    }

    suspend fun getOrdersByUser(userId: Long): List<Order>? {
        return try {
            ordersRepository.getOrdersByUser(userId).firstOrNull()
        } catch (e: Exception) {
            _error.value = "Failed to load orders: ${e.message}"
            null
        }
    }

    suspend fun getProductById(productId: Int): Product? {
        return try {
            productRepository.getProductById(productId)
        } catch (e: Exception) {
            _error.value = "Failed to load product: ${e.message}"
            null
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearUserId()
            _currentUser.value = null
        }
    }
}