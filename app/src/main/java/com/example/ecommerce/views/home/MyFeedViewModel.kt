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

    private val _products = MutableStateFlow<List<Product>?>(null)
    val products: StateFlow<List<Product>?> = _products.asStateFlow()

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    val allProducts: StateFlow<List<Product>> = _allProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init{
        fetchAllProducts()
    }

    fun fetchAllProducts() {
        viewModelScope.launch {
            _allProducts.value = productRepository.getAllProducts() ?: emptyList()
        }
    }


    fun fetchProductsByIds(ids: List<Int>) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val fetchedProducts = mutableListOf<Product>()
                for (id in ids) {
                    val product = productRepository.getProductById(id)
                    if (product != null) {
                        fetchedProducts.add(product)
                    } else {
                        _error.emit("Failed to fetch ID $id")
                        break
                    }
                }
                if (fetchedProducts.isNotEmpty()) {
                    _products.value = fetchedProducts
                } else if (_error.replayCache.isEmpty()) {
                    _error.value ="No products fetched"
                }
            } catch (e: Exception) {
                _error.value =e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}