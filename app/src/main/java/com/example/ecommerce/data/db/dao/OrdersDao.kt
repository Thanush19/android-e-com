package com.example.ecommerce.data.db.dao

import androidx.room.*
import com.example.ecommerce.data.db.entity.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Query("SELECT * FROM orders WHERE userId = :userId")
    fun getOrdersByUser(userId: Long): Flow<List<Order>>

}
