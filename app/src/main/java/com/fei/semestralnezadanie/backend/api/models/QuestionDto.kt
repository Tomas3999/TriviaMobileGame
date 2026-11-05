package com.fei.semestralnezadanie.backend.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionDto(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    @SerialName("correct_answer")
    val correctAnswer: String,
    @SerialName("incorrect_answers")
    val incorrectAnswers: List<String>
)

@Serializable
data class QuestionResponseDto(
    @SerialName("response_code")
    val responseCode: Int,
    val results: List<QuestionDto>
)