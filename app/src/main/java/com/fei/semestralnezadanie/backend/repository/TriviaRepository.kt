package com.fei.semestralnezadanie.backend.repository

import com.fei.semestralnezadanie.backend.api.TriviaApi
import com.fei.semestralnezadanie.backend.api.models.CategoryDto
import com.fei.semestralnezadanie.backend.database.dao.TriviaDao
import com.fei.semestralnezadanie.backend.database.entities.CategoryCorrectCount
import com.fei.semestralnezadanie.backend.database.entities.CategoryInfo
import com.fei.semestralnezadanie.backend.database.entities.QuestionAnswer
import com.fei.semestralnezadanie.backend.database.entities.TriviaResult
import com.fei.semestralnezadanie.backend.database.entities.TriviaResultWithQuestions
import com.fei.semestralnezadanie.backend.repository.models.TriviaQuestion
import com.fei.semestralnezadanie.backend.repository.models.toDomain
import com.fei.semestralnezadanie.enums.ResponseCode
import kotlinx.coroutines.flow.Flow

class ApiException(val responseCode: ResponseCode) : Exception("API Error code: ${responseCode.code}")

interface TriviaRepository {
    suspend fun requestSessionToken(): Result<String>
    suspend fun getCategoryQuestionCount(categoryId: Int, difficulty: String?): Result<Int>
    suspend fun getCategories(): Result<List<CategoryDto>>
    suspend fun getQuestions(
        amount: Int,
        categoryId: Int? = null,
        difficulty: String? = null,
        type: String? = null,
        token: String? = null
    ): Result<List<TriviaQuestion>>

    suspend fun saveQuizResult(result: TriviaResult, answers: List<QuestionAnswer>): Long
    fun getAllResults(): Flow<List<TriviaResult>>
    fun getQuizDetail(quizId: Long): Flow<TriviaResultWithQuestions?>
    suspend fun getMarathonPersonalBest(): Int?

    fun getCategoryCorrectCounts(): Flow<List<CategoryCorrectCount>>
    fun getAllCategoryInfos(): Flow<List<CategoryInfo>>
    suspend fun saveCategoryTotalCount(categoryId: Int, categoryName: String, totalCount: Int)
    suspend fun syncMissingOrExpiredCategoryCounts(categoryNames: List<String>)
}

class TriviaRepositoryImpl(
    private val apiService: TriviaApi,
    private val triviaDao: TriviaDao
) : TriviaRepository {

    private var cachedCategories: List<CategoryDto>? = null

    override suspend fun requestSessionToken(): Result<String> {
        return runCatching {
            val response = apiService.requestToken()
            val status = ResponseCode.fromCode(response.responseCode)
            if (status == ResponseCode.SUCCESS && !response.token.isNullOrEmpty()) {
                response.token
            } else {
                throw ApiException(status)
            }
        }
    }

    override suspend fun getCategoryQuestionCount(categoryId: Int, difficulty: String?): Result<Int> {
        return runCatching {
            val counts = apiService.getCategoryQuestionCount(categoryId).counts
            when (difficulty) {
                "easy" -> counts.easy
                "medium" -> counts.medium
                "hard" -> counts.hard
                else -> counts.total
            }
        }
    }

    override suspend fun getCategories(): Result<List<CategoryDto>> {
        cachedCategories?.let { return Result.success(it) }

        return runCatching {
            val response = apiService.getCategories()
            response.categories.also { cachedCategories = it }
        }
    }

    override suspend fun getQuestions(
        amount: Int,
        categoryId: Int?,
        difficulty: String?,
        type: String?,
        token: String?
    ): Result<List<TriviaQuestion>> {
        return runCatching {
            val response = apiService.getQuestions(
                amount = amount,
                categoryId = categoryId,
                difficulty = difficulty,
                type = type,
                token = token
            )

            val status = ResponseCode.fromCode(response.responseCode)

            if (status == ResponseCode.SUCCESS) {
                response.results.map { it.toDomain() }
            } else {
                throw ApiException(status)
            }
        }
    }

    override suspend fun saveQuizResult(result: TriviaResult, answers: List<QuestionAnswer>): Long {
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

    override fun getCategoryCorrectCounts(): Flow<List<CategoryCorrectCount>> {
        return triviaDao.getCategoryCorrectCounts()
    }

    override fun getAllCategoryInfos(): Flow<List<CategoryInfo>> {
        return triviaDao.getAllCategoryInfos()
    }

    override suspend fun saveCategoryTotalCount(categoryId: Int, categoryName: String, totalCount: Int) {
        triviaDao.insertCategoryInfo(
            CategoryInfo(
                categoryName = categoryName,
                categoryId = categoryId,
                totalQuestionsCount = totalCount,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    /**
     * Skontroluje, pre ktoré odohrané kategórie ešte nepoznáme celkový počet otázok v API
     * (alebo sú staršie ako 7 dní), a potichu ich stiahne a uloží do Roomu.
     */
    override suspend fun syncMissingOrExpiredCategoryCounts(categoryNames: List<String>) {
        val now = System.currentTimeMillis()
        val sevenDaysMillis = java.util.concurrent.TimeUnit.DAYS.toMillis(7)

        // Zistíme, ktoré kategórie naozaj potrebujú stiahnutie
        val neededNames = categoryNames.filter { name ->
            val cached = triviaDao.getCategoryInfoByName(name)
            cached == null || (now - cached.lastUpdated) > sevenDaysMillis
        }

        if (neededNames.isEmpty()) return

        // Stiahneme zoznam kategórií pre preklad názvu -> ID
        val apiCategories = getCategories().getOrNull() ?: return

        for (name in neededNames) {
            val matchingCategory = apiCategories.find { it.name == name } ?: continue
            runCatching {
                val counts = apiService.getCategoryQuestionCount(matchingCategory.id).counts
                saveCategoryTotalCount(
                    categoryId = matchingCategory.id,
                    categoryName = name,
                    totalCount = counts.total
                )
            }
        }
    }
}