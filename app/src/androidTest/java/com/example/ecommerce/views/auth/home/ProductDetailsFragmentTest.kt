package com.example.ecommerce.views.auth.home

import androidx.fragment.app.FragmentFactory
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ecommerce.R
import com.example.ecommerce.launchFragmentInHiltContainer
import com.example.ecommerce.views.home.ProductDetailsFragment
import com.example.ecommerce.views.home.ProductDetailsFragmentArgs
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ProductDetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testAllUiElementsAreDisplayed() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val bundle = ProductDetailsFragmentArgs(productId = 1).toBundle()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.productDetailsFragment, bundle)
        }
        launchFragmentInHiltContainer<ProductDetailsFragment>(
            fragmentArgs = bundle,
            factory = FragmentFactory()
        ) {
            Navigation.setViewNavController(this.requireView(), navController)
        }
        onView(ViewMatchers.withId(R.id.tvProductTitle))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.tvProductPrice))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.tvProductDescription))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.tvProductCategory))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.tvProductRating))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.ivProductImage))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.btnBack))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.btnBuyNow))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testBuyNowButtonShowsConfirmationDialog() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val bundle = ProductDetailsFragmentArgs(productId = 1).toBundle()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.productDetailsFragment, bundle)
        }
        launchFragmentInHiltContainer<ProductDetailsFragment>(
            fragmentArgs = bundle,
            factory = FragmentFactory()
        ) {
            Navigation.setViewNavController(this.requireView(), navController)
        }
        onView(ViewMatchers.withId(R.id.btnBuyNow)).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.confirm_purchase_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(R.string.confirm_purchase_message))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(R.string.buy_now))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(R.string.cancel))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}