package com.example.ecommerce.views.auth.home


import androidx.fragment.app.FragmentFactory
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ecommerce.R
import com.example.ecommerce.launchFragmentInHiltContainer
import com.example.ecommerce.views.adapters.ProductAdapter
import com.example.ecommerce.views.home.MyFeedFragment
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
class MyFeedFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testUiElementsAreDisplayed() {
        launchFragmentInHiltContainer<MyFeedFragment>(
            factory = FragmentFactory()
        )

        onView(withId(R.id.ivFilter))
            .check(matches(isDisplayed()))

        onView(withId(R.id.rvHorizontalProducts))
            .check(matches(isDisplayed()))

        onView(withId(R.id.rvProducts))
            .check(matches(isDisplayed()))

        onView(withId(R.id.nestedScrollView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testFilterButtonShowsPopupMenu() {
        launchFragmentInHiltContainer<MyFeedFragment>(
            factory = FragmentFactory()
        )

        onView(withId(R.id.ivFilter)).perform(click())

        onView(withText(R.string.sort_price_asc))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testFilterSelectionUpdatesRecyclerView() {
        launchFragmentInHiltContainer<MyFeedFragment>(
            factory = FragmentFactory()
        )

        onView(withId(R.id.ivFilter)).perform(click())

        onView(withText(R.string.sort_price_asc)).perform(click())

        onView(withId(R.id.rvProducts))
            .check(matches(isDisplayed()))

        onView(withId(R.id.rvHorizontalProducts))
            .check(matches(isDisplayed()))
    }

}