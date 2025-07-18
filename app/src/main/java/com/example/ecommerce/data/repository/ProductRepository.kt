package com.example.ecommerce.data.repository

import com.example.ecommerce.data.api.ProductApiService
import com.example.ecommerce.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val productApiService: ProductApiService) {
    suspend fun getAllProducts(): List<Product>? = withContext(Dispatchers.IO) {
        try {
            val res = productApiService.getAllProducts()
            if (res.isSuccessful) {
                return@withContext res.body()
            }
        } catch (e: Exception) {
            println("error ${e.message}")
        }
        return@withContext null
    }

    suspend fun getProductById(id: Int): Product? = withContext(Dispatchers.IO) {
        try {
            val res = productApiService.getProductById(id)
            if (res.isSuccessful) {
                return@withContext res.body()
            }
        } catch (e: Exception) {
            println("error ${e.message}")
        }
        return@withContext null
    }


}