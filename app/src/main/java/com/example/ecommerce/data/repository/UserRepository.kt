package com.example.ecommerce.data.repository

import com.example.ecommerce.data.db.entity.User

interface UserRepository {
    suspend fun registerUser(userName: String, password: String): Long?
    suspend fun loginUser(userName: String): User?
    suspend fun getUserById(userId: Long): User?
}
