package com.example.ecommerce.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.ecommerce.data.api.ProductApiService
import com.example.ecommerce.data.db.LocalDB
import com.example.ecommerce.data.db.dao.OrdersDao
import com.example.ecommerce.data.db.dao.UserDao
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import dagger.hilt.testing.*

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class, PreferencesModule::class]
)
object TestAppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LocalDB {
        return Room.inMemoryDatabaseBuilder(context, LocalDB::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: LocalDB): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideOrdersDao(database: LocalDB): OrdersDao = database.ordersDao()

    @Provides
    @Singleton
    fun provideProductApiService(retrofit: Retrofit): ProductApiService {
        return retrofit.create(ProductApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("test_user_prefs")
        }
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }
}