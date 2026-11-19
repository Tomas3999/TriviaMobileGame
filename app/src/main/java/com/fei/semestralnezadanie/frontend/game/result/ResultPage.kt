package com.fei.semestralnezadanie.frontend.game.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.frontend.components.PrimaryButton
import com.fei.semestralnezadanie.frontend.components.SecondaryButton
import com.fei.semestralnezadanie.frontend.components.ShareHelper

@Composable
fun ResultPage(
    score: Int,
    totalQuestions: Int,
    durationSeconds: Long,
    categoryName: String,
    isMarathon: Boolean,
    isNewPersonalBest: Boolean,
    quizId: Long,
    onViewDetails: (Long) -> Unit,
    onBackToLobby: () -> Unit
) {
    val context = LocalContext.current
    val accuracyPercent = if (totalQuestions > 0) (score * 100) / totalQuestions else 0

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Config.UI.SPACING_LARGE),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. HORNÁ ČASŤ: Ikona pohára a hlavný nadpis
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_MEDIUM)
            ) {
                Spacer(modifier = Modifier.height(Config.UI.SPACING_LARGE))

                Box(
                    modifier = Modifier
                        .size(Config.UI.AVATAR_SIZE_LARGE)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(Config.UI.AVATAR_INNER_ICON_SIZE),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = if (isMarathon) stringResource(R.string.result_title_marathon)
                    else stringResource(R.string.result_title_classic),
                    fontSize = Config.UI.FONT_SIZE_HEADER,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (isNewPersonalBest) {
                    Text(
                        text = stringResource(R.string.result_new_best),
                        fontSize = Config.UI.FONT_SIZE_TITLE,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 2. STREDNÁ ČASŤ: Karta so štatistikou
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_LARGE),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Config.UI.SPACING_LARGE),
                    verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_MEDIUM),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.result_score_label, score, totalQuestions),
                        fontSize = Config.UI.FONT_SIZE_QUESTION,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = stringResource(R.string.result_accuracy, accuracyPercent),
                        fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = stringResource(R.string.result_time, durationSeconds),
                        fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 3. SPODNÁ ČASŤ: Použitie našich zjednotených tlačidiel
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_MEDIUM)
            ) {
                // Hlavné tlačidlo na zdieľanie
                PrimaryButton(
                    text = stringResource(R.string.result_btn_share),
                    icon = Icons.Default.Share,
                    onClick = {
                        ShareHelper.shareResult(
                            context = context,
                            score = score,
                            totalQuestions = totalQuestions,
                            categoryName = categoryName,
                            durationSeconds = durationSeconds
                        )
                    }
                )

                // Sekundárne tlačidlo na detailný rozbor otázok
                if (quizId > 0) {
                    SecondaryButton(
                        text = stringResource(R.string.result_btn_details),
                        icon = Icons.AutoMirrored.Filled.List,
                        onClick = { onViewDetails(quizId) }
                    )
                }

                // Sekundárne tlačidlo Späť do menu
                SecondaryButton(
                    text = stringResource(R.string.result_btn_lobby),
                    onClick = onBackToLobby
                )
            }
        }
    }
}