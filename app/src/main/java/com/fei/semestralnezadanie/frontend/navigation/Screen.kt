package com.fei.semestralnezadanie.frontend.navigation

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.net.URLEncoder

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome_screen")
    object Lobby : Screen("lobby_screen")
    // Sem neskôr pridáme Game, History, Settings
    object Game : Screen(
        "game_screen?categoryId={categoryId}&categoryName={categoryName}&difficulty={difficulty}&type={type}&gameModeId={gameModeId}"
    ) {
        val arguments: List<NamedNavArgument> = listOf(
            navArgument("categoryId") { type = NavType.IntType; defaultValue = -1 }, // -1 = Any kategória
            navArgument("categoryName") { type = NavType.StringType; defaultValue = "Any" },
            navArgument("difficulty") { type = NavType.StringType; nullable = true },
            navArgument("type") { type = NavType.StringType; nullable = true },
            navArgument("gameModeId") { type = NavType.IntType; defaultValue = 0 }
        )

        fun createRoute(
            categoryId: Int?,
            categoryName: String,
            difficulty: String?,
            type: String?,
            gameModeId: Int
        ): String {
            val catId = categoryId ?: -1 // Ak je null, pošleme -1
            val diff = difficulty ?: ""
            val t = type ?: ""
            val safeCategory = Uri.encode(categoryName)
            return "game_screen?categoryId=$catId&categoryName=$safeCategory&difficulty=$diff&type=$t&gameModeId=$gameModeId"
        }
    }

    object Result : Screen(
        "result_screen/{score}/{totalQuestions}/{durationSeconds}/{categoryName}/{isMarathon}/{isNewPersonalBest}/{quizId}"
    ) {
        val arguments: List<NamedNavArgument> = listOf(
            navArgument("score") { type = NavType.IntType },
            navArgument("totalQuestions") { type = NavType.IntType },
            navArgument("durationSeconds") { type = NavType.LongType },
            navArgument("categoryName") { type = NavType.StringType },
            navArgument("isMarathon") { type = NavType.BoolType },
            navArgument("isNewPersonalBest") { type = NavType.BoolType },
            navArgument("quizId") { type = NavType.LongType }
        )

        fun createRoute(
            score: Int,
            totalQuestions: Int,
            durationSeconds: Long,
            categoryName: String,
            isMarathon: Boolean,
            isNewPersonalBest: Boolean,
            quizId: Long
        ): String {
            val safeCategory = Uri.encode(categoryName)
            return "result_screen/$score/$totalQuestions/$durationSeconds/$safeCategory/$isMarathon/$isNewPersonalBest/$quizId"
        }
    }

    object Questions : Screen("questions_screen/{quizId}") {
        val arguments: List<NamedNavArgument> = listOf(
            navArgument("quizId") { type = NavType.LongType }
        )

        fun createRoute(quizId: Long): String = "questions_screen/$quizId"
    }

    object History : Screen("history_screen")

    object Challenges : Screen("challenges_screen")


}