package com.fei.semestralnezadanie.backend.datastore

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
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
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Config.DataStore.PREFERENCES_NAME
)

class UserManager(private val context: Context) {
    companion object {
        private val KEY_USER_NAME = stringPreferencesKey(Config.DataStore.KEY_USER_NAME)
        private val KEY_AVATAR_URI = stringPreferencesKey(Config.DataStore.KEY_AVATAR_URI)
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey(Config.DataStore.KEY_IS_ONBOARDING_COMPLETED)
        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("key_notifications_enabled")
        private val KEY_LANGUAGE_CODE = stringPreferencesKey(Config.DataStore.KEY_LANGUAGE_CODE)
    }

    val userProfileFlow: Flow<User> = context.dataStore.data.map { preferences ->
        val savedLanguage = preferences[KEY_LANGUAGE_CODE]
        val currentAppLanguage = if (!savedLanguage.isNullOrEmpty()) {
            savedLanguage
        } else {
            val appLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
            appLocale.substringBefore("-").substringBefore("_").ifEmpty {
                Locale.getDefault().language.take(2)
            }
        }

        User(
            name = preferences[KEY_USER_NAME] ?: "",
            avatarUri = preferences[KEY_AVATAR_URI],
            isOnboardingCompleted = preferences[KEY_ONBOARDING_COMPLETED] ?: false,
            notificationsEnabled = preferences[KEY_NOTIFICATIONS_ENABLED] ?: Config.Notifications.NOTIFICATIONS_DEFAULT_ENABLED,
            languageCode = currentAppLanguage
        )
    }

    suspend fun saveUserProfile(
        name: String,
        avatarUri: String?,
        notificationsEnabled: Boolean? = null,
        languageCode: String? = null
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_NAME] = name
            if (avatarUri != null) {
                preferences[KEY_AVATAR_URI] = avatarUri
            } else {
                preferences.remove(KEY_AVATAR_URI)
            }
            if (notificationsEnabled != null) {
                preferences[KEY_NOTIFICATIONS_ENABLED] = notificationsEnabled
            }
            if (languageCode != null) {
                preferences[KEY_LANGUAGE_CODE] = languageCode
            }
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean = true) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setLanguageCode(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE_CODE] = languageCode
        }
    }
}
