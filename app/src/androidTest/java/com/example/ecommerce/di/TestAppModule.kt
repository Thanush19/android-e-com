    package com.example.ecommerce.di

    import android.content.Context
    import androidx.datastore.core.DataStore
    import androidx.datastore.preferences.core.Preferences
    import androidx.datastore.preferences.core.PreferenceDataStoreFactory
    import androidx.datastore.preferences.preferencesDataStoreFile
    import androidx.room.Room
    import com.example.ecommerce.data.api.ProductApiService
    import com.example.ecommerce.data.db.LocalDB
    import com.example.ecommerce.data.db.dao.OrdersDao
    import com.example.ecommerce.data.db.dao.UserDao
    import com.example.ecommerce.data.preferences.UserPreferencesRepository
    import com.example.ecommerce.data.repository.OrdersRepository
    import com.example.ecommerce.data.repository.ProductRepository
    import com.example.ecommerce.data.repository.UserRepository
    import dagger.Module
    import dagger.Provides
    import dagger.hilt.android.qualifiers.ApplicationContext
    import dagger.hilt.components.SingletonComponent
    import dagger.hilt.testing.TestInstallIn
    import okhttp3.mockwebserver.MockWebServer
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import javax.inject.Singleton

    @Module
    @TestInstallIn(
        components = [SingletonComponent::class],
        replaces = [DatabaseModule::class, RepositoryModule::class, PreferencesModule::class, NetworkModule::class]
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

        @Provides
        @Singleton
        fun provideUserRepository(userDao: UserDao): UserRepository {
            return UserRepository(userDao)
        }

        @Provides
        @Singleton
        fun provideOrdersRepository(ordersDao: OrdersDao): OrdersRepository {
            return OrdersRepository(ordersDao)
        }

    }