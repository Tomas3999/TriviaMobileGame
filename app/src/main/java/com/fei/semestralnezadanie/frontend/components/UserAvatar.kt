package com.fei.semestralnezadanie.frontend.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.frontend.enums.AvatarPreset

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    avatarUri: String?,
    size: Dp = Config.UI.AVATAR_SIZE_LARGE,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(
                width = Config.UI.AVATAR_BORDER_WIDTH,
                color = MaterialTheme.colorScheme.primary,
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
                modifier = Modifier.size(size * 0.65f),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}