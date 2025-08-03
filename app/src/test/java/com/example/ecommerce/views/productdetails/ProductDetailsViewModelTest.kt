package com.example.ecommerce.views.productdetails

import com.example.ecommerce.data.model.Product
import com.example.ecommerce.data.model.Rating
import com.example.ecommerce.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class ProductDetailsViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var productRepository: ProductRepository

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var testScope: TestScope
    private lateinit var vm: ProductDetailsViewModel

    @Before
    fun setUp() {
        testScope = TestScope(testDispatcher)
        Dispatchers.setMain(testDispatcher)
        vm = ProductDetailsViewModel(productRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getProductById returns product when available`() = runTest(testDispatcher) {
        val productId = 101
        val product = Product(
            id = productId,
            title = "Test Product",
            price = 99.99,
            description = "Test Description",
            category = "Test Category",
            image = "test.jpg",
            rating = Rating(rate = 4.5, count = 100)
        )
        whenever(productRepository.getProductById(productId)).thenReturn(product)
        val result = vm.getProductById(productId)
        advanceUntilIdle()
        assertEquals(product, result)
        assertNull(vm.error.first())
        verify(productRepository, times(1)).getProductById(productId)
    }

    @Test
    fun `getProductById returns null when product not found`() = runTest(testDispatcher) {
        val productId = 101
        whenever(productRepository.getProductById(productId)).thenReturn(null)
        val result = vm.getProductById(productId)
        advanceUntilIdle()
        assertNull(result)
        assertNull(vm.error.first())
        verify(productRepository, times(1)).getProductById(productId)
    }

    @Test
    fun `getProductById handles repository exception`() = runTest(testDispatcher) {
        val productId = 101
        whenever(productRepository.getProductById(productId)).thenThrow(RuntimeException("Product repo error"))
        val result = vm.getProductById(productId)
        advanceUntilIdle()
        assertNull(result)
        assertEquals("Failed to load product: Product repo error", vm.error.first())
        verify(productRepository, times(1)).getProductById(productId)
    }
}