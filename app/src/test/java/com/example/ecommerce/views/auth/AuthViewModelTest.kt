package com.example.ecommerce.views.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ecommerce.data.db.entity.User
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import com.example.ecommerce.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import org.junit.Assert.*
import kotlinx.coroutines.test.StandardTestDispatcher

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AuthViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var testScope: TestScope

    @Before
    fun setUp() {
        testScope = TestScope(testDispatcher)
        Dispatchers.setMain(testDispatcher)
        userRepository = mock<UserRepository>()
        userPreferencesRepository = mock<UserPreferencesRepository>()
        whenever(userPreferencesRepository.isLoggedIn).thenReturn(flowOf(false))
        viewModel = AuthViewModel(userRepository, userPreferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init checks auth state from user preferences`() = runTest(testDispatcher) {
        whenever(userPreferencesRepository.isLoggedIn).thenReturn(flowOf(false))
        viewModel = AuthViewModel(userRepository, userPreferencesRepository)

        assertEquals(false, viewModel.loggedIn.value)
    }

    @Test
    fun `login with empty username or password emits error state`() = runTest(testDispatcher) {
        viewModel.login("", "123")

        assertEquals(AuthState.Error("Username and password can't be empty"), viewModel.loginState.value)
    }

    @Test
    fun `login with valid credentials emits success state and updates loggedIn`() = runTest(testDispatcher) {
        val user = User(id = 1L, userName = "abc", password = "123")
        whenever(userRepository.loginUser("abc")).thenReturn(user)
        whenever(userPreferencesRepository.saveUserId(1L)).thenReturn(Unit)

        viewModel.login("abc", "123")

        assertEquals(AuthState.Loading, viewModel.loginState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Success, viewModel.loginState.value)
        assertTrue(viewModel.loggedIn.value)
        verify(userRepository, times(1)).loginUser("abc")
        verify(userPreferencesRepository, times(1)).saveUserId(1L)
    }

    @Test
    fun `login with invalid username emits error state`() = runTest(testDispatcher) {
        whenever(userRepository.loginUser("abcd")).thenReturn(null)

        viewModel.login("abcd", "123")

        assertEquals(AuthState.Loading, viewModel.loginState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Error("Invalid username or password"), viewModel.loginState.value)

    }

    @Test
    fun `login with wrong password emits error state`() = runTest(testDispatcher) {
        val user = User(id = 1, userName = "abc", password = "123")
        whenever(userRepository.loginUser("abc")).thenReturn(user)

        viewModel.login("abc", "1234")

        assertEquals(AuthState.Loading, viewModel.loginState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Error("Invalid username or password"), viewModel.loginState.value)
    }

    @Test
    fun `login with repository exception emits error state`() = runTest(testDispatcher) {
        whenever(userRepository.loginUser("abc")).thenThrow(RuntimeException("nw err"))

        viewModel.login("abc", "123")

        assertEquals(AuthState.Loading, viewModel.loginState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Error("Login failed: nw err"), viewModel.loginState.value)
    }

    @Test
    fun `register with empty fields emits error state`() = runTest(testDispatcher) {
        viewModel.register("", "123", "123")

        assertEquals(AuthState.Error("All fields are required"), viewModel.registerState.value)
    }

    @Test
    fun `register with mismatched passwords emits error state`() = runTest(testDispatcher) {
        viewModel.register("abc", "123", "1234")

        assertEquals(AuthState.Error("Passwords do not match"), viewModel.registerState.value)

    }

    @Test
    fun `register with existing username emits error state`() = runTest(testDispatcher) {
        whenever(userRepository.registerUser("abc", "123")).thenReturn(null)

        viewModel.register("abc", "123", "123")

        assertEquals(AuthState.Loading, viewModel.registerState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Error("Username already exists"), viewModel.registerState.value)

    }

    @Test
    fun `register with valid input emits success state and updates loggedIn`() = runTest(testDispatcher) {
        whenever(userRepository.registerUser("abc", "123")).thenReturn(1L)
        whenever(userPreferencesRepository.saveUserId(1L)).thenReturn(Unit)

        viewModel.register("abc", "123", "123")

        assertEquals(AuthState.Loading, viewModel.registerState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Success, viewModel.registerState.value)
        assertTrue(viewModel.loggedIn.value)

    }

    @Test
    fun `register with repository exception emits error state`() = runTest(testDispatcher) {
        whenever(userRepository.registerUser("abc", "123")).thenThrow(RuntimeException("Database error"))

        viewModel.register("abc", "123", "123")

        assertEquals(AuthState.Loading, viewModel.registerState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Error("Registration failed: Database error"), viewModel.registerState.value)

    }
}

