package com.example.ecommerce.repo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ecommerce.data.db.LocalDB
import com.example.ecommerce.data.repository.UserRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UserRepositoryIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var database: LocalDB

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `register user and success `() = runTest {
        val userId = userRepository.registerUser("abc", "123")
        assertNotNull(userId)

        val user = userRepository.getUserById(userId!!)
        assertNotNull(user)
        assertEquals("abc", user?.userName)
        assertEquals("123", user?.password)
    }

    @Test
    fun `register user with existing username`() = runTest {
        userRepository.registerUser("abc", "123")
        val userId = userRepository.registerUser("abc", "123")
        assertNull(userId)
    }

    @Test
    fun `login user success`() = runTest {
        userRepository.registerUser("abc", "123")
        val user = userRepository.loginUser("abc")
        assertNotNull(user)
        assertEquals("abc", user?.userName)
    }

    @Test
    fun `login user with invalid name`() = runTest {
        val user = userRepository.loginUser("abcde")
        assertNull(user)
    }

    @Test
    fun `get user id`() = runTest {
        val userId = userRepository.registerUser("abc", "123")
        val user = userRepository.getUserById(userId!!)
        assertNotNull(user)
        assertEquals("abc", user?.userName)
    }

    @Test
    fun `get userId for invalid userId`() = runTest {
        val user = userRepository.getUserById(999L)
        assertNull(user)
    }
}