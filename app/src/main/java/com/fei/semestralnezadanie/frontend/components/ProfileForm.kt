package com.fei.semestralnezadanie.frontend.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.frontend.enums.AvatarPreset

@Composable
fun ProfileForm(
    name: String,
    avatarUri: String?,
    onNameChange: (String) -> Unit,
    onAvatarChange: (String) -> Unit,
    notificationsEnabled: Boolean = true,
    onNotificationsChange: (Boolean) -> Unit = {},
    selectedLanguage: String = "sk",
    onLanguageChange: (String) -> Unit = {},
    showSettingsSection: Boolean = false,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let { onAvatarChange(it.toString()) }
        }
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_MEDIUM)
    ) {
        // 1. Náhľad avatara
        UserAvatar(
            avatarUri = avatarUri,
            size = Config.UI.AVATAR_SIZE_LARGE
        )

        // 2. Tlačidlo pre galériu
        SecondaryButton(
            text = stringResource(R.string.welcome_btn_gallery),
            onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )

        // 3. Riadok s predvoľbami avatarov
        Row(
            horizontalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarPreset.entries.forEach { preset ->
                val isSelected = avatarUri == preset.key
                Box(
                    modifier = Modifier
                        .size(Config.UI.AVATAR_SIZE_SMALL)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onAvatarChange(preset.key) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = preset.icon,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Config.UI.SPACING_SMALL))

        // 4. Pole na meno
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.welcome_name_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_MEDIUM)
        )

        // 5. NASTAVENIA: Notifikácie a Jazyk
        if (showSettingsSection) {
            Spacer(modifier = Modifier.height(Config.UI.SPACING_SMALL))
            HorizontalDivider()

            // Prepínač pre Notifikácie
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.settings_notifications_title),
                        fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.settings_notifications_desc),
                        fontSize = Config.UI.FONT_SIZE_CAPTION,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = onNotificationsChange
                )
            }

            // Prepínač pre Jazyk (číta z Config.SUPPORTED_LANGUAGES)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings_language_title),
                    fontSize = Config.UI.FONT_SIZE_BODY_LARGE
                )

                Row(horizontalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)) {
                    Config.App.SUPPORTED_LANGUAGES.forEach { langCode ->
                        // Automatický rodný názov jazyka (sk -> Slovenčina, en -> English)
                        val nativeLanguageName = remember(langCode) {
                            java.util.Locale(langCode).getDisplayLanguage(java.util.Locale(langCode))
                                .replaceFirstChar { it.uppercase() }
                        }

                        FilterChip(
                            selected = selectedLanguage.startsWith(langCode),
                            onClick = { onLanguageChange(langCode) },
                            label = {
                                Text(
                                    text = nativeLanguageName,
                                    fontSize = Config.UI.FONT_SIZE_CAPTION
                                )
                            },
                            shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_SMALL)
                        )
                    }
                }
            }
        }
    }
}