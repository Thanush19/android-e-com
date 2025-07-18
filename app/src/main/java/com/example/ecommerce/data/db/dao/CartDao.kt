package com.example.ecommerce.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ecommerce.data.db.entity.Cart
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert
    suspend fun insertCart(cart: Cart): Long

    @Update
    suspend fun updateCart(cart: Cart)

    @Query("SELECT * FROM carts WHERE userId = :userId")
    suspend fun getCartByUserId(userId: Long): Cart?

}