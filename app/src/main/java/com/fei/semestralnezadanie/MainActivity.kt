package com.fei.semestralnezadanie

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import com.fei.semestralnezadanie.frontend.components.AppLoader
import com.fei.semestralnezadanie.frontend.navigation.AppNavigation
import com.fei.semestralnezadanie.frontend.navigation.MainViewModel
import com.fei.semestralnezadanie.frontend.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

import com.fei.semestralnezadanie.ui.theme.SemestralneZadanieTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SemestralneZadanieTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val profileState by mainViewModel.userProfile.collectAsState()

                    when (val profile = profileState) {
                        null -> {
                            AppLoader()
                        }
                        else -> {
                            // Aplikovanie uloženého jazyka z DataStore pri štarte aplikácie
                            LaunchedEffect(profile.languageCode) {
                                val currentTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                                val currentLang = currentTag.substringBefore("-").substringBefore("_")
                                if (profile.languageCode.isNotEmpty() && currentLang != profile.languageCode) {
                                    AppCompatDelegate.setApplicationLocales(
                                        LocaleListCompat.forLanguageTags(profile.languageCode)
                                    )
                                }
                            }

                            val startDestination = if (profile.isOnboardingCompleted) {
                                Screen.Lobby.route
                            } else {
                                Screen.Welcome.route
                            }

                            AppNavigation(
                                startDestination = startDestination,
                                userProfile = profile,
                                onUpdateProfile = { name, avatarUri, avatarBorderColorKey, notificationsEnabled, languageCode ->
                                    mainViewModel.updateProfile(
                                        name = name,
                                        avatarUri = avatarUri,
                                        avatarBorderColorKey = avatarBorderColorKey,
                                        notificationsEnabled = notificationsEnabled,
                                        languageCode = languageCode
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}