package com.fei.semestralnezadanie.frontend.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.frontend.components.AppLoader
import com.fei.semestralnezadanie.frontend.game.components.Question

@Composable
fun GamePage(
    viewModel: GameViewModel = hiltViewModel(),
    onGameOver: (score: Int, totalQuestions: Int, durationSeconds: Long, isMarathon: Boolean, isNewPersonalBest: Boolean, quizId: Long) -> Unit,
    onAbortClassicGame: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Sledovanie minimalizovania okna (pauza a obnovenie časovača v oboch módoch)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.pauseTimer()
                Lifecycle.Event.ON_RESUME -> viewModel.resumeTimer()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state.isGameOver) {
        if (state.isGameOver) {
            onGameOver(
                state.correctAnswersCount,
                state.correctAnswersCount + state.incorrectAnswersCount,
                state.totalGameDurationSeconds,
                viewModel.isMarathon,
                state.isNewPersonalBestInMarathon,
                state.savedQuizId
            )
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    AppLoader(textRes = R.string.game_loading_questions)
                }

                state.errorResId != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(state.errorResId!!),
                            fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                state.currentQuestion != null -> {
                    Question(
                        question = state.currentQuestion!!,
                        questionIndex = state.currentQuestionIndex,
                        totalQuestionsCount = if (viewModel.isMarathon) null else state.totalQuestionsCount,
                        remainingSeconds = state.remainingSeconds,
                        onAnswerSelected = { index -> viewModel.submitAnswer(index) },
                        onCloseClick = {
                            if (viewModel.isMarathon) {
                                viewModel.endMarathonEarly()
                            } else {
                                viewModel.pauseTimer()
                                onAbortClassicGame()
                            }
                        }
                    )
                }
            }
        }
    }
}