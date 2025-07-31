package com.example.ecommerce.views.productdetails

import com.example.ecommerce.data.model.Product
import com.example.ecommerce.data.repository.ProductRepository
import javax.inject.Inject

class ProductDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) {

    suspend fun getProductById(id:Int): Product? {
        return productRepository.getProductById(id)
    }
}