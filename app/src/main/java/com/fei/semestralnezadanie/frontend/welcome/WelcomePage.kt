package com.fei.semestralnezadanie.frontend.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.frontend.components.PrimaryButton
import com.fei.semestralnezadanie.frontend.components.ProfileForm
import com.fei.semestralnezadanie.ui.theme.SemestralneZadanieTheme

@Composable
fun WelcomePage(
    viewModel: WelcomeViewModel = hiltViewModel(),
    onContinueToLobby: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Presmerovanie do Lobby po dokončení profilu
    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            onContinueToLobby()
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Config.UI.SPACING_LARGE)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Nadpisy
            Text(
                text = stringResource(R.string.welcome_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.welcome_subtitle),
                fontSize = Config.UI.FONT_SIZE_BODY_MEDIUM,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Config.UI.SPACING_EXTRA_LARGE))

            // Zdieľaný formulár profilu
            ProfileForm(
                name = state.name,
                avatarUri = state.avatarUri,
                onNameChange = { viewModel.onNameChange(it) },
                onAvatarChange = { viewModel.onAvatarSelected(it) },
                avatarBorderColorKey = state.avatarBorderColorKey,
                onAvatarBorderColorChange = { viewModel.onAvatarBorderColorSelected(it) },
                showSettingsSection = false
            )

            Spacer(modifier = Modifier.height(Config.UI.SPACING_LARGE))

            // Tlačidlo Pokračovať
            PrimaryButton(
                text = stringResource(R.string.welcome_btn_continue),
                enabled = state.isValid,
                onClick = { viewModel.saveProfileAndContinue() }
            )
        }
    }
}