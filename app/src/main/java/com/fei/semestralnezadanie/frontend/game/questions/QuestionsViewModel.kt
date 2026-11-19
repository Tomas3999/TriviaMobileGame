package com.fei.semestralnezadanie.frontend.game.questions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fei.semestralnezadanie.backend.database.entities.TriviaResultWithQuestions
import com.fei.semestralnezadanie.backend.repository.TriviaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class QuestionsUiState(
    val isLoading: Boolean = true,
    val quizDetail: TriviaResultWithQuestions? = null
)

@HiltViewModel
class QuestionsViewModel @Inject constructor(
    private val triviaRepository: TriviaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val quizId: Long = savedStateHandle.get<Long>("quizId") ?: -1L

    val uiState: StateFlow<QuestionsUiState> = triviaRepository.getQuizDetail(quizId)
        .map { detail ->
            QuestionsUiState(
                isLoading = false,
                quizDetail = detail
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = QuestionsUiState(isLoading = true)
        )
}
