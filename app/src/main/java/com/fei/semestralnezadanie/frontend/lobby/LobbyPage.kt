package com.fei.semestralnezadanie.frontend.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.frontend.components.PrimaryButton
import com.fei.semestralnezadanie.frontend.enums.QuestionType
import com.fei.semestralnezadanie.enums.Difficulty
import com.fei.semestralnezadanie.enums.GameMode

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LobbyPage(
    viewModel: LobbyViewModel = hiltViewModel(),
    onStartQuiz: (categoryId: Int?, categoryName: String, difficulty: String?, type: String?, gameModeId: Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Config.UI.SPACING_LARGE)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_LARGE)
        ) {

            // 2. Kategória (Dropdown menu)
            Column(verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)) {
                Text(
                    text = stringResource(R.string.lobby_category_label),
                    fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                    style = MaterialTheme.typography.titleMedium
                )

                if (state.isLoadingCategories) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(Config.UI.PROGRESS_INDICATOR_SIZE))
                        Text(
                            text = stringResource(R.string.lobby_category_loading),
                            fontSize = Config.UI.FONT_SIZE_BODY_MEDIUM
                        )
                    }
                } else {
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.selectedCategory?.name ?: stringResource(R.string.lobby_category_any),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_MEDIUM)
                        )

                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            // Položka pre "Všetky kategórie (Any)"
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.lobby_category_any)) },
                                onClick = {
                                    viewModel.selectCategory(null)
                                    categoryDropdownExpanded = false
                                }
                            )
                            // Dynamické položky z API
                            state.categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        viewModel.selectCategory(category)
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 3. Náročnosť (Difficulty Chips)
            Column(verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)) {
                Text(
                    text = stringResource(R.string.lobby_difficulty_label),
                    fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)
                ) {
                    Difficulty.entries.forEach { difficulty ->
                        FilterChip(
                            selected = state.selectedDifficulty == difficulty,
                            onClick = { viewModel.selectDifficulty(difficulty) },
                            label = { Text(stringResource(difficulty.labelRes), fontSize = Config.UI.FONT_SIZE_BODY_SMALL) },
                            shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_SMALL)
                        )
                    }
                }
            }

            // 4. Typ otázok (Multiple Choice vs True/False)
            Column(verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)) {
                Text(
                    text = stringResource(R.string.lobby_type_label),
                    fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                    style = MaterialTheme.typography.titleMedium
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)
                ) {
                    QuestionType.entries.forEach { type ->
                        FilterChip(
                            selected = state.selectedType == type,
                            onClick = { viewModel.selectType(type) },
                            label = { Text(stringResource(type.labelRes), fontSize = Config.UI.FONT_SIZE_BODY_SMALL) },
                            shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_SMALL)
                        )
                    }
                }
            }

            // 5. Herný mód (Klasický vs Maratón)
            Column(verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)) {
                Text(
                    text = stringResource(R.string.lobby_mode_label),
                    fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)
                ) {
                    GameMode.entries.forEach { mode ->
                        FilterChip(
                            selected = state.selectedGameMode == mode,
                            onClick = { viewModel.selectGameMode(mode) },
                            label = { Text(stringResource(mode.labelRes), fontSize = Config.UI.FONT_SIZE_BODY_SMALL) },
                            shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_SMALL)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 6. Tlačidlo Spustiť kvíz
            PrimaryButton(
                text = stringResource(R.string.lobby_btn_start),
                onClick = {
                    onStartQuiz(
                        state.selectedCategory?.id,
                        state.selectedCategory?.name ?: "Any",
                        state.selectedDifficulty.apiValue,
                        state.selectedType.apiValue,
                        state.selectedGameMode.id
                    )
                }
            )
        }
    }
}
