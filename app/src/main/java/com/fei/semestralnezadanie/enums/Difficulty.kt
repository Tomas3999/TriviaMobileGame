package com.fei.semestralnezadanie.enums

import androidx.annotation.StringRes
import com.fei.semestralnezadanie.R

enum class Difficulty(
    val id: Int,
    val apiValue: String?,
    @StringRes val labelRes: Int
) {
    ANY(0, null, R.string.lobby_difficulty_any),
    EASY(1, "easy", R.string.lobby_difficulty_easy),
    MEDIUM(2, "medium", R.string.lobby_difficulty_medium),
    HARD(3, "hard", R.string.lobby_difficulty_hard);

    companion object {
        fun fromId(id: Int): Difficulty =
            entries.find { it.id == id } ?: ANY

        fun fromApiValue(value: String?): Difficulty =
            entries.find { it.apiValue == value } ?: ANY
    }
}