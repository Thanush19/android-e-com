package com.example.ecommerce.di

import android.content.Context
import com.example.ecommerce.data.api.ProductApiService
import com.example.ecommerce.data.api.RetrofitClient
import com.example.ecommerce.data.db.LocalDB
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import com.example.ecommerce.data.repository.CartRepository
import com.example.ecommerce.data.repository.ProductRepository
import com.example.ecommerce.data.repository.UserRepository

class AppContainer(private val context: Context) {
    private val database: LocalDB by lazy {
        LocalDB.getInstance(context)
    }
    private val userDao by lazy { database.userDao() }
    private val cartDao by lazy { database.cartDao() }

    private val productApiService: ProductApiService by lazy {
        RetrofitClient.productApiService
    }

    val userRepository by lazy { UserRepository(userDao) }
    val cartRepository by lazy { CartRepository(cartDao) }
    val productRepository by lazy { ProductRepository(productApiService) }
    val userPreferencesRepository by lazy { UserPreferencesRepository(context) }
}
