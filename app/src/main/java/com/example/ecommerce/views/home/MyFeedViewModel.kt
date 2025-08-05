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

    private val _verticalProducts = MutableStateFlow<List<Product>>(emptyList())
    val verticalProducts: StateFlow<List<Product>> = _verticalProducts.asStateFlow()

    private val _horizontalProducts = MutableStateFlow<List<Product>>(emptyList())
    val horizontalProducts: StateFlow<List<Product>> = _horizontalProducts.asStateFlow()

    private val _isLoadingVertical = MutableStateFlow(false)
    val isLoadingVertical: StateFlow<Boolean> = _isLoadingVertical.asStateFlow()

    private val _isLoadingHorizontal = MutableStateFlow(false)
    val isLoadingHorizontal: StateFlow<Boolean> = _isLoadingHorizontal.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _sortOption = MutableStateFlow<Int?>(null)
    val sortOption: StateFlow<Int?> = _sortOption.asStateFlow()

    init {
        fetchAllProducts(LayoutType.VERTICAL)
        fetchAllProducts(LayoutType.HORIZONTAL)
    }

    enum class LayoutType {
        VERTICAL,
        HORIZONTAL
    }

    fun setSortOption(sortOption: Int?) {
        _sortOption.value = sortOption
    }

    fun fetchAllProducts(layoutType: LayoutType) {
        when (layoutType) {
            LayoutType.VERTICAL -> {
                if (_isLoadingVertical.value) return
                viewModelScope.launch {
                    _isLoadingVertical.value = true
                    _error.value = null
                    try {
                        val newProducts = productRepository.getAllProducts() ?: emptyList()
                        val currentList = _verticalProducts.value.toMutableList()
                        currentList.addAll(newProducts)
                        _verticalProducts.value = currentList
                    } catch (_: Exception) {
                        _error.value = "Network error"
                    } finally {
                        _isLoadingVertical.value = false
                    }
                }
            }
            LayoutType.HORIZONTAL -> {
                if (_isLoadingHorizontal.value) return
                viewModelScope.launch {
                    _isLoadingHorizontal.value = true
                    _error.value = null
                    try {
                        val newProducts = productRepository.getAllProducts() ?: emptyList()
                        val currentList = _horizontalProducts.value.toMutableList()
                        currentList.addAll(newProducts)
                        _horizontalProducts.value = currentList
                    } catch (e: Exception) {
                        _error.value = e.message
                    } finally {
                        _isLoadingHorizontal.value = false
                    }
                }
            }
        }
    }
}