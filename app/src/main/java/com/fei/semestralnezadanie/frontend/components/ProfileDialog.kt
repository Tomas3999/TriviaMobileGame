package com.fei.semestralnezadanie.frontend.components

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.os.LocaleListCompat
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R

@Composable
fun ProfileDialog(
    currentName: String,
    currentAvatarUri: String?,
    currentNotificationsEnabled: Boolean,
    currentLanguageCode: String = Config.App.DEFAULT_LANGUAGE_CODE,
    onDismiss: () -> Unit,
    onSaveProfile: (newName: String, newAvatarUri: String?, notificationsEnabled: Boolean, languageCode: String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var selectedAvatarUri by remember { mutableStateOf(currentAvatarUri) }
    var notificationsEnabled by remember { mutableStateOf(currentNotificationsEnabled) }

    // Inicializácia vybraného jazyka z aktuálne načítaného profilu z DataStore
    var selectedLanguage by remember(currentLanguageCode) {
        mutableStateOf(currentLanguageCode)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.profile_dialog_title),
                fontSize = Config.UI.FONT_SIZE_TITLE,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            ProfileForm(
                name = name,
                avatarUri = selectedAvatarUri,
                onNameChange = { name = it },
                onAvatarChange = { selectedAvatarUri = it },
                notificationsEnabled = notificationsEnabled,
                onNotificationsChange = { notificationsEnabled = it },
                selectedLanguage = selectedLanguage,
                onLanguageChange = { newLang ->
                    selectedLanguage = newLang
                },
                showSettingsSection = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.trim().isNotEmpty()) {
                        onSaveProfile(name.trim(), selectedAvatarUri, notificationsEnabled, selectedLanguage)
                    }
                },
                enabled = name.trim().isNotEmpty(),
                shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_SMALL)
            ) {
                Text(
                    text = stringResource(R.string.profile_btn_save),
                    fontSize = Config.UI.FONT_SIZE_BODY_SMALL
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.profile_btn_cancel),
                    fontSize = Config.UI.FONT_SIZE_BODY_SMALL
                )
            }
        }
    )
}
