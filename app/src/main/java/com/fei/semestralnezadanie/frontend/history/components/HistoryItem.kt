package com.fei.semestralnezadanie.frontend.history.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.backend.database.entities.TriviaResult
import com.fei.semestralnezadanie.enums.Difficulty
import com.fei.semestralnezadanie.enums.GameMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryItem(
    result: TriviaResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDate = remember(result.timestamp) {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(result.timestamp))
    }

    // Bezpečný prevod cez fromId namiesto hľadania podľa indexu
    val gameMode = GameMode.fromId(result.gameMode)
    val difficulty = Difficulty.fromId(result.difficulty)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_MEDIUM),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Config.UI.SPACING_MEDIUM),
            verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = result.category,
                    fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = formattedDate,
                    fontSize = Config.UI.FONT_SIZE_CAPTION,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "${stringResource(gameMode.labelRes)} • ${stringResource(difficulty.labelRes)}",
                fontSize = Config.UI.FONT_SIZE_BODY_SMALL,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.history_item_score,
                        result.correctAnswersCount,
                        result.totalQuestionsCount
                    ),
                    fontSize = Config.UI.FONT_SIZE_BODY_MEDIUM,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.history_item_time, result.durationSeconds),
                    fontSize = Config.UI.FONT_SIZE_BODY_MEDIUM,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}