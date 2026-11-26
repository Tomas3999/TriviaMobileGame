package com.fei.semestralnezadanie.backend.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_info")
data class CategoryInfo(
    @PrimaryKey
    val categoryId: Int,
    val categoryName: String,
    val totalQuestionsCount: Int,
    val lastUpdated: Long
)