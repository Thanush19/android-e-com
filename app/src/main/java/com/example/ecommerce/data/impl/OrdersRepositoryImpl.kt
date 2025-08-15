package com.example.ecommerce.data.impl

import com.example.ecommerce.data.db.dao.OrdersDao
import com.example.ecommerce.data.db.entity.Order
import com.example.ecommerce.data.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(private val ordersDao: OrdersDao) :
    OrdersRepository {

    override suspend fun placeOrder(order: Order): Long {
        return ordersDao.insertOrder(order)
    }

    override fun getOrdersByUser(userId: Long): Flow<List<Order>> {
        return try {
            ordersDao.getOrdersByUser(userId)
        } catch (_: Exception) {
            flowOf(emptyList())
        }
    }
}