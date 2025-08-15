package com.example.ecommerce.data.repository

import com.example.ecommerce.data.db.dao.OrdersDao
import com.example.ecommerce.data.db.entity.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface OrdersRepository  {

    suspend fun placeOrder(order: Order): Long
    fun getOrdersByUser(userId: Long): Flow<List<Order>>
}

