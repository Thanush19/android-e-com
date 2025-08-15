package com.example.ecommerce.fake

import com.example.ecommerce.data.model.Product
import com.example.ecommerce.data.model.Rating
import com.example.ecommerce.data.repository.ProductRepository

class FakeProductRepository : ProductRepository {
    private val products = mutableListOf<Product>().apply {
        add(Product(id = 1, title = "Product 1", price = 10.0, description = "Description 1", category = "Category 1", image = "image1.jpg", rating = Rating(4.5, 100)))
        add(Product(id = 2, title = "Product 2", price = 20.0, description = "Description 2", category = "Category 2", image = "image2.jpg", rating = Rating(4.0, 50)))
        add(Product(id = 3, title = "Product 3", price = 30.0, description = "Description 3", category = "Category 3", image = "image3.jpg", rating = Rating(3.5, 75)))
    }

    override suspend fun getAllProducts(): List<Product>? {
        return products.ifEmpty { null }
    }

    override suspend fun getProductById(id: Int): Product? {
        return products.find { it.id == id }
    }

    fun addProduct(product: Product) {
        products.add(product)
    }

    fun clearProducts() {
        products.clear()
    }
}