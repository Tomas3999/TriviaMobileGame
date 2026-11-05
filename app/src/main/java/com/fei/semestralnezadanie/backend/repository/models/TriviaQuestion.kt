package com.fei.semestralnezadanie.backend.repository.models

import com.fei.semestralnezadanie.backend.api.models.QuestionDto
import com.fei.semestralnezadanie.utils.decodeHtml

data class TriviaQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val category: String,
    val difficulty: String
)

fun QuestionDto.toDomain(): TriviaQuestion {
    val decodedQuestion = this.question.decodeHtml()
    val decodedCorrect = this.correctAnswer.decodeHtml()
    val decodedIncorrect = this.incorrectAnswers.map { it.decodeHtml() }

    val shuffledOptions = (decodedIncorrect + decodedCorrect).shuffled()
    val correctIndex = shuffledOptions.indexOf(decodedCorrect)

    return TriviaQuestion(
        question = decodedQuestion,
        options = shuffledOptions,
        correctAnswerIndex = correctIndex,
        category = this.category.decodeHtml(),
        difficulty = this.difficulty
    )
}