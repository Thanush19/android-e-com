package com.example.ecommerce.views.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyFeedViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>?>()
    val products: LiveData<List<Product>?> = _products

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

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
                        _error.value = "Failed to fetch product with ID $id"
                        break
                    }
                }
                if (fetchedProducts.isNotEmpty()) {
                    _products.value = fetchedProducts
                } else if (_error.value == null) {
                    _error.value = "No products fetched"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}