//package com.example.ecommerce.di
//
//import com.example.ecommerce.data.api.ProductApiService
//import com.example.ecommerce.data.db.dao.OrdersDao
//import com.example.ecommerce.data.db.dao.UserDao
//import com.example.ecommerce.data.repository.OrdersRepository
//import com.example.ecommerce.data.repository.ProductRepository
//import com.example.ecommerce.data.repository.UserRepository
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@InstallIn(SingletonComponent::class)
//object RepositoryModule {
//
//    @Provides
//    @Singleton
//    fun provideUserRepository(userDao: UserDao): UserRepository {
//        return UserRepository(userDao)
//    }
//
//    @Provides
//    @Singleton
//    fun provideOrdersRepository(ordersDao: OrdersDao): OrdersRepository {
//        return OrdersRepository(ordersDao)
//    }
//
//    @Provides
//    @Singleton
//    fun provideProductRepository(productApiService: ProductApiService): ProductRepository {
//        return ProductRepository(productApiService)
//    }
//}