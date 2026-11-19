package com.fei.semestralnezadanie.frontend.lobby

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.backend.api.models.CategoryDto
import com.fei.semestralnezadanie.backend.datastore.UserManager
import com.fei.semestralnezadanie.backend.repository.TriviaRepository
import com.fei.semestralnezadanie.enums.Difficulty
import com.fei.semestralnezadanie.enums.GameMode
import com.fei.semestralnezadanie.frontend.enums.QuestionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LobbyUiState(
    val userName: String = "",
    val userAvatarUri: String? = null,
    val categories: List<CategoryDto> = emptyList(),
    val isLoadingCategories: Boolean = false,
    @StringRes
    val categoryErrorRes: Int? = null,
    val selectedCategory: CategoryDto? = null,
    val selectedDifficulty: Difficulty = Difficulty.ANY,
    val selectedType: QuestionType = QuestionType.ANY,
    val selectedGameMode: GameMode = GameMode.CLASSIC
)

@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val triviaRepository: TriviaRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LobbyUiState())
    val uiState: StateFlow<LobbyUiState> = _uiState.asStateFlow()

    init {
        observeUserProfile()
        loadCategories()
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userManager.userProfileFlow.collect { profile ->
                _uiState.update {
                    it.copy(
                        userName = profile.name,
                        userAvatarUri = profile.avatarUri
                    )
                }
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCategories = true, categoryErrorRes = null) }

            triviaRepository.getCategories()
                .onSuccess { categoriesList ->
                    _uiState.update {
                        it.copy(
                            categories = categoriesList,
                            isLoadingCategories = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            categoryErrorRes = R.string.error_failed_to_load_categories,
                            isLoadingCategories = false
                        )
                    }
                }
        }
    }

    fun selectCategory(category: CategoryDto?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectDifficulty(difficulty: Difficulty) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
    }

    fun selectType(type: QuestionType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun selectGameMode(gameMode: GameMode) {
        _uiState.update { it.copy(selectedGameMode = gameMode) }
    }
}