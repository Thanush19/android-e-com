package com.example.ecommerce.data.impl

import com.example.ecommerce.data.api.ProductApiService
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.data.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productApiService: ProductApiService
) : ProductRepository
{
    override suspend fun getAllProducts(): List<Product>? {
        return try {
            productApiService.getAllProducts().body()
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun getProductById(id: Int): Product? {
        return try {
            productApiService.getProductById(id).body()
        } catch (_: Exception) {
            null
        }
    }
}