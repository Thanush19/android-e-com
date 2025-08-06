package com.example.ecommerce.views.home

import androidx.fragment.app.FragmentFactory
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ecommerce.R
import com.example.ecommerce.launchFragmentInHiltContainer
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
class MyProfileFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testUiElementsAreDisplayed() {
        launchFragmentInHiltContainer<MyProfileFragment>(
            factory = FragmentFactory()
        )

       onView(ViewMatchers.withId(R.id.tvProfileGreeting))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        onView(ViewMatchers.withId(R.id.tvMyOrders))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        onView(ViewMatchers.withId(R.id.tvLogout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testLogoutClickNavigatesToLoginFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.myProfileFragment)
        }

        launchFragmentInHiltContainer<MyProfileFragment>(
            factory = FragmentFactory()
        ) {
            Navigation.setViewNavController(this.requireView(), navController)
        }

        onView(ViewMatchers.withId(R.id.tvLogout)).perform(ViewActions.click())

        assert(navController.currentDestination?.id == R.id.loginFragment)
    }
}