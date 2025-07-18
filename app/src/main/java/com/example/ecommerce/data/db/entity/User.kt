package com.example.ecommerce.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a user in the database
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userName: String,
    val password: String
)