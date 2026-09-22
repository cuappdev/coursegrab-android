package com.cornellappdev.coursegrab.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.components.EffectHandler
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import com.cornellappdev.coursegrab.ui.theme.DarkEnough
import com.google.android.gms.common.SignInButton

@Composable
fun LoginRoute(
    onNavigateToMain: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    EffectHandler(viewModel.effects) { effect ->
        when (effect) {
            is LoginEffect.Error -> snackbarHostState.showSnackbar(effect.message)
            LoginEffect.NavigateToMain -> onNavigateToMain()
        }
    }

    LoginScreen(
        snackbarHostState = snackbarHostState,
        onSignIn = viewModel::signIn,
        modifier = modifier
    )
}

@Composable
fun LoginScreen(
    snackbarHostState: SnackbarHostState,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = DarkEnough,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.welcome),
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 27.sp,
                color = Color.White
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 40.sp,
                color = Color.White
            )
            Image(
                painter = painterResource(R.drawable.ic_coursegrab_icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 30.dp)
                    .size(width = 128.dp, height = 150.dp)
            )
            Spacer(Modifier.height(40.dp))
            GoogleSignInButton(onClick = onSignIn, modifier = Modifier.width(185.dp))
        }
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            SignInButton(context).apply {
                setSize(SignInButton.SIZE_STANDARD)
            }
        },
        update = { button -> button.setOnClickListener { onClick() } }
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun LoginScreenPreview() {
    CourseGrabTheme {
        LoginScreen(
            snackbarHostState = remember { SnackbarHostState() },
            onSignIn = {}
        )
    }
}
