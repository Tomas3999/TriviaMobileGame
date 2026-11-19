package com.fei.semestralnezadanie.enums

import androidx.annotation.StringRes
import com.fei.semestralnezadanie.R

enum class GameMode(
    val id: Int,
    @StringRes val labelRes: Int
) {
    CLASSIC(0, R.string.lobby_mode_classic),
    MARATHON(1, R.string.lobby_mode_marathon);

    companion object {
        fun fromId(id: Int): GameMode =
            entries.find { it.id == id } ?: CLASSIC
    }
}