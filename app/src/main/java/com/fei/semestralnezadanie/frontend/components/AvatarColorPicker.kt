package com.fei.semestralnezadanie.frontend.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fei.semestralnezadanie.frontend.enums.AvatarColor


@Composable
fun AvatarColorPicker(
    selectedColorKey: String?,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 3
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarColor.entries.chunked(columns).forEach { rowColors ->
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowColors.forEach { avatarColor ->
                    val isSelected = selectedColorKey == avatarColor.key

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                        .padding(3.dp)
                                } else {
                                    Modifier.padding(3.dp)
                                }
                            )
                            .clip(CircleShape)
                            .background(avatarColor.color)
                            .clickable { onColorSelected(avatarColor.key) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
