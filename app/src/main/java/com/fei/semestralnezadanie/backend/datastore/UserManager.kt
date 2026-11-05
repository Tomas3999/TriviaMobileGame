package com.fei.semestralnezadanie.backend.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.backend.datastore.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Config.DataStore.PREFERENCES_NAME
)

class UserManager(private val context: Context) {
    companion object {
        private val KEY_USER_NAME = stringPreferencesKey(Config.DataStore.KEY_USER_NAME)
        private val KEY_AVATAR_URI = stringPreferencesKey(Config.DataStore.KEY_AVATAR_URI)
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey(Config.DataStore.KEY_IS_ONBOARDING_COMPLETED)
    }

    val userProfileFlow: Flow<User> = context.dataStore.data.map { preferences ->
        User(
            name = preferences[KEY_USER_NAME] ?: "",
            avatarUri = preferences[KEY_AVATAR_URI],
            isOnboardingCompleted = preferences[KEY_ONBOARDING_COMPLETED] ?: false
        )
    }

    suspend fun saveUserProfile(name: String, avatarUri: String?) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_NAME] = name
            if (avatarUri != null) {
                preferences[KEY_AVATAR_URI] = avatarUri
            } else {
                preferences.remove(KEY_AVATAR_URI)
            }
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean = true) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }
}