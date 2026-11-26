package com.fei.semestralnezadanie.frontend.welcome

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fei.semestralnezadanie.backend.datastore.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

// Stav obrazovky (UI State)
data class WelcomeUiState(
    val name: String = "",
    val avatarUri: String? = null,
    val avatarBorderColorKey: String? = null,
    val isCompleted: Boolean = false
) {
    // Tlačidlo "Pokračovať" je aktívne len vtedy, keď používateľ zadal meno
    val isValid: Boolean get() = name.trim().isNotEmpty()
}

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    // Aktualizácia zadaného mena
    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    // Aktualizácia zvoleného avatara (z galérie alebo predvoľby)
    fun onAvatarSelected(avatarUri: String) {
        _uiState.update { it.copy(avatarUri = avatarUri) }
    }

    // Aktualizácia zvolenej farby obrysu avatara
    fun onAvatarBorderColorSelected(colorKey: String) {
        _uiState.update { it.copy(avatarBorderColorKey = colorKey) }
    }

    // Uloženie do DataStore a dokončenie onboardingu
    fun saveProfileAndContinue() {
        val currentState = _uiState.value
        if (!currentState.isValid) return

        viewModelScope.launch {
            val currentLang = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                .substringBefore("-").substringBefore("_")
                .ifEmpty { Locale.getDefault().language.take(2) }

            userManager.saveUserProfile(
                name = currentState.name.trim(),
                avatarUri = currentState.avatarUri,
                avatarBorderColorKey = currentState.avatarBorderColorKey,
                languageCode = currentLang
            )
            userManager.setOnboardingCompleted(true)
            _uiState.update { it.copy(isCompleted = true) }
        }
    }
}