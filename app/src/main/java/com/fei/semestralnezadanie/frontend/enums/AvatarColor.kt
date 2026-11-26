package com.fei.semestralnezadanie.frontend.enums

import androidx.compose.ui.graphics.Color

enum class AvatarColor(val key: String, val color: Color) {
    INDIGO("color_indigo", Color(0xFF6366F1)),
    EMERALD("color_emerald", Color(0xFF10B981)),
    AMBER("color_amber", Color(0xFFF59E0B)),
    ROSE("color_rose", Color(0xFFF43F5E)),
    CYAN("color_cyan", Color(0xFF06B6D4)),
    PURPLE("color_purple", Color(0xFFA855F7));

    companion object {
        fun getColorByKey(key: String?): Color? {
            return entries.find { it.key == key }?.color
        }
    }
}