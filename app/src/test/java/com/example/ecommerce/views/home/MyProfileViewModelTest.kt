package com.example.ecommerce.views.home

import com.example.ecommerce.data.db.entity.Order
import com.example.ecommerce.data.db.entity.User
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.data.model.Rating
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import com.example.ecommerce.data.repository.OrdersRepository
import com.example.ecommerce.data.repository.ProductRepository
import com.example.ecommerce.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class MyProfileViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var userRepository: UserRepository
    @Mock
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    @Mock
    private lateinit var ordersRepository: OrdersRepository
    @Mock
    private lateinit var productRepository: ProductRepository

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var testScope: TestScope
    private lateinit var vm: MyProfileViewModel

    @Before
    fun setUp() {
        testScope = TestScope(testDispatcher)
        Dispatchers.setMain(testDispatcher)
        vm = MyProfileViewModel(
            userRepository,
            userPreferencesRepository,
            ordersRepository,
            productRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads user data when userId is available`() = runTest(testDispatcher) {
        val user = User(id = 1L, userName = "abc", password = "123")
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(1L))
        whenever(userRepository.getUserById(1L)).thenReturn(user)
        vm.loadUserData()
        advanceUntilIdle()
        assertEquals(user, vm.currentUser.first())
    }

    @Test
    fun `init sets currentUser to null when userId is null`() = runTest(testDispatcher) {
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(null))
        vm.loadUserData()
        advanceUntilIdle()
        assertNull(vm.currentUser.first())
    }

    @Test
    fun `init does not crash when userRepository throws exception`() = runTest(testDispatcher) {
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(1L))
        whenever(userRepository.getUserById(1L)).thenThrow(RuntimeException("Database error"))
        vm.loadUserData()
        advanceUntilIdle()
        assertNull(vm.currentUser.first())
        verify(userRepository, times(1)).getUserById(1L)
    }

    @Test
    fun `getOrdersByUser returns orders when available`() = runTest(testDispatcher) {
        val userId = 1L
        val orders = listOf(Order(id = 1L, userId = userId, productIds = listOf(101, 102)))
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(userId))
        whenever(ordersRepository.getOrdersByUser(userId)).thenReturn(flowOf(orders))
        val result = vm.getOrdersByUser(userId)
        advanceUntilIdle()
        assertEquals(orders, result)
    }

    @Test
    fun `getOrdersByUser returns empty list when no orders found`() = runTest(testDispatcher) {
        val userId = 1L
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(userId))
        whenever(ordersRepository.getOrdersByUser(userId)).thenReturn(flowOf(emptyList()))
        val result = vm.getOrdersByUser(userId)
        advanceUntilIdle()
        assertEquals(emptyList<Order>(), result)
    }

    @Test
    fun `getOrdersByUser handles orders repo exception`() = runTest(testDispatcher) {
        val userId = 1L
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(userId))
        whenever(ordersRepository.getOrdersByUser(userId)).thenReturn(flowOf(emptyList()))
        val result = vm.getOrdersByUser(userId)
        advanceUntilIdle()
        assertEquals(emptyList<Order>(), result)
    }

    @Test
    fun `getProductById returns product when available`() = runTest(testDispatcher) {
        val productId = 101
        val product = Product(
            id = productId,
            title = "p22",
            price = 99.99,
            description = "Tkjsf",
            category = "Test kjfdjs",
            image = "jdf.jpg",
            rating = Rating(rate = 4.5, count = 100)
        )
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(1L))
        whenever(productRepository.getProductById(productId)).thenReturn(product)
        val result = vm.getProductById(productId)
        advanceUntilIdle()
        assertEquals(product, result)
    }

    @Test
    fun `getProductById returns null when product not found`() = runTest(testDispatcher) {
        val productId = 101
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(1L))
        whenever(productRepository.getProductById(productId)).thenReturn(null)
        val result = vm.getProductById(productId)
        advanceUntilIdle()
        assertNull(result)
    }

    @Test
    fun `getProductById handles repository exception`() = runTest(testDispatcher) {
        val productId = 101
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(1L))
        whenever(productRepository.getProductById(productId)).thenThrow(RuntimeException("Product repo error"))
        val result = vm.getProductById(productId)
        advanceUntilIdle()
        assertNull(result)
    }

    @Test
    fun `logout clears userId in preferences`() = runTest(testDispatcher) {
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(1L))
        whenever(userPreferencesRepository.clearUserId()).thenReturn(Unit)
        vm.logout()
        advanceUntilIdle()
        verify(userPreferencesRepository, times(1)).clearUserId()
    }
}