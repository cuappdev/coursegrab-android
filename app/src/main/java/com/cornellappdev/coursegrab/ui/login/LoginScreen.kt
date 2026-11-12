package com.cornellappdev.coursegrab.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.components.EffectHandler
import com.cornellappdev.coursegrab.ui.components.StatusBarIcons
import com.cornellappdev.coursegrab.ui.components.courseGrabShadow
import com.cornellappdev.coursegrab.ui.components.meshGradientBackground
import kotlinx.coroutines.launch

private val WelcomeStyle = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 27.sp,
    letterSpacing = (-1).sp
)
private val TitleStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 40.sp)
private val GoogleButtonStyle = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    letterSpacing = 0.16.sp,
    color = Color(0xFF767676)
)
private val GoogleButtonShape = RoundedCornerShape(8.dp)

@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit,
    onReady: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isCheckingSession by viewModel.isCheckingSession.collectAsStateWithLifecycle()

    StatusBarIcons(onDarkBackground = true)

    LaunchedEffect(isCheckingSession) {
        if (!isCheckingSession) onReady()
    }

    EffectHandler(viewModel.effects) { effect ->
        when (effect) {
            is LoginEffect.Error -> scope.launch { snackbarHostState.showSnackbar(effect.message) }
            LoginEffect.NavigateToMain -> onNavigateToMain()
        }
    }

    LoginContent(
        snackbarHostState = snackbarHostState,
        ringBell = !isCheckingSession,
        onSignIn = { viewModel.signIn(context) },
        modifier = modifier
    )
}

@Composable
private fun LoginContent(
    snackbarHostState: SnackbarHostState,
    ringBell: Boolean,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .meshGradientBackground()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(56.dp, Alignment.CenterVertically)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(R.string.welcome), style = WelcomeStyle, color = Color.White)
                    Text(stringResource(R.string.app_name), style = TitleStyle, color = Color.White)
                }
                HeroBell(ring = ringBell)
            }
            GoogleSignInButton(onClick = onSignIn)
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .safeDrawingPadding()
        )
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .courseGrabShadow(GoogleButtonShape, blur = 2.dp, offsetY = 1.dp, alpha = 0.25f)
            .clip(GoogleButtonShape)
            .background(Color.White)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_cg_google_g),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(stringResource(R.string.sign_in_with_google), style = GoogleButtonStyle)
    }
}

@Preview(widthDp = 402, heightDp = 874)
@Composable
private fun LoginScreenPreview() {
    LoginContent(
        snackbarHostState = remember { SnackbarHostState() },
        ringBell = false,
        onSignIn = {}
    )
}
