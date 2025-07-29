package com.example.ecommerce.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val SORT_OPTION_KEY = intPreferencesKey("sort_option")
    }

    val userId: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    val isLoggedIn: Flow<Boolean> = userId.map { id ->
        id != null
    }

    val sortOption: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[SORT_OPTION_KEY]
    }

    suspend fun saveUserId(userId: Long) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    suspend fun clearUserId() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
        }
    }

    suspend fun saveSortOption(sortOption: Int) {
        context.dataStore.edit { preferences ->
            preferences[SORT_OPTION_KEY] = sortOption
        }
    }

    suspend fun clearSortOption() {
        context.dataStore.edit { preferences ->
            preferences.remove(SORT_OPTION_KEY)
        }
    }
}