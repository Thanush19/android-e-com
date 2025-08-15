package com.example.ecommerce.repo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ecommerce.data.db.dao.UserDao
import com.example.ecommerce.data.db.entity.Order
import com.example.ecommerce.data.db.entity.User
import com.example.ecommerce.data.repository.OrdersRepository
import com.example.ecommerce.fake.FakeOrdersRepository
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
    lateinit var userDao: UserDao

    @Inject
    lateinit var repo: OrdersRepository

    @Before
    fun setUp() = runTest {
        hiltRule.inject()
        userDao.insertUser(User(id = 1L, userName = "u1", password = "u1@gmail.com"))
    }

    @Test
    fun `placeOrder insert order and return orderId`() = runTest {
        val order = Order(userId = 1L, productIds = listOf(1, 2))

        val orderId = repo.placeOrder(order)

        assertNotNull(orderId)
        val orders = repo.getOrdersByUser(1L).first()
        assertEquals(listOf(order.copy(id = orderId)), orders)
    }

    @Test
    fun `get OrdersByUser returnsOrders For GivenUserId`() = runTest {
        val o1 = Order(userId = 1L, productIds = listOf(1, 2))
        val o2 = Order(userId = 1L, productIds = listOf(3))
        repo.placeOrder(o1)
        repo.placeOrder(o2)

        val orders = repo.getOrdersByUser(1L).first()
        assertEquals(2, orders.size)
        assertEquals(listOf(1L, 1L), orders.map { it.userId })
    }

    @Test
    fun `get OrdersByUser returns EmptyList For NonExistent UserId`() = runTest {
        val orders = repo.getOrdersByUser(999L).first()
        assertEquals(emptyList<Order>(), orders)
    }
}