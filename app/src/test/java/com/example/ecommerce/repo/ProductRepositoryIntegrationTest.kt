package com.example.ecommerce.repo

import com.example.ecommerce.data.api.ProductApiService
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.data.model.Rating
import com.example.ecommerce.data.repository.ProductRepository
import com.google.gson.Gson
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@HiltAndroidTest
class ProductRepositoryIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: ProductRepository

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ProductApiService

    @Before
    fun setUp() {
        hiltRule.inject()
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Override API service with MockWebServer URL
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApiService::class.java)

        // Re-inject repository with mocked API service
        repository = ProductRepository(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getAllProducts returns list of products on successful API response`() = runTest {
        // Arrange
        val products = listOf(
            Product(1, "Product 1", 10.0, "Desc 1", "Category", "image1.jpg", Rating(4.5, 100)),
            Product(2, "Product 2", 20.0, "Desc 2", "Category", "image2.jpg", Rating(4.0, 50))
        )
        mockWebServer.enqueue(
            MockResponse()
                .setBody(Gson().toJson(products))
                .setResponseCode(200)
        )

        // Act
        val result = repository.getAllProducts()

        // Assert
        assertNotNull(result)
        assertEquals(products, result)
    }

    @Test
    fun `getAllProducts returns null on API failure`() = runTest {
        // Arrange
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        // Act
        val result = repository.getAllProducts()

        // Assert
        assertNull(result)
    }

    @Test
    fun `getProductById returns product on successful API response`() = runTest {
        // Arrange
        val product = Product(1, "Product 1", 10.0, "Desc 1", "Category", "image1.jpg", Rating(4.5, 100))
        mockWebServer.enqueue(
            MockResponse()
                .setBody(Gson().toJson(product))
                .setResponseCode(200)
        )

        // Act
        val result = repository.getProductById(1)

        // Assert
        assertNotNull(result)
        assertEquals(product, result)
    }

    @Test
    fun `getProductById returns null on API failure`() = runTest {
        // Arrange
        mockWebServer.enqueue(MockResponse().setResponseCode(404))

        // Act
        val result = repository.getProductById(1)

        // Assert
        assertNull(result)
    }
}