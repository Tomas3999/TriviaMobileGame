package com.fei.semestralnezadanie.frontend.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.fei.semestralnezadanie.Config

/**
 * Hlavné plné tlačidlo aplikácie (napr. Štart kvízu, Pokračovať, Zdieľať).
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(Config.UI.BUTTON_HEIGHT),
        shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_MEDIUM)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null)
            }
            Text(
                text = text,
                fontSize = Config.UI.FONT_SIZE_TITLE
            )
        }
    }
}

/**
 * Sekundárne obrysové tlačidlo (napr. Výber fotky z galérie, Späť do menu).
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(Config.UI.BUTTON_HEIGHT),
        shape = RoundedCornerShape(Config.UI.CORNER_RADIUS_MEDIUM)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Config.UI.SPACING_SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null)
            }
            Text(
                text = text,
                fontSize = Config.UI.FONT_SIZE_BODY_LARGE
            )
        }
    }
}