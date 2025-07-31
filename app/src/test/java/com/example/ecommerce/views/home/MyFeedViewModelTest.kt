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
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import com.example.ecommerce.R


@ExperimentalCoroutinesApi
class MyFeedViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var productRepo: ProductRepository

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
        vm = MyFeedViewModel(productRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchAllProducts for vertical scroll - successful fetch updates products and loading state`() = runTest {
        val expectedProducts = listOf(testProduct)
        `when`(productRepo.getAllProducts()).thenReturn(expectedProducts)

        vm.fetchAllProducts(MyFeedViewModel.LayoutType.VERTICAL)
        advanceUntilIdle()

        assertFalse(vm.isLoadingVertical.first())
        assertEquals(expectedProducts, vm.verticalProducts.first())
        assertNull(vm.error.first())
    }

    @Test
    fun `fetchAllProducts for horizontal scroll - successful fetch updates products and loading state`() = runTest {
        val expectedProducts = listOf(testProduct)
        `when`(productRepo.getAllProducts()).thenReturn(expectedProducts)

        vm.fetchAllProducts(MyFeedViewModel.LayoutType.HORIZONTAL)
        advanceUntilIdle()

        assertFalse(vm.isLoadingHorizontal.first())
        assertEquals(expectedProducts, vm.horizontalProducts.first())
        assertNull(vm.error.first())
    }

    @Test
    fun `fetchAllProducts vertical - repository returns null, sets empty list`() = runTest {
        `when`(productRepo.getAllProducts()).thenReturn(null)

        vm.fetchAllProducts(MyFeedViewModel.LayoutType.VERTICAL)

        assertFalse(vm.isLoadingVertical.first())
        assertEquals(emptyList<Product>(), vm.verticalProducts.first())
        assertNull(vm.error.first())
    }

    @Test
    fun `fetchAllProducts horizontal - repository throws exception, sets error`() = runTest {
        val exceptionMsg = "nw err"
        `when`(productRepo.getAllProducts()).thenThrow(RuntimeException(exceptionMsg))

        vm.fetchAllProducts(MyFeedViewModel.LayoutType.HORIZONTAL)

        assertFalse(vm.isLoadingHorizontal.first())
        assertEquals(emptyList<Product>(), vm.horizontalProducts.first())
        assertEquals(exceptionMsg, vm.error.first())
    }

    @Test
    fun `fetchAllProducts - init calls both vertical and horizontal fetches`() = runTest {
        val expectedProducts = listOf(testProduct)
        `when`(productRepo.getAllProducts()).thenReturn(expectedProducts)

        vm = MyFeedViewModel(productRepo)

        assertEquals(expectedProducts, vm.verticalProducts.first())
        assertEquals(expectedProducts, vm.horizontalProducts.first())
        assertFalse(vm.isLoadingVertical.first())
        assertFalse(vm.isLoadingHorizontal.first())
        assertNull(vm.error.first())
    }

    @Test
    fun `fetchAllProducts vertical - multiple fetches append products`() = runTest {
        val firstBatch = listOf(testProduct)
        val secondBatch = listOf(testProduct.copy(id = 2, title = "pro 2"))
        `when`(productRepo.getAllProducts())
            .thenReturn(firstBatch)
            .thenReturn(secondBatch)

        vm.fetchAllProducts(MyFeedViewModel.LayoutType.VERTICAL)
        vm.fetchAllProducts(MyFeedViewModel.LayoutType.VERTICAL)

        val expected = listOf(testProduct, testProduct.copy(id = 2, title = "pro 2"))
        assertEquals(expected, vm.verticalProducts.first())
        assertFalse(vm.isLoadingVertical.first())
        assertNull(vm.error.first())
    }

    @Test
    fun `setSortOption - sets sort option correctly`() = runTest {
        val sortOption = R.id.sort_price_asc
        vm.setSortOption(sortOption)
        assertEquals(sortOption, vm.sortOption.first())
    }

    @Test
    fun `setSortOption - clears sort option when null is passed`() = runTest {
        vm.setSortOption(R.id.sort_price_desc)
        assertEquals(R.id.sort_price_desc, vm.sortOption.first())
        vm.setSortOption(null)
        assertNull(vm.sortOption.first())
    }

    @Test
    fun `setSortOption - multiple calls update sort option correctly`() = runTest {
        val sortOption1 = R.id.sort_price_asc
        val sortOption2 = R.id.sort_name_asc

        vm.setSortOption(sortOption1)
        assertEquals(sortOption1, vm.sortOption.first())

        vm.setSortOption(sortOption2)
        assertEquals(sortOption2, vm.sortOption.first())
    }
}