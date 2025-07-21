package com.example.ecommerce.data.repository

import com.example.ecommerce.data.db.dao.OrdersDao
import com.example.ecommerce.data.db.entity.Order
import kotlinx.coroutines.flow.Flow

class OrdersRepository(private val ordersDao: OrdersDao) {

    suspend fun placeOrder(order: Order): Long {
        return ordersDao.insertOrder(order)
    }

    fun getOrdersByUser(userId: Long): Flow<List<Order>> {
        return ordersDao.getOrdersByUser(userId)
    }
}
