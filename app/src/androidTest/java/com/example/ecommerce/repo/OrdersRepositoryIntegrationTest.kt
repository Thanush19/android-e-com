package com.example.ecommerce.repo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ecommerce.data.db.dao.OrdersDao
import com.example.ecommerce.data.db.dao.UserDao
import com.example.ecommerce.data.db.entity.Order
import com.example.ecommerce.data.db.entity.User
import com.example.ecommerce.data.repository.OrdersRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OrdersRepositoryIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var ordersDao: OrdersDao

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var repository: OrdersRepository

    @Before
    fun setUp() = runTest {
        hiltRule.inject()
        userDao.insertUser(User( 1L,   "User1", "user1@example.com"))
        userDao.insertUser(User(2L, "User2","user2@example.com"))
    }

    @Test
    fun placeOrder_insertsOrderAndReturnsOrderId() = runTest {
        val order = Order(userId = 1L, productIds = listOf(1, 2))

        val orderId = repository.placeOrder(order)

        assertNotNull(orderId)
        val orders = ordersDao.getOrdersByUser(1L).first()
        assertEquals(listOf(order.copy(id = orderId)), orders)
    }

    @Test
    fun getOrdersByUser_returnsOrdersForGivenUserId() = runTest {
        val order1 = Order(userId = 1L, productIds = listOf(1, 2))
        val order2 = Order(userId = 1L, productIds = listOf(3))
        val order3 = Order(userId = 2L, productIds = listOf(4))
        ordersDao.insertOrder(order1)
        ordersDao.insertOrder(order2)
        ordersDao.insertOrder(order3)

        val orders = repository.getOrdersByUser(1L).first()
        assertEquals(2, orders.size)
        assertEquals(listOf(1L, 1L), orders.map { it.userId })
    }

    @Test
    fun getOrdersByUser_returnsEmptyListForNonExistentUserId() = runTest {
        val orders = repository.getOrdersByUser(999L).first()
        assertEquals(emptyList<Order>(), orders)
    }
}