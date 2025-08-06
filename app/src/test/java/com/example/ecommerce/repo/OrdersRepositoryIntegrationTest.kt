package com.example.ecommerce.repo

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ecommerce.data.db.LocalDB
import com.example.ecommerce.data.db.dao.OrdersDao
import com.example.ecommerce.data.db.entity.Order
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import com.example.ecommerce.data.repository.OrdersRepository


@HiltAndroidTest
class OrdersRepositoryIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var ordersDao: OrdersDao

    @Inject
    lateinit var repository:OrdersRepository

    @Inject
     lateinit var database: LocalDB

    @Before
    fun setUp() {
        hiltRule.inject()
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LocalDB::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `placeOrder inserts order and returns order ID`() = runTest {
        val order = Order(userId = 1L, productIds = listOf(1, 2))

        val orderId = repository.placeOrder(order)

        assertNotNull(orderId)
        val orders = ordersDao.getOrdersByUser(1L).first()
        assertEquals(listOf(order.copy(id = orderId)), orders)
    }

    @Test
    fun `getOrdersByUser returns orders for given user ID`() = runTest {
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
    fun `getOrdersByUser returns empty list for non-existent user ID`() = runTest {
        val orders = repository.getOrdersByUser(999L).first()
        assertEquals(emptyList(), orders)
    }
}