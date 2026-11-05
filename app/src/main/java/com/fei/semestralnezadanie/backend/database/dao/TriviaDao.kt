package com.fei.semestralnezadanie.backend.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.fei.semestralnezadanie.backend.database.entities.QuestionAnswer
import com.fei.semestralnezadanie.backend.database.entities.TriviaResult
import com.fei.semestralnezadanie.backend.database.entities.TriviaResultWithQuestions
import kotlinx.coroutines.flow.Flow

@Dao
interface TriviaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTriviaResult(result: TriviaResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionAnswers(answers: List<QuestionAnswer>)

    @Transaction
    suspend fun insertTriviaRecord(result: TriviaResult, answers: List<QuestionAnswer>) {
        val resultId = insertTriviaResult(result)
        val answersWithId = answers.map { it.copy(triviaResultId = resultId) }
        insertQuestionAnswers(answersWithId)
    }

    @Query("SELECT * FROM trivia_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<TriviaResult>>

    @Query("SELECT * FROM trivia_results ORDER BY correctAnswersCount DESC, durationSeconds ASC")
    fun getResultsSortedByScore(): Flow<List<TriviaResult>>

    @Query("SELECT * FROM trivia_results ORDER BY durationSeconds ASC")
    fun getResultsSortedByTime(): Flow<List<TriviaResult>>

    @Query("SELECT * FROM trivia_results WHERE category = :category ORDER BY timestamp DESC")
    fun getResultsByCategory(category: String): Flow<List<TriviaResult>>

    @Transaction
    @Query("SELECT * FROM trivia_results WHERE id = :quizId")
    fun getTriviaDetail(quizId: Long): Flow<TriviaResultWithQuestions?>

    @Query("SELECT MAX(correctAnswersCount) FROM trivia_results WHERE gameMode = 1")
    suspend fun getMarathonPersonalBest(): Int?
}
