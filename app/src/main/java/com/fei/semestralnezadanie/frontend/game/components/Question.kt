package com.fei.semestralnezadanie.frontend.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.backend.repository.models.TriviaQuestion

/**
 * Čistý prezentačný komponent pre práve JEDNU otázku.
 * Nemá žiadny ViewModel, nerieši sieť ani Room.
 */
@Composable
fun Question(
    question: TriviaQuestion,
    questionIndex: Int,
    totalQuestionsCount: Int?, // null, ak je to Maratón
    remainingSeconds: Long,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Config.UI.SPACING_LARGE),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Hlavička: Poradové číslo otázky a 15-sekundový časovač
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Číslo otázky
                Text(
                    text = if (totalQuestionsCount != null) {
                        stringResource(R.string.game_question_counter, questionIndex, totalQuestionsCount)
                    } else {
                        stringResource(R.string.game_question_counter_marathon, questionIndex)
                    },
                    fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Odpočet sekúnd
                Text(
                    text = stringResource(R.string.game_timer_seconds, remainingSeconds),
                    fontSize = Config.UI.FONT_SIZE_TITLE,
                    fontWeight = FontWeight.Bold,
                    color = if (remainingSeconds <= Config.Trivia.QUESTION_TIME_LIMIT_WARNING) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(Config.UI.SPACING_SMALL))

            // Časová lišta
            LinearProgressIndicator(
                progress = { remainingSeconds / Config.Trivia.QUESTION_TIME_LIMIT_SECONDS.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Config.UI.SPACING_SMALL),
                color = if (remainingSeconds <= 5) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // 2. Samotný text tejto jednej otázky
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = Config.UI.SPACING_MEDIUM),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = question.question,
                fontSize = Config.UI.FONT_SIZE_QUESTION,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // 3. Možnosti odpovedí
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_MEDIUM)
        ) {
            question.options.forEachIndexed { index, optionText ->
                OutlinedButton(
                    onClick = { onAnswerSelected(index) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Config.UI.BUTTON_HEIGHT),
                    shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_MEDIUM),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = optionText,
                        fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}