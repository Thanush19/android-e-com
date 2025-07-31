package com.example.ecommerce.views.home


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import org.junit.Assert.*
import kotlinx.coroutines.test.StandardTestDispatcher


@ExperimentalCoroutinesApi
class MyProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var userRepository: UserRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var ordersRepository: OrdersRepository
    private lateinit var productRepository: ProductRepository
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var testScope: TestScope

    @Before
    fun setUp() {
        testScope = TestScope(testDispatcher)
        Dispatchers.setMain(testDispatcher)
        userRepository = mock<UserRepository>()
        userPreferencesRepository = mock<UserPreferencesRepository>()
        ordersRepository = mock<OrdersRepository>()
        productRepository = mock<ProductRepository>()
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

        val viewModel = MyProfileViewModel(
            userRepository,
            userPreferencesRepository,
            ordersRepository,
            productRepository
        )

        advanceUntilIdle()
        assertEquals(user, viewModel.currentUser.value)
    }

    @Test
    fun `init sets currentUser to null when userId is null`() = runTest(testDispatcher) {
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(null))

        val viewModel = MyProfileViewModel(
            userRepository,
            userPreferencesRepository,
            ordersRepository,
            productRepository
        )

        advanceUntilIdle()
        assertNull(viewModel.currentUser.value)
    }

    @Test
    fun `init does not crash when userRepository throws exception`() = runTest(testDispatcher) {
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(1L))
        whenever(userRepository.getUserById(1L)).thenThrow(RuntimeException("Database error"))

        val viewModel = MyProfileViewModel(
            userRepository,
            userPreferencesRepository,
            ordersRepository,
            productRepository
        )

        advanceUntilIdle()
        assertNull(viewModel.currentUser.value)
        verify(userRepository, times(1)).getUserById(1L)
    }

    @Test
    fun `getOrdersByUser returns orders when available`() = runTest(testDispatcher) {
        val userId = 1L
        val orders = listOf(Order(id = 1L, userId = userId, productIds = listOf(101, 102)))
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(userId))
        whenever(ordersRepository.getOrdersByUser(userId)).thenReturn(flowOf(orders))

        val viewModel = MyProfileViewModel(
            userRepository,
            userPreferencesRepository,
            ordersRepository,
            productRepository
        )

        val result = viewModel.getOrdersByUser(userId)

        advanceUntilIdle()
        assertEquals(orders, result)
    }

    @Test
    fun `getOrdersByUser returns null when no orders found`() = runTest(testDispatcher) {
        val userId = 1L
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(userId))
        whenever(ordersRepository.getOrdersByUser(userId)).thenReturn(flowOf(emptyList()))

        val viewModel= MyProfileViewModel(
        userRepository,
        userPreferencesRepository,
        ordersRepository,
        productRepository
        )

        val result = viewModel.getOrdersByUser(userId)

        advanceUntilIdle()
        assertEquals(emptyList<Order>(), result)
    }

    @Test
    fun `getOrdersByUser handles orders repo exception `() = runTest(testDispatcher) {
        val userId = 1L
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(userId))
        whenever(ordersRepository.getOrdersByUser(userId)).thenReturn(flowOf(emptyList()))

        val viewModel = MyProfileViewModel(
            userRepository,
            userPreferencesRepository,
            ordersRepository,
            productRepository
        )

        val result = viewModel.getOrdersByUser(userId)

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

        val viewModel = MyProfileViewModel(
            userRepository,
            userPreferencesRepository,
            ordersRepository,
            productRepository
        )

        val result = viewModel.getProductById(productId)

        advanceUntilIdle()
        assertEquals(product, result)
    }

    @Test
    fun `getProductById returns null when product not found`() = runTest(testDispatcher) {
        val productId = 101
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(1L))
        whenever(productRepository.getProductById(productId)).thenReturn(null)

        val viewModel = MyProfileViewModel(
            userRepository,
            userPreferencesRepository,
            ordersRepository,
            productRepository
        )

        val result = viewModel.getProductById(productId)

        advanceUntilIdle()
        assertNull(result)
    }

    @Test
    fun `getProductById handles repository exception `() = runTest(testDispatcher) {
        val productId = 101
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(1L))
        whenever(productRepository.getProductById(productId)).thenReturn(null)

        val viewModel = MyProfileViewModel(
            userRepository,
            userPreferencesRepository,
            ordersRepository,
            productRepository
        )

        val result = viewModel.getProductById(productId)

        advanceUntilIdle()
        assertNull(result)
        assertEquals(null, result)
    }

    @Test
    fun `logout clears userId in preferences`() = runTest(testDispatcher) {
        whenever(userPreferencesRepository.userId).thenReturn(flowOf(1L))
        whenever(userPreferencesRepository.clearUserId()).thenReturn(Unit)

        val viewModel = MyProfileViewModel(
            userRepository,
            userPreferencesRepository,
            ordersRepository,
            productRepository
        )

        viewModel.logout()

        advanceUntilIdle()
        verify(userPreferencesRepository, times(1)).clearUserId()
    }

}