package com.bangkit.compends.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {
    private val ISLOGIN = stringPreferencesKey("user")

    fun getUser(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[ISLOGIN]
        }
    }

    suspend fun saveUser(token: String) {
        dataStore.edit { preferences ->
            preferences[ISLOGIN] = token
        }
    }

    suspend fun destroyUser() {
        dataStore.edit { preferences ->
            preferences.remove(ISLOGIN)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}