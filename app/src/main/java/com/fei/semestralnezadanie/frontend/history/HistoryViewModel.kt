package com.fei.semestralnezadanie.frontend.history

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.backend.database.entities.TriviaResult
import com.fei.semestralnezadanie.backend.repository.TriviaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// Možnosti radenia podľa zadania
enum class HistorySortOption(@StringRes val labelRes: Int) {
    NEWEST(R.string.history_sort_newest),
    BEST_SCORE(R.string.history_sort_score),
    FASTEST_TIME(R.string.history_sort_time)
}

data class HistoryUiState(
    val isLoading: Boolean = true,
    val results: List<TriviaResult> = emptyList(),
    val availableCategories: List<String> = emptyList(),
    val selectedCategory: String? = null, // null = všetky kategórie
    val selectedSort: HistorySortOption = HistorySortOption.NEWEST
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    triviaRepository: TriviaRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _selectedSort = MutableStateFlow(HistorySortOption.NEWEST)

    // Spojíme tok z Room databázy s našimi filtrami do jedného StateFlow
    val uiState: StateFlow<HistoryUiState> = combine(
        triviaRepository.getAllResults(),
        _selectedCategory,
        _selectedSort
    ) { allResults, category, sortOption ->

        // 1. Zoznam unikátnych kategórií z odohraných hier pre filter
        val categories = allResults.map { it.category }.distinct().sorted()

        // 2. Filtrovanie podľa kategórie
        val filtered = if (category != null) {
            allResults.filter { it.category == category }
        } else {
            allResults
        }

        // 3. Radenie podľa zvoleného kritéria
        val sorted = when (sortOption) {
            HistorySortOption.NEWEST -> filtered.sortedByDescending { it.timestamp }
            HistorySortOption.BEST_SCORE -> filtered.sortedWith(
                compareByDescending<TriviaResult> { it.correctAnswersCount }
                    .thenBy { it.durationSeconds }
            )
            HistorySortOption.FASTEST_TIME -> filtered.sortedBy { it.durationSeconds }
        }

        HistoryUiState(
            isLoading = false,
            results = sorted,
            availableCategories = categories,
            selectedCategory = category,
            selectedSort = sortOption
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HistoryUiState(isLoading = true)
    )

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun selectSortOption(sortOption: HistorySortOption) {
        _selectedSort.value = sortOption
    }
}