package com.fei.semestralnezadanie.frontend.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.backend.datastore.models.User
import com.fei.semestralnezadanie.frontend.challenges.ChallengesPage
import com.fei.semestralnezadanie.frontend.components.ProfileDialog
import com.fei.semestralnezadanie.frontend.components.UserAvatar
import com.fei.semestralnezadanie.frontend.game.GamePage
import com.fei.semestralnezadanie.frontend.game.questions.QuestionsPage
import com.fei.semestralnezadanie.frontend.game.result.ResultPage
import com.fei.semestralnezadanie.frontend.history.HistoryPage
import com.fei.semestralnezadanie.frontend.lobby.LobbyPage
import com.fei.semestralnezadanie.frontend.welcome.WelcomePage
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    startDestination: String,
    userProfile: User,
    onUpdateProfile: (name: String, avatarUri: String?, notificationsEnabled: Boolean, languageCode: String) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showProfileDialog by remember { mutableStateOf(false) }

    val showBars = currentRoute in listOf(
        Screen.Lobby.route,
        Screen.History.route,
        Screen.Challenges.route
    )

    val topBarTitleRes = when (currentRoute) {
        Screen.History.route -> R.string.history_title
        Screen.Challenges.route -> R.string.nav_challenges
        else -> R.string.app_name
    }

    Scaffold(
        topBar = {
            if (showBars) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(topBarTitleRes),
                            fontSize = Config.UI.FONT_SIZE_TITLE
                        )
                    },
                    actions = {
                        UserAvatar(
                            modifier = Modifier.padding(end = Config.UI.SPACING_MEDIUM),
                            avatarUri = userProfile.avatarUri,
                            size = Config.UI.AVATAR_SIZE_SMALL,
                            onClick = { showProfileDialog = true }
                        )
                    }
                )
            }
        },
        bottomBar = {
            if (showBars) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo(Screen.Lobby.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Welcome Page
            composable(route = Screen.Welcome.route) {
                WelcomePage(
                    onContinueToLobby = {
                        navController.navigate(Screen.Lobby.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            // 2. ZÁLOŽKA 1: Lobby Page
            composable(route = Screen.Lobby.route) {
                LobbyPage(
                    onStartQuiz = { categoryId, categoryName, difficulty, type, gameModeId ->
                        val route = Screen.Game.createRoute(
                            categoryId = categoryId,
                            categoryName = categoryName,
                            difficulty = difficulty,
                            type = type,
                            gameModeId = gameModeId
                        )
                        navController.navigate(route)
                    }
                )
            }

            // 3. ZÁLOŽKA 2: History Page
            composable(route = Screen.History.route) {
                HistoryPage(
                    onQuizClick = { quizId ->
                        navController.navigate(Screen.Questions.createRoute(quizId))
                    }
                )
            }

            // 4. ZÁLOŽKA 3: Challenges Page
            composable(route = Screen.Challenges.route) {
                ChallengesPage()
            }

            // 5. Game Page
            composable(
                route = Screen.Game.route,
                arguments = Screen.Game.arguments
            ) { backStackEntry ->
                val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Any"

                GamePage(
                    onGameOver = { score, totalQuestions, durationSeconds, isMarathon, isNewPersonalBest, quizId ->
                        val resultRoute = Screen.Result.createRoute(
                            score = score,
                            totalQuestions = totalQuestions,
                            durationSeconds = durationSeconds,
                            categoryName = categoryName,
                            isMarathon = isMarathon,
                            isNewPersonalBest = isNewPersonalBest,
                            quizId = quizId
                        )
                        navController.navigate(resultRoute) {
                            popUpTo(Screen.Game.route) { inclusive = true }
                        }
                    },
                    onAbortClassicGame = {
                        navController.popBackStack()
                    }
                )
            }

            // 6. Result Page
            composable(
                route = Screen.Result.route,
                arguments = Screen.Result.arguments
            ) { backStackEntry ->
                val score = backStackEntry.arguments?.getInt("score") ?: 0
                val totalQuestions = backStackEntry.arguments?.getInt("totalQuestions") ?: 0
                val durationSeconds = backStackEntry.arguments?.getLong("durationSeconds") ?: 0L
                val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Any"
                val isMarathon = backStackEntry.arguments?.getBoolean("isMarathon") ?: false
                val isNewPersonalBest = backStackEntry.arguments?.getBoolean("isNewPersonalBest") ?: false
                val quizId = backStackEntry.arguments?.getLong("quizId") ?: -1L

                ResultPage(
                    score = score,
                    totalQuestions = totalQuestions,
                    durationSeconds = durationSeconds,
                    categoryName = categoryName,
                    isMarathon = isMarathon,
                    isNewPersonalBest = isNewPersonalBest,
                    quizId = quizId,
                    onViewDetails = { id ->
                        navController.navigate(Screen.Questions.createRoute(id))
                    },
                    onBackToLobby = {
                        navController.navigate(Screen.Lobby.route) {
                            popUpTo(Screen.Lobby.route) { inclusive = true }
                        }
                    }
                )
            }

            // 7. Questions Detail Page
            composable(
                route = Screen.Questions.route,
                arguments = Screen.Questions.arguments
            ) {
                QuestionsPage(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    // Spoločný profilový dialóg pre celú aplikáciu
    if (showProfileDialog) {
        ProfileDialog(
            currentName = userProfile.name,
            currentAvatarUri = userProfile.avatarUri,
            currentNotificationsEnabled = userProfile.notificationsEnabled,
            currentLanguageCode = userProfile.languageCode,
            onDismiss = { showProfileDialog = false },
            onSaveProfile = { newName, newAvatarUri, notificationsEnabled, newLanguageCode ->
                onUpdateProfile(newName, newAvatarUri, notificationsEnabled, newLanguageCode)
                showProfileDialog = false
            }
        )
    }
}