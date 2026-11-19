package com.fei.semestralnezadanie.frontend.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

enum class AvatarPreset(val key: String, val icon: ImageVector) {
    PERSON("preset_person", Icons.Default.Person),
    FACE("preset_face", Icons.Default.Face),
    STAR("preset_star", Icons.Default.Star);

    companion object {
        // Predvolená ikona, ak nič nie je vybraté
        val DEFAULT_ICON: ImageVector = Icons.Default.AccountCircle

        // Nájde ikonu podľa uloženého kľúča
        fun getIconByKey(key: String?): ImageVector {
            return entries.find { it.key == key }?.icon ?: DEFAULT_ICON
        }
    }
}