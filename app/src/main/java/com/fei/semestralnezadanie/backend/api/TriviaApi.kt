package com.fei.semestralnezadanie.backend.api

import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.backend.api.models.CategoryCountResponseDto
import com.fei.semestralnezadanie.backend.api.models.CategoryResponseDto
import com.fei.semestralnezadanie.backend.api.models.QuestionResponseDto
import com.fei.semestralnezadanie.backend.api.models.TokenResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApi {
    @GET(Config.Api.CATEGORIES_ENDPOINT)
    suspend fun getCategories(): CategoryResponseDto

    @GET(Config.Api.TOKEN_ENDPOINT)
    suspend fun requestToken(
        @Query("command") command: String = "request"
    ): TokenResponseDto

    @GET(Config.Api.COUNT_ENDPOINT)
    suspend fun getCategoryQuestionCount(
        @Query("category") categoryId: Int
    ): CategoryCountResponseDto

    @GET(Config.Api.QUESTIONS_ENDPOINT)
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") categoryId: Int? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("type") type: String? = null,
        @Query("token") token: String? = null
    ): QuestionResponseDto
}
