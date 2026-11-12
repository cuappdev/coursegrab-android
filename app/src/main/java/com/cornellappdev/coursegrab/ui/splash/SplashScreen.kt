package com.cornellappdev.coursegrab.ui.splash

import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.cornellappdev.coursegrab.R

@Composable
fun SplashScreen(onFinished: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        SplashVideo(
            onFinished = onFinished,
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center)
        )
        Image(
            painter = painterResource(R.drawable.ic_powered_by),
            contentDescription = stringResource(R.string.powered_by_appdev),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 64.dp)
                .width(180.dp)
        )
    }
}

@Composable
private fun SplashVideo(onFinished: () -> Unit, modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) {
        Image(
            painter = painterResource(R.drawable.ic_coursegrab_icon),
            contentDescription = null,
            modifier = modifier.padding(64.dp)
        )
        return
    }

    val currentOnFinished by rememberUpdatedState(onFinished)
    AndroidView(
        modifier = modifier,
        factory = { context ->
            VideoView(context).apply {
                keepScreenOn = true
                setOnCompletionListener { currentOnFinished() }
                setOnErrorListener { _, _, _ ->
                    currentOnFinished()
                    true
                }
                setVideoURI("android.resource://${context.packageName}/${R.raw.splash}".toUri())
                start()
            }
        },
        onRelease = { it.stopPlayback() }
    )
}

@Preview(widthDp = 360, heightDp = 720)
@Composable
private fun SplashScreenPreview() {
    SplashScreen(onFinished = {})
}
