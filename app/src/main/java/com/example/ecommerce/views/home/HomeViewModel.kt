package com.example.ecommerce.views.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.db.entity.User
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import com.example.ecommerce.data.repository.ProductRepository
import com.example.ecommerce.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData(false)

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        loadUserData()
        loadProducts()
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

    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val productList = productRepository.getAllProducts() ?: emptyList()
                _products.value = productList
                if (productList.isEmpty()) {
                    _error.value = "No products found"
                }
            } catch (e: Exception) {
                _products.value = emptyList()
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearUserId()
        }
    }
}