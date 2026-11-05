package com.fei.semestralnezadanie.backend.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Int,
    val name: String
)

@Serializable
data class CategoryResponseDto(
    @SerialName("trivia_categories")
    val categories: List<CategoryDto>
)