package com.example.ecommerce.data.repository

import com.example.ecommerce.data.api.ProductApiService
import com.example.ecommerce.data.model.Product


class ProductRepository(private val productApiService: ProductApiService) {
    suspend fun getAllProducts(): List<Product>? {
       return productApiService.getAllProducts().body()
    }

    suspend fun getProductById(id: Int): Product?  {
        return    productApiService.getProductById(id).body()
    }
}

