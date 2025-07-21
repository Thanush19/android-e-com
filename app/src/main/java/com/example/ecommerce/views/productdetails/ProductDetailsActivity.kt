package com.example.ecommerce.views.home

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import coil.load
import com.example.ecommerce.data.db.entity.Order
import com.example.ecommerce.data.db.entity.User
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import com.example.ecommerce.data.repository.OrdersRepository
import com.example.ecommerce.data.repository.ProductRepository
import com.example.ecommerce.databinding.ActivityProductDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding

    @Inject
    lateinit var productRepository: ProductRepository

    @Inject
    lateinit var ordersRepository: OrdersRepository

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    private val _currentUserId = MutableLiveData<Long?>()
    val currentUserId: LiveData<Long?> = _currentUserId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val productId = intent.getIntExtra("PRODUCT_ID", -1)


        lifecycleScope.launch {
            val product = productRepository.getProductById(productId)
            if (product != null) {

                binding.tvProductTitle.text = product.title
                binding.tvProductPrice.text = "Rs.${product.price}"
                binding.tvProductDescription.text = product.description
                binding.tvProductCategory.text = "Category: ${product.category}"
                binding.tvProductRating.text = "Rating: ${product.rating.rate} (${product.rating.count} reviews)"
                binding.ivProductImage.load(product.image) {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_menu_report_image)
                }
            } else {
                Toast.makeText(this@ProductDetailsActivity, "Failed to load product", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        binding.btnBuyNow.setOnClickListener {
            lifecycleScope.launch {
                // Get current user ID
                lifecycleScope.launch {
                    userPreferencesRepository.userId.collectLatest { userId ->
                        if (userId != null) {
                            _currentUserId.value = userId
                        } else {
                            _currentUserId.value = null
                        }
                    }
                }

                val productId = intent.getIntExtra("PRODUCT_ID", -1)
                if (productId != -1) {
                    val order = Order(
                        userId = _currentUserId.value!!,
                        productIds = listOf(productId)
                    )

                    // Insert order
                    val orderId = ordersRepository.placeOrder(order)

                    // Show confirmation
                    Toast.makeText(
                        this@ProductDetailsActivity,
                        "Order placed successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@ProductDetailsActivity,
                        "Invalid product",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}