package com.example.ecommerce.views.auth

import android.text.InputType
import androidx.fragment.app.FragmentFactory
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ecommerce.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.ecommerce.launchFragmentInHiltContainer

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RegisterFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testAllUiElementsAreDisplayed() {
        launchFragmentInHiltContainer<RegisterFragment>(
            factory = FragmentFactory()
        )
        onView(withId(R.id.tvRegisterTitle)).check(matches(isDisplayed()))
            .check(matches(withText(R.string.register_title)))
        onView(withId(R.id.tilUsername)).check(matches(isDisplayed()))
        onView(withId(R.id.etUsername)).check(matches(isDisplayed()))
        onView(withId(R.id.tilPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.tilConfirmPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.etConfirmPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.btnRegister)).check(matches(isDisplayed()))
            .check(matches(withText(R.string.register_button)))
        onView(withId(R.id.tvLoginPrompt)).check(matches(isDisplayed()))
            .check(matches(withText(R.string.login_prompt)))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testRegisterButtonClickWithEmptyFieldsShowsError() {
        launchFragmentInHiltContainer<RegisterFragment>(
            factory = FragmentFactory()
        )
        onView(withId(R.id.btnRegister))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click())

    }

    @Test
    fun testRegisterButtonClickWithValidCredentials() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.loginFragment)
        }

        launchFragmentInHiltContainer<RegisterFragment>(
            factory = FragmentFactory()
        ){
            Navigation.setViewNavController(this.requireView(), navController)
        }

        onView(withId(R.id.etUsername)).perform(typeText("abc"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.etConfirmPassword)).perform(typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.btnRegister)).perform(click())
    }

    @Test
    fun testLoginPromptClickNavigatesToLoginFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.registerFragment)
        }
        launchFragmentInHiltContainer<RegisterFragment>(
            factory = FragmentFactory()
        ) {
            Navigation.setViewNavController(this.requireView(), navController)
        }
        onView(withId(R.id.tvLoginPrompt)).perform(click())
        assert(navController.currentDestination?.id == R.id.loginFragment)
    }

    @Test
    fun testPasswordFieldHidesTextByDefault() {
        launchFragmentInHiltContainer<RegisterFragment>(
            factory = FragmentFactory()
        )
        onView(withId(R.id.etPassword)).check(matches(withInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)))
    }

    @Test
    fun testConfirmPasswordFieldHidesTextByDefault() {
        launchFragmentInHiltContainer<RegisterFragment>(
            factory = FragmentFactory()
        )
        onView(withId(R.id.etConfirmPassword)).check(matches(withInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)))
    }

    @Test
    fun testInputFieldsClearErrorsWhenTextChanged() {
        launchFragmentInHiltContainer<RegisterFragment>(
            factory = FragmentFactory()
        )
        onView(withId(R.id.btnRegister)).perform(click())
        onView(withId(R.id.etUsername)).perform(typeText("a"), closeSoftKeyboard())
    }
}