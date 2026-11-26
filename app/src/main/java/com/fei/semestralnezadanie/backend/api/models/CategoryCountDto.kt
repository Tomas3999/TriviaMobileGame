package com.fei.semestralnezadanie.backend.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryCountResponseDto(
    @SerialName("category_id")
    val categoryId: Int,
    @SerialName("category_question_count")
    val counts: CategoryQuestionCountDto
)

@Serializable
data class CategoryQuestionCountDto(
    @SerialName("total_question_count")
    val total: Int,
    @SerialName("total_easy_question_count")
    val easy: Int,
    @SerialName("total_medium_question_count")
    val medium: Int,
    @SerialName("total_hard_question_count")
    val hard: Int
)