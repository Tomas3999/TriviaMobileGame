package com.fei.semestralnezadanie.enums

import androidx.annotation.StringRes
import com.fei.semestralnezadanie.R
enum class ResponseCode(val code: Int, @StringRes val messageRes: Int) {
    SUCCESS(0, 0),
    NO_RESULTS(1, R.string.api_error_no_results),
    INVALID_PARAMETER(2, R.string.api_error_invalid_parameter),
    TOKEN_NOT_FOUND(3, R.string.api_error_token_not_found),
    TOKEN_EMPTY(4, R.string.api_error_token_empty),
    RATE_LIMIT(5, R.string.api_error_rate_limit),
    UNKNOWN(-1, R.string.api_error_unknown);

    companion object {
        fun fromCode(code: Int): ResponseCode =
            entries.find { it.code == code } ?: UNKNOWN
    }
}