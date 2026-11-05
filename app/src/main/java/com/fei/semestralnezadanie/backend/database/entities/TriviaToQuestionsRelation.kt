package com.fei.semestralnezadanie.backend.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class TriviaToQuestionsRelation(
    @Embedded
    val triviaResult: TriviaResult,

    @Relation(
        parentColumn = "id",
        entityColumn = "triviaResultId"
    )
    val questions: List<QuestionAnswer>
)