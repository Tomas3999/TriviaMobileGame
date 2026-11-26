package com.fei.semestralnezadanie.frontend.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.frontend.enums.AvatarColor
import com.fei.semestralnezadanie.frontend.enums.AvatarPreset
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@Composable
fun ProfileForm(
    name: String,
    avatarUri: String?,
    onNameChange: (String) -> Unit,
    onAvatarChange: (String) -> Unit,
    avatarBorderColorKey: String? = null,
    onAvatarBorderColorChange: (String) -> Unit = {},
    notificationsEnabled: Boolean = true,
    onNotificationsChange: (Boolean) -> Unit = {},
    selectedLanguage: String = "sk",
    onLanguageChange: (String) -> Unit = {},
    showSettingsSection: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let {
                val savedFileUri = saveImageToInternalStorage(context, it)
                if (savedFileUri != null) {
                    onAvatarChange(savedFileUri)
                } else {
                    onAvatarChange(it.toString())
                }
            }
        }
    )

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            onNotificationsChange(isGranted)
        }
    )

    val currentBorderColor = AvatarColor.getColorByKey(avatarBorderColorKey)
        ?: MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_MEDIUM)
    ) {
        // 1. Sekcia avatara a výberu farby obrysu
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val openGallery = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }

            // Veľký avatar + ikonka na galériu v rohu
            Box(contentAlignment = Alignment.BottomEnd) {
                UserAvatar(
                    avatarUri = avatarUri,
                    size = Config.UI.AVATAR_SIZE_LARGE,
                    borderColor = currentBorderColor,
                    onClick = openGallery
                )

                Surface(
                    onClick = openGallery,
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = currentBorderColor,
                    shadowElevation = 4.dp,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.surface)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = stringResource(R.string.welcome_btn_gallery),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Pilulka s predvoľbami ikon + tlačidlom farby obrysu
            var colorMenuExpanded by remember { mutableStateOf(false) }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Predvoľby ikon avatara
                    AvatarPreset.entries.forEach { preset ->
                        val isSelected = avatarUri == preset.key

                        Box(contentAlignment = Alignment.BottomEnd) {
                            UserAvatar(
                                avatarUri = preset.key,
                                size = Config.UI.AVATAR_SIZE_SMALL,
                                borderColor = if (isSelected) currentBorderColor
                                else MaterialTheme.colorScheme.outlineVariant,
                                onClick = { onAvatarChange(preset.key) }
                            )

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(currentBorderColor)
                                        .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Jemný zvislý oddeľovač
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    // Farebné tlačidlo + popup rozbaľovaný VŽDY DOĽAVA
                    Box {
                        val density = LocalDensity.current
                        val marginPx = with(density) { 6.dp.roundToPx() }

                        val leftAlignedPositionProvider = remember(marginPx) {
                            object : PopupPositionProvider {
                                override fun calculatePosition(
                                    anchorBounds: IntRect,
                                    windowSize: IntSize,
                                    layoutDirection: LayoutDirection,
                                    popupContentSize: IntSize
                                ): IntOffset {
                                    // Pravý okraj popupu = pravý okraj tlačidla -> rozbalí sa DOĽAVA
                                    val x = (anchorBounds.right - popupContentSize.width)
                                        .coerceAtLeast(0)

                                    val yBelow = anchorBounds.bottom + marginPx
                                    val y = if (yBelow + popupContentSize.height <= windowSize.height) {
                                        yBelow
                                    } else {
                                        (anchorBounds.top - popupContentSize.height - marginPx)
                                            .coerceAtLeast(0)
                                    }
                                    return IntOffset(x, y)
                                }
                            }
                        }

                        Surface(
                            onClick = { colorMenuExpanded = true },
                            modifier = Modifier.size(Config.UI.AVATAR_SIZE_SMALL),
                            shape = CircleShape,
                            color = currentBorderColor,
                            border = BorderStroke(2.5.dp, MaterialTheme.colorScheme.surface),
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = if (avatarBorderColorKey == null) MaterialTheme.colorScheme.onPrimary
                                    else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        if (colorMenuExpanded) {
                            Popup(
                                popupPositionProvider = leftAlignedPositionProvider,
                                onDismissRequest = { colorMenuExpanded = false },
                                properties = PopupProperties(focusable = true)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_MEDIUM),
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 3.dp,
                                    shadowElevation = 6.dp,
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                                        AvatarColorPicker(
                                            selectedColorKey = avatarBorderColorKey,
                                            onColorSelected = {
                                                onAvatarBorderColorChange(it)
                                                colorMenuExpanded = false
                                            },
                                            columns = 3
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 2. Pole na meno
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.welcome_name_label)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_MEDIUM)
        )

        // 3. NASTAVENIA: Notifikácie a Jazyk
        if (showSettingsSection) {
            var languageMenuExpanded by remember { mutableStateOf(false) }

            val currentLangCode = remember(selectedLanguage) {
                Config.App.SUPPORTED_LANGUAGES.firstOrNull {
                    selectedLanguage.startsWith(it)
                } ?: selectedLanguage
            }

            val currentLanguageName = remember(currentLangCode) {
                val locale = Locale.forLanguageTag(currentLangCode)
                locale.getDisplayLanguage(locale)
                    .replaceFirstChar { it.uppercase() }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_MEDIUM),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Notifikácie
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.settings_notifications_title),
                                fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                                fontWeight = FontWeight.Medium,
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
                            onCheckedChange = { isChecked ->
                                if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (!hasPermission) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        onNotificationsChange(true)
                                    }
                                } else {
                                    onNotificationsChange(isChecked)
                                }
                            }
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    // Jazyk (rozbaľovacie menu)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_language_title),
                            fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Box {
                            Surface(
                                onClick = { languageMenuExpanded = true },
                                shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_SMALL),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = currentLanguageName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = languageMenuExpanded,
                                onDismissRequest = { languageMenuExpanded = false }
                            ) {
                                Config.App.SUPPORTED_LANGUAGES.forEach { langCode ->
                                    val isSelected = selectedLanguage.startsWith(langCode)
                                    val nativeLanguageName = remember(langCode) {
                                        val locale = Locale.forLanguageTag(langCode)
                                        locale.getDisplayLanguage(locale)
                                            .replaceFirstChar { it.uppercase() }
                                    }

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = nativeLanguageName,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        trailingIcon = if (isSelected) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        } else null,
                                        onClick = {
                                            languageMenuExpanded = false
                                            onLanguageChange(langCode)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.filesDir, "avatar_user.jpg")
        FileOutputStream(file).use { outputStream ->
            inputStream.use { input ->
                input.copyTo(outputStream)
            }
        }
        Uri.fromFile(file).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private val PreviewRegistryOwner = object : ActivityResultRegistryOwner {
    override val activityResultRegistry = object : ActivityResultRegistry() {
        override fun <I, O> onLaunch(
            requestCode: Int,
            contract: ActivityResultContract<I, O>,
            input: I,
            options: ActivityOptionsCompat?
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileFormPreview() {
    var name by remember { mutableStateOf("Janko") }
    var avatarUri by remember { mutableStateOf<String?>(AvatarPreset.MOOD.key) }
    var borderColorKey by remember { mutableStateOf<String?>(AvatarColor.INDIGO.key) }
    var notifications by remember { mutableStateOf(true) }
    var language by remember { mutableStateOf("sk") }

    CompositionLocalProvider(LocalActivityResultRegistryOwner provides PreviewRegistryOwner) {
        Surface(modifier = Modifier.padding(16.dp)) {
            ProfileForm(
                name = name,
                avatarUri = avatarUri,
                onNameChange = { name = it },
                onAvatarChange = { avatarUri = it },
                avatarBorderColorKey = borderColorKey,
                onAvatarBorderColorChange = { borderColorKey = it },
                notificationsEnabled = notifications,
                onNotificationsChange = { notifications = it },
                selectedLanguage = language,
                onLanguageChange = { language = it },
                showSettingsSection = true
            )
        }
    }
}