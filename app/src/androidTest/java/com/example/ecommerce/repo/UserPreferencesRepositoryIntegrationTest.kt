package com.example.ecommerce.repo


import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UserPreferencesRepositoryIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: UserPreferencesRepository

    @Before
    fun setUp() = runTest {
        hiltRule.inject()
        repository.clearUserId()
    }

    @Test
    fun `saveUserId stores userId and retrieves it correctly`() = runTest {
        val userId = 123L

        repository.saveUserId(userId)

        val retrievedUserId = repository.userId.first()
        assertEquals(userId, retrievedUserId)
    }

    @Test
    fun `isLoggedIn returns true when userId is set`() = runTest {
        val userId = 456L
        repository.saveUserId(userId)

        val isLoggedIn = repository.isLoggedIn.first()

        assertTrue(isLoggedIn)
    }

    @Test
    fun `isLoggedIn returns false when userId is not set`() = runTest {

        val isLoggedIn = repository.isLoggedIn.first()

        assertFalse(isLoggedIn)
    }

    @Test
    fun `clearUserId removes userId and updates isLoggedIn`() = runTest {
        val userId = 789L
        repository.saveUserId(userId)

        repository.clearUserId()

        val retrievedUserId = repository.userId.first()
        val isLoggedIn = repository.isLoggedIn.first()
        assertNull(retrievedUserId)
        assertFalse(isLoggedIn)
    }

    @Test
    fun `userId returns null when no userId is set`() = runTest {

        val retrievedUserId = repository.userId.first()

        assertNull(retrievedUserId)
    }
}