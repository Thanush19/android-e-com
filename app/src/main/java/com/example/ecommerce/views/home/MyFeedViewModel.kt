package com.example.ecommerce.views.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyFeedViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    val allProducts: StateFlow<List<Product>> = _allProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchAllProducts()
    }

    fun fetchAllProducts() {
        println("DEBUG: fetchAllProducts called, loading: ${_isLoading.value}")
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                println("DEBUG: Making API call to get products")
                val newProducts = productRepository.getAllProducts() ?: emptyList()
                println("DEBUG: Got ${newProducts.size} new products")
                val currentList = _allProducts.value.toMutableList()
                val oldSize = currentList.size
                currentList.addAll(newProducts)
                _allProducts.value = currentList
                println("DEBUG: Total products now: ${currentList.size}, added ${currentList.size - oldSize}")
            } catch (e: Exception) {
                println("DEBUG: Error fetching products: ${e.message}")
                _error.value = e.message ?: "Failed to fetch products"
            } finally {
                _isLoading.value = false
            }
        }
    }
}