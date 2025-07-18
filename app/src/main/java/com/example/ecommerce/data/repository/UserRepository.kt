package com.example.ecommerce.data.repository

import com.example.ecommerce.data.db.dao.UserDao
import com.example.ecommerce.data.db.entity.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    suspend fun registerUser(userName: String, password: String): Long? {
        val existingUser = userDao.getUserByUserName(userName)
        if (existingUser != null) {
            return null
        }
        val newUser = User(userName = userName, password = password)
        return userDao.insertUser(newUser)
    }

    suspend fun loginUser(userName: String, password: String): User? {
        val user = userDao.getUserByUserName(userName)
        return user
    }

    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }
}