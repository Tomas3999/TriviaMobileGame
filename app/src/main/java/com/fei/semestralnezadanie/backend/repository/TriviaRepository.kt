package com.fei.semestralnezadanie.backend.repository

import com.fei.semestralnezadanie.backend.api.TriviaApi
import com.fei.semestralnezadanie.backend.api.models.CategoryDto
import com.fei.semestralnezadanie.backend.database.dao.TriviaDao
import com.fei.semestralnezadanie.backend.database.entities.QuestionAnswer
import com.fei.semestralnezadanie.backend.database.entities.TriviaResult
import com.fei.semestralnezadanie.backend.database.entities.TriviaResultWithQuestions
import com.fei.semestralnezadanie.backend.repository.models.TriviaQuestion
import com.fei.semestralnezadanie.backend.repository.models.toDomain
import com.fei.semestralnezadanie.enums.ResponseCode
import kotlinx.coroutines.flow.Flow

/**
 * Vlastná výnimka pre API chyby.
 * Nenesie natvrdo text, ale typovo bezpečný ResponseCode so string resource ID.
 */
class ApiException(val responseCode: ResponseCode) : Exception("API Error code: ${responseCode.code}")

interface TriviaRepository {
    // 1. API operácie (Sieť)
    suspend fun getCategories(): Result<List<CategoryDto>>
    suspend fun getQuestions(
        amount: Int,
        categoryId: Int? = null,
        difficulty: String? = null,
        type: String? = null
    ): Result<List<TriviaQuestion>>

    // 2. Databázové operácie (Room)
    suspend fun saveQuizResult(result: TriviaResult, answers: List<QuestionAnswer>): Long
    fun getAllResults(): Flow<List<TriviaResult>>
    fun getQuizDetail(quizId: Long): Flow<TriviaResultWithQuestions?>
    suspend fun getMarathonPersonalBest(): Int?
}

/**
 * Konkrétna implementácia repozitára prepájajúca Retrofit a Room.
 */
class TriviaRepositoryImpl(
    private val apiService: TriviaApi,
    private val triviaDao: TriviaDao
) : TriviaRepository {

    // --- SIEŤOVÉ VOLANIA (RETROFIT) ---

    override suspend fun getCategories(): Result<List<CategoryDto>> {
        return runCatching {
            val response = apiService.getCategories()
            response.categories
        }
    }

    override suspend fun getQuestions(
        amount: Int,
        categoryId: Int?,
        difficulty: String?,
        type: String?
    ): Result<List<TriviaQuestion>> {
        return runCatching {
            val response = apiService.getQuestions(
                amount = amount,
                categoryId = categoryId,
                difficulty = difficulty,
                type = type
            )

            // Overenie kódu odpovede cez náš Enum
            val status = ResponseCode.fromCode(response.responseCode)

            if (status == ResponseCode.SUCCESS) {
                // Prevedieme surové DTO na čisté otázky (očistia sa HTML entity a zamiešajú sa možnosti)
                response.results.map { it.toDomain() }
            } else {
                // Vyhodíme typovo bezpečnú výnimku s ResponseCode bez hardcoded textu
                throw ApiException(status)
            }
        }
    }

    // --- LOKÁLNE VOLANIA (ROOM DATABÁZA) ---

    override suspend fun saveQuizResult(result: TriviaResult, answers: List<QuestionAnswer>): Long {
        // Atomický zápis do Roomu cez @Transaction v DAO
        return triviaDao.insertTriviaRecord(result, answers)
    }

    override fun getAllResults(): Flow<List<TriviaResult>> {
        return triviaDao.getAllResults()
    }

    override fun getQuizDetail(quizId: Long): Flow<TriviaResultWithQuestions?> {
        return triviaDao.getTriviaDetail(quizId)
    }

    override suspend fun getMarathonPersonalBest(): Int? {
        return triviaDao.getMarathonPersonalBest()
    }
}