package com.example.ecommerce.views.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import junit.framework.TestCase.*
import kotlin.collections.emptyList


@ExperimentalCoroutinesApi
class MyFeedViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var productRepository: ProductRepository

    private lateinit var vm: MyFeedViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testProduct = Product(
        id = 1,
        title = "p22",
        price = 99.99,
        description = "Tkjsf",
        category = "Test kjfdjs",
        image = "jdf.jpg",
        rating = Rating(rate = 4.5, count = 100)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        vm = MyFeedViewModel(productRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchAllProducts for vertical scroll - successful fetch updates products and loading state`() = runTest {
        val expectedProducts = listOf(testProduct)
        `when`(productRepository.getAllProducts()).thenReturn(expectedProducts)

        vm.fetchAllProducts(MyFeedViewModel.LayoutType.VERTICAL)
        advanceUntilIdle()
        assertFalse(vm.isLoadingVertical.first())
        assertEquals(expectedProducts, vm.verticalProducts.first())
        assertNull(vm.error.first())
    }

    @Test
    fun `fetchAllProducts for horizontal scroll - successful fetch updates products and loading state`() = runTest {
        val expectedProducts = listOf(testProduct)
        `when`(productRepository.getAllProducts()).thenReturn(expectedProducts)

        vm.fetchAllProducts(MyFeedViewModel.LayoutType.HORIZONTAL)

        advanceUntilIdle()
        assertFalse(vm.isLoadingHorizontal.first())
        assertEquals(expectedProducts, vm.horizontalProducts.first())
        assertNull(vm.error.first())
    }

    @Test
    fun `fetchAllProducts vertical - repository returns null, sets empty list`() = runTest {
        `when`(productRepository.getAllProducts()).thenReturn(null)

        vm.fetchAllProducts(MyFeedViewModel.LayoutType.VERTICAL)

        advanceUntilIdle()
        assertFalse(vm.isLoadingVertical.first())
        assertEquals(emptyList<Product>(), vm.verticalProducts.first())
        assertNull(vm.error.first())
    }

    @Test
    fun `fetchAllProducts horizontal - repository throws exception, sets error`() = runTest {
        val exceptionMessage = "Network error"
        `when`(productRepository.getAllProducts()).thenThrow(RuntimeException(exceptionMessage))

        vm.fetchAllProducts(MyFeedViewModel.LayoutType.HORIZONTAL)

        advanceUntilIdle()
        assertFalse(vm.isLoadingHorizontal.first())
        assertEquals(emptyList<Product>(), vm.horizontalProducts.first())
        assertEquals(exceptionMessage, vm.error.first())
    }

    @Test
    fun `fetchAllProducts - init calls both vertical and horizontal fetches`() = runTest {
        val expectedProducts = listOf(testProduct)
        `when`(productRepository.getAllProducts()).thenReturn(expectedProducts)

        vm = MyFeedViewModel(productRepository)

        advanceUntilIdle()
        assertEquals(expectedProducts, vm.verticalProducts.first())
        assertEquals(expectedProducts, vm.horizontalProducts.first())
    }

    @Test
    fun `fetchAllProducts vertical - multiple fetches append products`() = runTest {
        val firstBatch = listOf(testProduct)
        val secondBatch = listOf(testProduct.copy(id = 2))
        `when`(productRepository.getAllProducts())
            .thenReturn(firstBatch)
            .thenReturn(secondBatch)

        vm.fetchAllProducts(MyFeedViewModel.LayoutType.VERTICAL)
        advanceUntilIdle()
        vm.fetchAllProducts(MyFeedViewModel.LayoutType.VERTICAL)
        advanceUntilIdle()

        val expected = listOf(testProduct, testProduct.copy(id = 2))
        assertEquals(expected, vm.verticalProducts.first())
    }
}