package com.fei.semestralnezadanie.backend.datastore.models

data class User(
    val name: String = "",
    val avatarUri: String? = null,
    val isOnboardingCompleted: Boolean = false
)
