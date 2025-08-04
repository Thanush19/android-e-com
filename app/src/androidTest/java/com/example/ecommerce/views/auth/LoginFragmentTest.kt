package com.example.ecommerce.views.auth

import androidx.fragment.app.FragmentFactory
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ecommerce.R
import com.example.ecommerce.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class LoginFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())


    private lateinit var viewModel: AuthViewModel
    private val loginStateFlow = MutableStateFlow<AuthState?>(null)

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
        onView(withId(R.id.btnLogin)).perform(click())
        loginStateFlow.tryEmit(AuthState.Error("Username and password can't be empty"))
        onView(withText("Username and password can't be empty")).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginButtonClickWithValidCredentialsNavigatesToMyFeed() {
        launchFragmentInHiltContainer<LoginFragment>(
            factory = FragmentFactory()
        )
        onView(withId(R.id.etUsername)).perform(typeText("testuser"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("password"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())

        loginStateFlow.tryEmit(AuthState.Loading)
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
        onView(withId(R.id.btnLogin)).check(matches(not(isEnabled())))

        loginStateFlow.tryEmit(AuthState.Success)
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.btnLogin)).check(matches(isEnabled()))
        assert(navController.currentDestination?.id == R.id.myFeedFragment)
    }

    @Test
    fun testLoginButtonClickWithInvalidCredentialsShowsError() {
        launchFragmentInHiltContainer<LoginFragment>(
            factory = FragmentFactory()
        )
        onView(withId(R.id.etUsername)).perform(typeText("wronguser"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("wrongpass"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())

        loginStateFlow.tryEmit(AuthState.Error("Invalid username or password"))
        onView(withText("Invalid username or password")).check(matches(isDisplayed()))
        assert(navController.currentDestination?.id == R.id.loginFragment)
    }

    @Test
    fun testRegisterPromptClickNavigatesToRegisterFragment() {
        launchFragmentInHiltContainer<LoginFragment>(
            factory = FragmentFactory()
        )
        onView(withId(R.id.tvRegisterPrompt)).perform(click())
        assert(navController.currentDestination?.id == R.id.registerFragment)
    }

    @Test
    fun testLoginButtonDisabledDuringLoadingState() {
        launchFragmentInHiltContainer<LoginFragment>(
            factory = FragmentFactory()
        )
        onView(withId(R.id.etUsername)).perform(typeText("testuser"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("password"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())

        loginStateFlow.tryEmit(AuthState.Loading)
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
        onView(withId(R.id.btnLogin)).check(matches(not(isEnabled())))
    }
}
