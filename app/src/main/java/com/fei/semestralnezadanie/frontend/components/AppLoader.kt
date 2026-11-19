package com.fei.semestralnezadanie.frontend.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fei.semestralnezadanie.Config
import kotlinx.coroutines.delay

@Composable
fun AppLoader(
    modifier: Modifier = Modifier,
    delayMillis: Long = Config.App.LOADER_DELAY_MILLIS, // Ak zadáš napr. 400L, zobrazí sa až po 400 ms
    @StringRes textRes: Int? = null // Voliteľný text pod kolieskom
) {
    var isVisible by remember { mutableStateOf(delayMillis == 0L) }

    if (delayMillis > 0L) {
        LaunchedEffect(Unit) {
            delay(delayMillis)
            isVisible = true
        }
    }

    if (isVisible) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )

                if (textRes != null) {
                    Spacer(modifier = Modifier.height(Config.UI.SPACING_MEDIUM))
                    Text(
                        text = stringResource(textRes),
                        fontSize = Config.UI.FONT_SIZE_BODY_MEDIUM,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}