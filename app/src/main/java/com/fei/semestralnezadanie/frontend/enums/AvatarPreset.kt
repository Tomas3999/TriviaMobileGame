package com.fei.semestralnezadanie.frontend.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.ui.graphics.vector.ImageVector

enum class AvatarPreset(val key: String, val icon: ImageVector) {
    MOOD("preset_mood", Icons.Rounded.Mood),
    PETS("preset_pets", Icons.Rounded.Pets),
    ROCKET("preset_rocket", Icons.Rounded.RocketLaunch);

    companion object {
        val DEFAULT_ICON: ImageVector = Icons.Rounded.AccountCircle

        fun getIconByKey(key: String?): ImageVector {
            return entries.find { it.key == key }?.icon ?: DEFAULT_ICON
        }
    }
}