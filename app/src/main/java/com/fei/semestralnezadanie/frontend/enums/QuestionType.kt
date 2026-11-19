package com.fei.semestralnezadanie.frontend.enums

import androidx.annotation.StringRes
import com.fei.semestralnezadanie.R

enum class QuestionType(val apiValue: String?, @StringRes val labelRes: Int) {
    ANY(null, R.string.lobby_type_any),
    MULTIPLE("multiple", R.string.lobby_type_multiple),
    BOOLEAN("boolean", R.string.lobby_type_boolean)
}