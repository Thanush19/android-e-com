package com.example.ecommerce.repo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ecommerce.data.db.LocalDB
import com.example.ecommerce.data.db.dao.UserDao
import com.example.ecommerce.data.db.entity.User
import com.example.ecommerce.data.repository.UserRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@HiltAndroidTest
class UserRepositoryIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var repository: UserRepository

    private lateinit var database: LocalDB

    @Before
    fun setUp() {
        hiltRule.inject()
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LocalDB::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `registerUser inserts new user and returns user ID`() = runTest {
        // Arrange
        val userName = "john_doe"
        val password = "pass123"

        // Act
        val userId = repository.registerUser(userName, password)

        // Assert
        assertNotNull(userId)
        val user = userDao.getUserById(userId)
        assertNotNull(user)
        assertEquals(userName, user.userName)
        assertEquals(password, user.password)
    }

    @Test
    fun `registerUser returns null for existing username`() = runTest {
        // Arrange
        val userName = "john_doe"
        val password = "pass123"
        userDao.insertUser(User(userName = userName, password = password))

        // Act
        val userId = repository.registerUser(userName, "new_pass")

        // Assert
        assertNull(userId)
    }

    @Test
    fun `loginUser retrieves user by username`() = runTest {
        // Arrange
        val user = User(userName = "john_doe", password = "pass123")
        val userId = userDao.insertUser(user)

        // Act
        val result = repository.loginUser("john_doe")

        // Assert
        assertNotNull(result)
        assertEquals(user.copy(id = userId), result)
    }

    @Test
    fun `loginUser returns null for non-existent username`() = runTest {
        // Act
        val result = repository.loginUser("unknown")

        // Assert
        assertNull(result)
    }

    @Test
    fun `getUserById retrieves user by ID`() = runTest {
        // Arrange
        val user = User(userName = "john_doe", password = "pass123")
        val userId = userDao.insertUser(user)

        // Act
        val result = repository.getUserById(userId)

        // Assert
        assertNotNull(result)
        assertEquals(user.copy(id = userId), result)
    }
}