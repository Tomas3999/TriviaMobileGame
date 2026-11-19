package com.fei.semestralnezadanie.frontend.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.frontend.components.AppLoader

@Composable
fun ChallengesPage(
    viewModel: ChallengesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        AppLoader()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Config.UI.SPACING_LARGE)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_LARGE)
    ) {
        // --- 1. KARTA: 7-DŇOVÝ STREAK ---
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
                verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_MEDIUM)
            ) {
                // Hlavička karty s ikonou ohňa
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFFF6D00), // Ohnivá oranžová farba
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = stringResource(R.string.challenges_streak_title),
                        fontSize = Config.UI.FONT_SIZE_TITLE,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Popis pravidiel
                Text(
                    text = stringResource(R.string.challenges_streak_desc),
                    fontSize = Config.UI.FONT_SIZE_BODY_SMALL,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Stav na dnešný deň (Splnené vs. Čaká ťa)
                Text(
                    text = if (state.isTodayCompleted) {
                        stringResource(R.string.challenges_streak_today_done)
                    } else {
                        stringResource(R.string.challenges_streak_today_pending)
                    },
                    fontSize = Config.UI.FONT_SIZE_BODY_MEDIUM,
                    fontWeight = FontWeight.SemiBold,
                    color = if (state.isTodayCompleted) Color(0xFF2E7D32) else Color(0xFFFF6D00)
                )

                Spacer(modifier = Modifier.height(Config.UI.SPACING_SMALL))

                // Vizuálny ukazovateľ 7 dní (Kruhy pre dni 1 až 7)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (day in 1..state.streakTargetDays) {
                        val isDayAchieved = day <= state.currentStreak

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_EXTRA_SMALL)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isDayAchieved) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isDayAchieved) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outlineVariant,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDayAchieved) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text(
                                        text = "$day",
                                        fontSize = Config.UI.FONT_SIZE_CAPTION,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = stringResource(R.string.challenges_day_format, day),
                                fontSize = Config.UI.FONT_SIZE_CAPTION,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Celkový text postupu
                Text(
                    text = stringResource(
                        R.string.challenges_streak_progress,
                        state.currentStreak
                    ),
                    fontSize = Config.UI.FONT_SIZE_BODY_LARGE,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // --- 2. KARTA: OSOBNÝ REKORD V MARATÓNE ---
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
                verticalArrangement = Arrangement.spacedBy(Config.UI.SPACING_MEDIUM)
            ) {
                // Hlavička s pohárom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = stringResource(R.string.challenges_marathon_pb_title),
                        fontSize = Config.UI.FONT_SIZE_TITLE,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = stringResource(R.string.challenges_marathon_pb_desc),
                    fontSize = Config.UI.FONT_SIZE_BODY_SMALL,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Skóre rekordu
                Text(
                    text = stringResource(
                        R.string.challenges_marathon_pb_score,
                        state.marathonPersonalBest
                    ),
                    fontSize = Config.UI.FONT_SIZE_QUESTION,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}