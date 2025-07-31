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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val ordersRepository: OrdersRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            userPreferencesRepository.userId.collectLatest { userId ->
                if (userId != null) {
                    val user = userRepository.getUserById(userId)
                    _currentUser.value = user
                } else {
                    _currentUser.value = null
                }
            }
        }
    }

    suspend fun getOrdersByUser(userId: Long): List<Order>? {
        return ordersRepository.getOrdersByUser(userId).firstOrNull()
    }

    suspend fun getProductById(productId:Int) : Product? {
        return productRepository.getProductById(productId)
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearUserId()
        }
    }
}