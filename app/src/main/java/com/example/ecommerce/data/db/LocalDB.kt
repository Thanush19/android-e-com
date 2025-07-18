package com.example.ecommerce.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ecommerce.data.db.dao.CartDao
import com.example.ecommerce.data.db.dao.UserDao
import com.example.ecommerce.data.db.entity.Cart
import com.example.ecommerce.data.db.entity.User

@Database(
    entities = [User::class, Cart::class],
    version = 1
)
abstract class LocalDB : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao

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
                )
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
