package com.example.ecommerce

import android.app.Application
import com.example.ecommerce.data.api.ProductApiService
import com.example.ecommerce.data.api.RetrofitClient
import com.example.ecommerce.data.db.LocalDB
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import com.example.ecommerce.data.repository.OrdersRepository
import com.example.ecommerce.data.repository.ProductRepository
import com.example.ecommerce.data.repository.UserRepository
import com.example.ecommerce.data.repository.UserRepositoryImpl
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EcommerceApplication : Application() {
//    private val database: LocalDB by lazy {
//        LocalDB.getInstance(applicationContext)
//    }
//    private val userDao by lazy { database.userDao() }
//    private val ordersDao by lazy { database.ordersDao() }
//
//    private val productApiService: ProductApiService by lazy {
//        RetrofitClient.productApiService
//    }
//
//    val userRepository: UserRepository by lazy { UserRepositoryImpl(userDao) }
//    val ordersRepository by lazy { OrdersRepository(ordersDao) }
//    val productRepository by lazy { ProductRepository(productApiService) }
//    val userPreferencesRepository by lazy { UserPreferencesRepository(applicationContext) }
}