package com.fei.semestralnezadanie.backend.api

import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.backend.api.models.CategoryResponseDto
import com.fei.semestralnezadanie.backend.api.models.QuestionResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApi {
    @GET(Config.Api.CATEGORIES_ENDPOINT)
    suspend fun getCategories(): CategoryResponseDto

    @GET(Config.Api.QUESTIONS_ENDPOINT)
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") categoryId: Int? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("type") type: String? = null
    ): QuestionResponseDto
}
