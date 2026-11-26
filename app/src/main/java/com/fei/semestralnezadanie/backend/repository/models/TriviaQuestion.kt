package com.fei.semestralnezadanie.backend.repository.models

import com.fei.semestralnezadanie.backend.api.models.QuestionDto
import com.fei.semestralnezadanie.utils.decodeHtml

data class TriviaQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val category: String,
    val difficulty: String,
    val type: String // "multiple" alebo "boolean"
)

fun QuestionDto.toDomain(): TriviaQuestion {
    val decodedQuestion = this.question.decodeHtml()
    val decodedCorrect = this.correctAnswer.decodeHtml()
    val decodedIncorrect = this.incorrectAnswers.map { it.decodeHtml() }

    val finalOptions = if (this.type == "boolean") {
        listOf("True", "False")
    } else {
        (decodedIncorrect + decodedCorrect).shuffled()
    }

    val correctIndex = finalOptions.indexOfFirst { it.equals(decodedCorrect, ignoreCase = true) }
        .coerceAtLeast(0)

    return TriviaQuestion(
        question = decodedQuestion,
        options = finalOptions,
        correctAnswerIndex = correctIndex,
        category = this.category.decodeHtml(),
        difficulty = this.difficulty,
        type = this.type
    )
}