package com.fei.semestralnezadanie.backend.datastore.models

import com.fei.semestralnezadanie.Config

data class User(
    val name: String = "",
    val avatarUri: String? = null,
    val isOnboardingCompleted: Boolean = false,
    val notificationsEnabled: Boolean = Config.Notifications.NOTIFICATIONS_DEFAULT_ENABLED,
    val languageCode: String = Config.App.DEFAULT_LANGUAGE_CODE
)
