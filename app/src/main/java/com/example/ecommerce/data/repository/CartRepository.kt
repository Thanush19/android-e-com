package com.example.ecommerce.data.repository

import com.example.ecommerce.data.db.dao.CartDao
import com.example.ecommerce.data.db.entity.Cart
import kotlinx.coroutines.flow.Flow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Repository for managing Cart data
 */
class CartRepository(private val cartDao: CartDao) {

}
