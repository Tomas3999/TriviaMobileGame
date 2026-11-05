package com.fei.semestralnezadanie.backend.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "question_answers",
    foreignKeys = [
        ForeignKey(
            entity = TriviaResult::class,
            parentColumns = ["id"],
            childColumns = ["triviaResultId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["triviaResultId"])
    ]
)
data class QuestionAnswer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val triviaResultId: Long,
    val questionText: String,
    val options: List<String>,
    val selectedAnswer: Int?,
    val correctAnswer: Int,
    val timeSpentSeconds: Long
)