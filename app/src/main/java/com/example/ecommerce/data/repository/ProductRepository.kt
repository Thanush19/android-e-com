package com.example.ecommerce.data.repository

import com.example.ecommerce.data.api.ProductApiService
import com.example.ecommerce.data.model.Product

class ProductRepository(private val productApiService: ProductApiService) {
    suspend fun getAllProducts(): List<Product>? {
        return try {
            productApiService.getAllProducts().body()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getProductById(id: Int): Product? {
        return try {
            productApiService.getProductById(id).body()
        } catch (_: Exception) {
            null
        }
    }
}