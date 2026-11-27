package com.fei.semestralnezadanie.frontend.lobby

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FactCheck
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.enums.Difficulty
import com.fei.semestralnezadanie.enums.GameMode
import com.fei.semestralnezadanie.frontend.components.AppLoader
import com.fei.semestralnezadanie.frontend.components.PrimaryButton
import com.fei.semestralnezadanie.frontend.enums.QuestionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyPage(
    viewModel: LobbyViewModel = hiltViewModel(),
    onStartQuiz: (categoryId: Int?, categoryName: String, difficulty: String?, type: String?, gameModeId: Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            // Štartovacie tlačidlo vždy ukotvené dole na dosah palca
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Config.UI.SPACING_LARGE)
                ) {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Config.UI.SPACING_LARGE, vertical = Config.UI.SPACING_MEDIUM),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
           // 2. Herný Mód (Veľké akčné karty)
            SectionHeader(icon = Icons.Rounded.SportsEsports, title = stringResource(R.string.lobby_mode_label))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GameMode.entries.forEach { mode ->
                    val isSelected = state.selectedGameMode == mode
                    val icon = if (mode == GameMode.MARATHON) Icons.Rounded.AllInclusive else Icons.Rounded.Timer

                    GameModeCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(mode.labelRes),
                        icon = icon,
                        isSelected = isSelected,
                        onClick = { viewModel.selectGameMode(mode) }
                    )
                }
            }

            // 3. Náročnosť (Farebné interaktívne karty namiesto slidera)
            SectionHeader(icon = Icons.Rounded.Speed, title = stringResource(R.string.lobby_difficulty_label))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Difficulty.entries.forEach { diff ->
                    val isSelected = state.selectedDifficulty == diff
                    val targetColor = when (diff) {
                        Difficulty.EASY -> Config.UI.DIFFICULTY_EASY
                        Difficulty.MEDIUM -> Config.UI.DIFFICULTY_MEDIUM
                        Difficulty.HARD -> Config.UI.DIFFICULTY_HARD
                        else -> Config.UI.DIFFICULTY_ANY
                    }

                    DifficultyCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(diff.labelRes),
                        isSelected = isSelected,
                        accentColor = targetColor,
                        onClick = { viewModel.selectDifficulty(diff) }
                    )
                }
            }

            // 4. Kategória
            SectionHeader(icon = Icons.Rounded.Category, title = stringResource(R.string.lobby_category_label))
            if (state.isLoadingCategories) {
                AppLoader(modifier = Modifier.fillMaxWidth())
            } else {
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = state.selectedCategory?.name ?: stringResource(R.string.lobby_category_any),
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(Icons.Rounded.Category, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_MEDIUM),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.lobby_category_any), fontWeight = FontWeight.Bold) },
                            onClick = {
                                viewModel.selectCategory(null)
                                categoryDropdownExpanded = false
                            }
                        )
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

            // 5. Typ otázok
            SectionHeader(
                icon = Icons.Rounded.Quiz,
                title = stringResource(R.string.lobby_type_label)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuestionType.entries.forEach { type ->

                    val isSelected = state.selectedType == type

                    val targetColor = when (type) {
                        QuestionType.ANY -> Config.UI.TYPE_ANY
                        QuestionType.MULTIPLE -> Config.UI.TYPE_MULTIPLE
                        QuestionType.BOOLEAN -> Config.UI.TYPE_TRUE_FALSE
                    }

                    QuestionTypeCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(type.labelRes),
                        isSelected = isSelected,
                        accentColor = targetColor,
                        onClick = {
                            viewModel.selectType(type)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Pomocný header pre každú sekciu s ikonkou
@Composable
private fun SectionHeader(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            fontSize = 18.sp,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// Herný mód: vizuálna karta
@Composable
private fun GameModeCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(targetValue = if (isSelected) 1.02f else 1.0f, label = "scale")
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = modifier
            .scale(scale)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_MEDIUM),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Náročnosť: farebný badge/karta
@Composable
private fun DifficultyCard(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) accentColor else MaterialTheme.colorScheme.surface,
        label = "bgColor"
    )

    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .height(50.dp),
        shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_SMALL),
        border = BorderStroke(1.dp, if (isSelected) accentColor else MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = animatedBg)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
            )
        }
    }
}


@Composable
private fun QuestionTypeCard(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) accentColor else MaterialTheme.colorScheme.surface,
        label = "bgColor"
    )

    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .height(50.dp),
        shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_SMALL),
        border = BorderStroke(1.dp, if (isSelected) accentColor else MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = animatedBg)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                lineHeight = 15.sp,
                softWrap = true,
                textAlign = TextAlign.Center
            )
        }
    }
}