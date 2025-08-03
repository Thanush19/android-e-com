package com.example.ecommerce.views.productdetails

import androidx.lifecycle.ViewModel
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ProductDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    suspend fun getProductById(id: Int): Product? {
        return try {
            productRepository.getProductById(id)
        } catch (e: Exception) {
            _error.value = "Failed to load product: ${e.message}"
            null
        }
    }
}