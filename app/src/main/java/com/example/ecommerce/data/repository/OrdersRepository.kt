package com.example.ecommerce.data.repository

import com.example.ecommerce.data.db.dao.OrdersDao
import com.example.ecommerce.data.db.entity.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class OrdersRepository @Inject constructor(private val ordersDao: OrdersDao) {

    suspend fun placeOrder(order: Order): Long {
        return ordersDao.insertOrder(order)
    }

    fun getOrdersByUser(userId: Long): Flow<List<Order>> {
        return try {
            ordersDao.getOrdersByUser(userId)
        } catch (_: Exception) {
            flowOf(emptyList())
        }
    }
}