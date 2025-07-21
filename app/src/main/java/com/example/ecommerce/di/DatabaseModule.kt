package com.example.ecommerce.di

import android.content.Context
import androidx.room.Room
import com.example.ecommerce.data.db.LocalDB
import com.example.ecommerce.data.db.dao.OrdersDao
import com.example.ecommerce.data.db.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LocalDB {
        return Room.databaseBuilder(
            context,
            LocalDB::class.java,
            "ecommerce_db"
        ).build()
    }

    @Provides
    fun provideUserDao(database: LocalDB): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideOrdersDao(database: LocalDB): OrdersDao {
        return database.ordersDao()
    }
}