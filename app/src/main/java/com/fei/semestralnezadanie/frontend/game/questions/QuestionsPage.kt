package com.fei.semestralnezadanie.frontend.game.questions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.backend.database.entities.QuestionAnswer
import com.fei.semestralnezadanie.frontend.components.AppLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsPage(
    viewModel: QuestionsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.detail_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.detail_btn_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    AppLoader()
                }

                state.quizDetail == null || state.quizDetail?.questions.isNullOrEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Config.UI.SPACING_LARGE),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.api_error_unknown),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(Config.UI.SPACING_MEDIUM))
                        Button(onClick = onBack) {
                            Text(text = stringResource(R.string.detail_btn_back))
                        }
                    }
                }

                else -> {
                    val questions = state.quizDetail!!.questions

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Config.UI.SPACING_LARGE),
                        verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_LARGE)
                    ) {
                        itemsIndexed(questions) { index, question ->
                            QuestionReviewCard(
                                questionIndex = index + 1,
                                question = question
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionReviewCard(
    questionIndex: Int,
    question: QuestionAnswer
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Config.UI.SPACING_LARGE),
            verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_MEDIUM)
        ) {
            // 1. Hlavička položky: Otázka #X + Čas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.detail_question_number, questionIndex),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = stringResource(R.string.detail_time_spent, question.timeSpentSeconds),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 2. Znenie otázky
            Text(
                text = question.questionText,
                fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // 3. Upozornenie, ak hráč nestihol odpovedať (15s vypršalo)
            if (question.selectedAnswer == null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_SMALL),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Config.UI.SPACING_SMALL),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TimerOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.detail_unanswered),
                            fontSize = Config.UI.FONT_SIZE_BODY_SMALL,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // 4. Zoznam ponúkaných možností s farebným zvýraznením
            Column(
                verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)
            ) {
                question.options.forEachIndexed { optIndex, optionText ->
                    val isCorrect = optIndex == question.correctAnswer
                    val isSelectedWrong = question.selectedAnswer != null &&
                            optIndex == question.selectedAnswer &&
                            !isCorrect

                    val containerColor = when {
                        isCorrect -> Color(0xFFE8F5E9) // Jemná zelená
                        isSelectedWrong -> Color(0xFFFFEBEE) // Jemná červená
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }

                    val borderColor = when {
                        isCorrect -> Color(0xFF2E7D32) // Tmavozelený okraj
                        isSelectedWrong -> MaterialTheme.colorScheme.error // Červený okraj
                        else -> Color.Transparent
                    }

                    val textColor = when {
                        isCorrect -> Color(0xFF1B5E20)
                        isSelectedWrong -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isCorrect || isSelectedWrong) 1.5.dp else 0.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_MEDIUM)
                            ),
                        shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_MEDIUM),
                        color = containerColor
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Config.UI.SPACING_MEDIUM),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = optionText,
                                fontSize = Config.UI.FONT_SIZE_BODY_MEDIUM,
                                fontWeight = if (isCorrect || isSelectedWrong) FontWeight.Bold else FontWeight.Normal,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )

                            if (isCorrect) {
                                Spacer(modifier = Modifier.width(Config.UI.SPACING_SMALL))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(20.dp)
                                )
                            } else if (isSelectedWrong) {
                                Spacer(modifier = Modifier.width(Config.UI.SPACING_SMALL))
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
