package com.fei.semestralnezadanie.frontend.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.frontend.components.AppLoader
import com.fei.semestralnezadanie.frontend.history.components.HistoryItem

@Composable
fun HistoryPage(
    viewModel: HistoryViewModel = hiltViewModel(),
    onQuizClick: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Ovládanie radenia (Najnovšie, Najlepšie skóre, Najrýchlejšie)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Config.UI.SPACING_MEDIUM, vertical = Config.UI.SPACING_SMALL),
            horizontalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)
        ) {
            HistorySortOption.entries.forEach { sortOption ->
                FilterChip(
                    selected = state.selectedSort == sortOption,
                    onClick = { viewModel.selectSortOption(sortOption) },
                    label = {
                        Text(
                            text = stringResource(sortOption.labelRes),
                            fontSize = Config.UI.FONT_SIZE_CAPTION
                        )
                    },
                    shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_SMALL)
                )
            }
        }

        // 2. Filtrovanie podľa kategórie
        if (state.availableCategories.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = Config.UI.SPACING_MEDIUM),
                horizontalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)
            ) {
                // Možnosť "Všetky kategórie"
                item {
                    FilterChip(
                        selected = state.selectedCategory == null,
                        onClick = { viewModel.selectCategory(null) },
                        label = {
                            Text(
                                text = stringResource(R.string.history_filter_all),
                                fontSize = Config.UI.FONT_SIZE_CAPTION
                            )
                        },
                        shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_SMALL)
                    )
                }

                // Konkrétne kategórie
                items(state.availableCategories) { category ->
                    FilterChip(
                        selected = state.selectedCategory == category,
                        onClick = { viewModel.selectCategory(category) },
                        label = {
                            Text(
                                text = category,
                                fontSize = Config.UI.FONT_SIZE_CAPTION
                            )
                        },
                        shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_SMALL)
                    )
                }
            }
        }

        // 3. Zoznam výsledkov
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    AppLoader()
                }

                state.results.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Config.UI.SPACING_LARGE),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.history_empty),
                            fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Config.UI.SPACING_MEDIUM),
                        verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_MEDIUM)
                    ) {
                        items(
                            items = state.results,
                            key = { it.id }
                        ) { result ->
                            HistoryItem(
                                result = result,
                                onClick = { onQuizClick(result.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}