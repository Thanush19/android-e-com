package com.example.ecommerce.data.repository

import com.example.ecommerce.data.api.ProductApiService
import com.example.ecommerce.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val productApiService: ProductApiService) {
    suspend fun getAllProducts(): List<Product>? = withContext(Dispatchers.IO) {
        try {
            println("DEBUG: Repository making API call")
            val res = productApiService.getAllProducts()
            if (res.isSuccessful) {
                println("DEBUG: API call successful, got ${res.body()?.size} products")
                return@withContext res.body()
            } else {
                println("DEBUG: API call failed with code ${res.code()}")
            }
        } catch (e: Exception) {
            println("DEBUG: Repository error ${e.message}")
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


//hey i'm thinking of this approach
//        first fetch all the product using getAllProducts() and store it ,
//        use scroll listener which listen to scroll , whn user scrolls all the products , then again call the getAllProducts()
//        when fetching this products , show the loader , this is what i'm thnking of , help me to achieve it'