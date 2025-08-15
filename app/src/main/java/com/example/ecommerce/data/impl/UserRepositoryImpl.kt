package com.example.ecommerce.data.impl

import com.example.ecommerce.data.db.dao.UserDao
import com.example.ecommerce.data.db.entity.User
import com.example.ecommerce.data.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun registerUser(userName: String, password: String): Long? {
        val existingUser = userDao.getUserByUserName(userName)
        if (existingUser != null) {
            return null
        }
        val newUser = User(userName = userName, password = password)
        return userDao.insertUser(newUser)
    }

    override suspend fun loginUser(userName: String): User? {
        return userDao.getUserByUserName(userName)
    }

    override suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }
}