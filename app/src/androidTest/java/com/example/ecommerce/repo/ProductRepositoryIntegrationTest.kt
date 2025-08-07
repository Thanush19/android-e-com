package com.example.ecommerce.repo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ecommerce.data.repository.ProductRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ProductRepositoryIntegrationTest  {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var productRepository: ProductRepository

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testGetAllProducts_success() = runBlocking {
        val mockResponse = """
            [
                {"id": 1, "title": "Product 1", "price": 10.0, "description": "Desc 1", "category": "Cat 1", "image": "img1.jpg", "rating": {"rate": 4.5, "count": 100}},
                {"id": 2, "title": "Product 2", "price": 20.0, "description": "Desc 2", "category": "Cat 2", "image": "img2.jpg", "rating": {"rate": 4.0, "count": 50}}
            ]
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(mockResponse).setResponseCode(200))

        val products = productRepository.getAllProducts()
        assertNotNull(products)
        assertEquals(2, products?.size)
        assertEquals("Product 1", products?.get(0)?.title)
        assertEquals(4.5, products?.get(0)?.rating?.rate)
        assertEquals("Product 2", products?.get(1)?.title)
    }

    @Test
    fun testGetAllProducts_failure() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val products = productRepository.getAllProducts()
        assertNull(products)
    }

    @Test
    fun testGetProductById_success() = runBlocking {
        val mockResponse = """
            {"id": 1, "title": "Product 1", "price": 10.0, "description": "Desc 1", "category": "Cat 1", "image": "img1.jpg", "rating": {"rate": 4.5, "count": 100}}
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(mockResponse).setResponseCode(200))

        val product = productRepository.getProductById(1)
        assertNotNull(product)
        assertEquals("Product 1", product?.title)
        assertEquals(10.0, product?.price)
        assertEquals(4.5, product?.rating?.rate)
    }

    @Test
    fun testGetProductById_failure() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))

        val product = productRepository.getProductById(999)
        assertNull(product)
    }
}