package com.example.ecommerce.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@HiltAndroidTest
class UserPreferencesRepositoryIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: UserPreferencesRepository

    private lateinit var dataStore: DataStore<Preferences>

    @Before
    fun setUp() {
        hiltRule.inject()
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        dataStore = androidx.datastore.preferences.core.PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("test_user_prefs")
        }
        repository = UserPreferencesRepository(context)
    }

    @Test
    fun `saveUserId stores user ID in DataStore`() = runTest {
        repository.saveUserId(1L)

        val userId = repository.userId.first()
        assertEquals(1L, userId)
    }

    @Test
    fun `clearUserId removes user ID from DataStore`() = runTest {
        repository.saveUserId(1L)

        repository.clearUserId()

        val userId = repository.userId.first()
        assertEquals(null, userId)
    }

    @Test
    fun `isLoggedIn returns true when user ID is set`() = runTest {
        repository.saveUserId(1L)

        val isLoggedIn = repository.isLoggedIn.first()
        assertTrue(isLoggedIn)
    }

    @Test
    fun `isLoggedIn returns false when user ID is not set`() = runTest {
        val isLoggedIn = repository.isLoggedIn.first()
        assertFalse(isLoggedIn)
    }
}