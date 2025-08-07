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
    fun testRegisterUser_success() = runTest {
        val userId = userRepository.registerUser("testuser", "password")
        assertNotNull(userId)

        val user = userRepository.getUserById(userId!!)
        assertNotNull(user)
        assertEquals("testuser", user?.userName)
        assertEquals("password", user?.password)
    }

    @Test
    fun testRegisterUser_duplicateUserName() = runTest {
        userRepository.registerUser("testuser", "password")
        val userId = userRepository.registerUser("testuser", "newpassword")
        assertNull(userId)
    }

    @Test
    fun testLoginUser_success() = runTest {
        userRepository.registerUser("testuser", "password")
        val user = userRepository.loginUser("testuser")
        assertNotNull(user)
        assertEquals("testuser", user?.userName)
    }

    @Test
    fun testLoginUser_invalidUserName() = runTest {
        val user = userRepository.loginUser("nonexistent")
        assertNull(user)
    }

    @Test
    fun testGetUserById_success() = runTest {
        val userId = userRepository.registerUser("testuser", "password")
        val user = userRepository.getUserById(userId!!)
        assertNotNull(user)
        assertEquals("testuser", user?.userName)
    }

    @Test
    fun testGetUserById_invalidId() = runTest {
        val user = userRepository.getUserById(999L)
        assertNull(user)
    }
}