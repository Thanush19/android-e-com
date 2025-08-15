package com.example.ecommerce.fake

import com.example.ecommerce.data.db.entity.Order
import com.example.ecommerce.data.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeOrdersRepository : OrdersRepository {
    private val orders = mutableListOf<Order>()
    private var nextOrderId = 1L

    override suspend fun placeOrder(order: Order): Long {
        val orderWithId = order.copy(id = nextOrderId++)
        orders.add(orderWithId)
        return orderWithId.id
    }

    override fun getOrdersByUser(userId: Long): Flow<List<Order>> {
        val userOrders = orders.filter { it.userId == userId }
        return flowOf(userOrders)
    }

    fun addOrder(order: Order) {
        val orderWithId = order.copy(id = nextOrderId++)
        orders.add(orderWithId)
    }

    fun clearOrders() {
        orders.clear()
        nextOrderId = 1L
    }
}