package com.example.ecommerce.fake

import com.example.ecommerce.data.db.entity.User
import com.example.ecommerce.data.repository.UserRepository

class FakeUserRepository : UserRepository {
    private val users = mutableListOf<User>()
    private var idCounter = 1L

    override suspend fun registerUser(userName: String, password: String): Long? {
        if (users.any { it.userName == userName }) return null
        val user = User(id = idCounter++, userName = userName, password = password)
        users.add(user)
        return user.id
    }

    override suspend fun loginUser(userName: String): User? {
        return users.find { it.userName == userName }
    }

    override suspend fun getUserById(userId: Long): User? {
        return users.find { it.id == userId }
    }
}

