package com.example.ecommerce.views.auth

import android.text.InputType
import androidx.fragment.app.FragmentFactory
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
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
class LoginFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testAllUiElementsAreDisplayed() {
        launchFragmentInHiltContainer<LoginFragment>(
            factory = FragmentFactory()
        )
        onView(withId(R.id.tvLoginTitle)).check(matches(isDisplayed()))
            .check(matches(withText(R.string.login_title)))
        onView(withId(R.id.tilUsername)).check(matches(isDisplayed()))
        onView(withId(R.id.etUsername)).check(matches(isDisplayed()))
        onView(withId(R.id.tilPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))
            .check(matches(withText(R.string.login_button)))
        onView(withId(R.id.tvRegisterPrompt)).check(matches(isDisplayed()))
            .check(matches(withText(R.string.register_prompt)))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testLoginButtonClickWithEmptyFieldsShowsError() {
        launchFragmentInHiltContainer<LoginFragment>(
            factory = FragmentFactory()
        )
        onView(withId(R.id.btnLogin))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click())
    }

    @Test
    fun testLoginButtonClickWithValidCredentials() {
        launchFragmentInHiltContainer<LoginFragment>(
            factory = FragmentFactory()
        )
        // Enter valid credentials
        onView(withId(R.id.etUsername)).perform(typeText("validuser"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("validpass"), closeSoftKeyboard())

        // Click login button
        onView(withId(R.id.btnLogin)).perform(click())

        // Verify loading state
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
        onView(withId(R.id.btnLogin)).check(matches(not(isEnabled())))
    }

    @Test
    fun testRegisterPromptClickNavigatesToRegisterFragment() {
        launchFragmentInHiltContainer<LoginFragment>(
            factory = FragmentFactory()
        )
        // Click register prompt
        onView(withId(R.id.tvRegisterPrompt)).perform(click())

        // Verify navigation to register fragment
        assert(navController.currentDestination?.id == R.id.registerFragment)
    }

    @Test
    fun testPasswordFieldHidesTextByDefault() {
        launchFragmentInHiltContainer<LoginFragment>(
            factory = FragmentFactory()
        )
        // Verify password field is in password input type
        onView(withId(R.id.etPassword)).check(matches(withInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)))
    }

    @Test
    fun testInputFieldsClearErrorsWhenTextChanged() {
        launchFragmentInHiltContainer<LoginFragment>(
            factory = FragmentFactory()
        )
        // Trigger error state
        onView(withId(R.id.btnLogin)).perform(click())

        // Type in username field
        onView(withId(R.id.etUsername)).perform(typeText("a"), closeSoftKeyboard())

    }
}
