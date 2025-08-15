package com.example.ecommerce.data.repository

import com.example.ecommerce.data.api.ProductApiService
import com.example.ecommerce.data.model.Product
import javax.inject.Inject

interface ProductRepository {
    suspend fun getAllProducts(): List<Product>?
    suspend fun getProductById(id: Int): Product?
}

