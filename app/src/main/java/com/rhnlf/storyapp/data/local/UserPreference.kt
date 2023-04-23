package com.rhnlf.storyapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUser(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                preferences[NAME_KEY] ?: "",
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY] ?: ""
            )
        }
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
        }
    }

    suspend fun deleteUser() {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = ""
            preferences[EMAIL_KEY] = ""
            preferences[TOKEN_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}