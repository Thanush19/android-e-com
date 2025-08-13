package com.example.ecommerce.views.auth

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
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit.rule
import org.mockito.junit.MockitoRule

@ExperimentalCoroutinesApi
class AuthViewModelTest {
    @get:Rule
    val mockitoRule: MockitoRule = rule()

    private lateinit var vm: AuthViewModel
    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var testScope: TestScope

    @Before
    fun setUp() {
        testScope = TestScope(testDispatcher)
        Dispatchers.setMain(testDispatcher)
        whenever(userPreferencesRepository.isLoggedIn).thenReturn(flowOf(false))
        vm = AuthViewModel(userRepository, userPreferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init checks auth state from user preferences`() = runTest(testDispatcher) {
        whenever(userPreferencesRepository.isLoggedIn).thenReturn(flowOf(false))
        vm = AuthViewModel(userRepository, userPreferencesRepository)

        assertEquals(false, vm.loggedIn.value)
    }

    @Test
    fun `login with empty username or password emits error state`() = runTest(testDispatcher) {
        vm.login("", "123")

        assertEquals(AuthState.Error("Username and password can't be empty"), vm.loginState.value)
    }

    @Test
    fun `login with valid credentials emits success state and updates loggedIn`() = runTest(testDispatcher) {
        val user = User(id = 1L, userName = "abc", password = "123")
        whenever(userRepository.loginUser("abc")).thenReturn(user)
        whenever(userPreferencesRepository.saveUserId(1L)).thenReturn(Unit)

        vm.login("abc", "123")

        assertEquals(AuthState.Loading, vm.loginState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Success, vm.loginState.value)
        assertTrue(vm.loggedIn.value)
        verify(userRepository, times(1)).loginUser("abc")
        verify(userPreferencesRepository, times(1)).saveUserId(1L)
    }

    @Test
    fun `login with invalid username emits error state`() = runTest(testDispatcher) {
        whenever(userRepository.loginUser("abcd")).thenReturn(null)

        vm.login("abcd", "123")

        assertEquals(AuthState.Loading, vm.loginState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Error("Invalid username or password"), vm.loginState.value)

    }

    @Test
    fun `login with wrong password emits error state`() = runTest(testDispatcher) {
        val user = User(id = 1, userName = "abc", password = "123")
        whenever(userRepository.loginUser("abc")).thenReturn(user)

        vm.login("abc", "1234")

        assertEquals(AuthState.Loading, vm.loginState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Error("Invalid username or password"), vm.loginState.value)
    }

    @Test
    fun `login with repository exception emits error state`() = runTest(testDispatcher) {
        whenever(userRepository.loginUser("abc")).thenThrow(RuntimeException("nw err"))

        vm.login("abc", "123")

        assertEquals(AuthState.Loading, vm.loginState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Error("Login failed: nw err"), vm.loginState.value)
    }

    @Test
    fun `register with empty fields emits error state`() = runTest(testDispatcher) {
        vm.register("", "123", "123")

        assertEquals(AuthState.Error("All fields are required"), vm.registerState.value)
    }

    @Test
    fun `register with mismatched passwords emits error state`() = runTest(testDispatcher) {
        vm.register("abc", "123", "1234")

        assertEquals(AuthState.Error("Passwords do not match"), vm.registerState.value)

    }

    @Test
    fun `register with existing username emits error state`() = runTest(testDispatcher) {
        whenever(userRepository.registerUser("abc", "123")).thenReturn(null)

        vm.register("abc", "123", "123")

        assertEquals(AuthState.Loading, vm.registerState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Error("Username already exists"), vm.registerState.value)

    }

    @Test
    fun `register with valid input emits success state and updates loggedIn`() = runTest(testDispatcher) {
        whenever(userRepository.registerUser("abc", "123")).thenReturn(1L)
        whenever(userPreferencesRepository.saveUserId(1L)).thenReturn(Unit)

        vm.register("abc", "123", "123")

        assertEquals(AuthState.Loading, vm.registerState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Success, vm.registerState.value)
        assertTrue(vm.loggedIn.value)

    }

    @Test
    fun `register with repository exception emits error state`() = runTest(testDispatcher) {
        whenever(userRepository.registerUser("abc", "123")).thenThrow(RuntimeException("Database error"))

        vm.register("abc", "123", "123")

        assertEquals(AuthState.Loading, vm.registerState.value)
        advanceUntilIdle()
        assertEquals(AuthState.Error("Registration failed: Database error"), vm.registerState.value)
    }
}

