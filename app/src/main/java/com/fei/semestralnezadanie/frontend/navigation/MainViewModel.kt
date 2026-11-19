package com.fei.semestralnezadanie.frontend.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fei.semestralnezadanie.backend.datastore.UserManager
import com.fei.semestralnezadanie.backend.datastore.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {

    val userProfile: StateFlow<User?> = userManager.userProfileFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    fun updateProfile(
        name: String,
        avatarUri: String?,
        notificationsEnabled: Boolean,
        languageCode: String
    ) {
        viewModelScope.launch {
            userManager.saveUserProfile(
                name = name,
                avatarUri = avatarUri,
                notificationsEnabled = notificationsEnabled,
                languageCode = languageCode
            )
        }
    }
}