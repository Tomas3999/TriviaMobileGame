package com.fei.semestralnezadanie.backend.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trivia_results",
    indices = [
        Index(value = ["category"]),
        Index(value = ["correctAnswersCount"]),
        Index(value = ["durationSeconds"])
    ]
)
data class TriviaResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val category: String,
    val difficulty: Int,
    val gameMode: Int,
    val durationSeconds: Long,
    val totalQuestionsCount: Int,
    val correctAnswersCount: Int,
    val incorrectAnswersCount: Int
)
