package com.example.ecommerce.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ecommerce.data.db.dao.OrdersDao
import com.example.ecommerce.data.db.dao.UserDao
import com.example.ecommerce.data.db.entity.Order
import com.example.ecommerce.data.db.entity.User
import com.example.ecommerce.data.db.converters.Converters

@Database(
    entities = [User::class, Order::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class LocalDB : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun ordersDao(): OrdersDao

    companion object {
        private const val DATABASE_NAME = "ecommerce_db"

        @Volatile
        private var INSTANCE: LocalDB? = null

        fun getInstance(context: Context): LocalDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDB::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance
                instance
            }
        }
    }
}
