package com.example.ecommerce.di

import android.content.Context
import androidx.room.Room
import com.example.ecommerce.data.db.LocalDB
import com.example.ecommerce.data.db.dao.OrdersDao
import com.example.ecommerce.data.db.dao.UserDao
import com.example.ecommerce.data.repository.OrdersRepository
import com.example.ecommerce.data.repository.ProductRepository
import com.example.ecommerce.data.repository.UserRepository
import com.example.ecommerce.fake.FakeOrdersRepository
import com.example.ecommerce.fake.FakeProductRepository
import com.example.ecommerce.fake.FakeUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LocalDB {
        return Room.inMemoryDatabaseBuilder(context, LocalDB::class.java).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: LocalDB): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideOrdersDao(database: LocalDB): OrdersDao = database.ordersDao()

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return FakeUserRepository()
    }

    @Provides
    @Singleton
    fun provideOrdersRepository(): OrdersRepository {
        return FakeOrdersRepository()
    }

    @Provides
    @Singleton
    fun provideProductRepository(): ProductRepository {
        return FakeProductRepository()
    }
}