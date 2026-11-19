package com.fei.semestralnezadanie.frontend.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R

// Definícia jednej záložky spodného panela
private data class BottomNavItem(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
)

// Zoznam našich 3 záložiek
private val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Lobby.route,
        labelRes = R.string.nav_play,
        icon = Icons.Default.PlayArrow
    ),
    BottomNavItem(
        route = Screen.History.route,
        labelRes = R.string.nav_history,
        icon = Icons.Default.History
    ),
    BottomNavItem(
        route = Screen.Challenges.route,
        labelRes = R.string.nav_challenges,
        icon = Icons.Default.EmojiEvents
    )
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            val label = stringResource(item.labelRes)
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = Config.UI.FONT_SIZE_CAPTION
                    )
                }
            )
        }
    }
}