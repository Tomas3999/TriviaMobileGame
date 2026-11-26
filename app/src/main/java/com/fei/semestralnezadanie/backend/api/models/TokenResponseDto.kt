package com.fei.semestralnezadanie.backend.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponseDto(
    @SerialName("response_code")
    val responseCode: Int,
    @SerialName("token")
    val token: String? = null
)