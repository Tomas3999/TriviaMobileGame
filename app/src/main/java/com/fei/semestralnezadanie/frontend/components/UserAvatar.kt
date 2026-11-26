package com.fei.semestralnezadanie.frontend.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.frontend.enums.AvatarColor
import com.fei.semestralnezadanie.frontend.enums.AvatarPreset

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    avatarUri: String?,
    size: Dp = Config.UI.AVATAR_SIZE_LARGE,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    onClick: (() -> Unit)? = null
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val gradientStart = lerp(surfaceColor, borderColor, 0.30f)
    val gradientEnd = lerp(surfaceColor, borderColor, 0.10f)

    val iconTint = if (borderColor.luminance() > 0.55f) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        lerp(borderColor, MaterialTheme.colorScheme.onSurface, 0.20f)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(gradientStart, gradientEnd)
                )
            )
            .border(
                width = Config.UI.AVATAR_BORDER_WIDTH,
                color = borderColor,
                shape = CircleShape
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        val isCustomImage = avatarUri?.startsWith("content://") == true ||
                avatarUri?.startsWith("file://") == true ||
                avatarUri?.startsWith("/") == true

        if (isCustomImage) {
            AsyncImage(
                model = avatarUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = AvatarPreset.getIconByKey(avatarUri),
                contentDescription = null,
                modifier = Modifier.size(size * 0.56f),
                tint = iconTint
            )
        }
    }
}