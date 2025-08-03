package com.example.ecommerce

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.example.ecommerce.views.home.TempFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TempFragmentTest {

    @Test
    fun checkText() {
        val scenario = launchFragmentInContainer<TempFragment>()
        onView(withText("hiii")).check(matches(isDisplayed()))
    }
}