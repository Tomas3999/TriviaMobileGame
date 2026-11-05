package com.fei.semestralnezadanie.backend.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class TriviaResultWithQuestions(
    @Embedded
    val triviaResult: TriviaResult,

    @Relation(
        parentColumn = "id",
        entityColumn = "triviaResultId"
    )
    val questions: List<QuestionAnswer>
)
