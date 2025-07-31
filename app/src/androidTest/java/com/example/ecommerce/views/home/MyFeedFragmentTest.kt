package com.example.ecommerce.views.home

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ecommerce.R
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.data.model.Rating
import com.example.ecommerce.data.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

// Hilt module to provide mocked ViewModel
@Module
@InstallIn(SingletonComponent::class)
object TestModule {
    @Provides
    @Singleton
    fun provideMyFeedViewModel(
        productRepository: ProductRepository
    ): MyFeedViewModel {
        val viewModel = mockk<MyFeedViewModel>(relaxed = true)
        val mockProducts = listOf(
            Product(1, "Product C", 30.99, "Desc 1", "Cat 1", "img1.jpg", Rating(4.5, 100)),
            Product(2, "Product A", 10.49, "Desc 2", "Cat 2", "img2.jpg", Rating(3.8, 50)),
            Product(3, "Product B", 20.99, "Desc 3", "Cat 3", "img3.jpg", Rating(4.2, 75))
        )
        coEvery { productRepository.getAllProducts() } returns mockProducts
        every { viewModel.verticalProducts } returns MutableStateFlow(mockProducts)
        every { viewModel.horizontalProducts } returns MutableStateFlow(mockProducts)
        every { viewModel.isLoadingVertical } returns MutableStateFlow(false)
        every { viewModel.isLoadingHorizontal } returns MutableStateFlow(false)
        every { viewModel.sortOption } returns MutableStateFlow(null)
        return viewModel
    }
}

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MyFeedFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var mockNavController: NavController

    private val mockProducts = listOf(
        Product(1, "Product C", 30.99, "Desc 1", "Cat 1", "img1.jpg", Rating(4.5, 100)),
        Product(2, "Product A", 10.49, "Desc 2", "Cat 2", "img2.jpg", Rating(3.8, 50)),
        Product(3, "Product B", 20.99, "Desc 3", "Cat 3", "img3.jpg", Rating(4.2, 75))
    )

    @Before
    fun setup() {
        hiltRule.inject()
        mockNavController = mockk(relaxed = true)
    }

}