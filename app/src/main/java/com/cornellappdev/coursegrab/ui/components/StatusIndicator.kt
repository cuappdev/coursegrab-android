package com.cornellappdev.coursegrab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme

@Composable
fun StatusIndicator(isOpen: Boolean, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(
            if (isOpen) R.drawable.ic_status_open else R.drawable.ic_status_closed
        ),
        contentDescription = stringResource(
            if (isOpen) R.string.status_open else R.string.status_closed
        ),
        modifier = modifier
    )
}

@Preview
@Composable
private fun StatusIndicatorPreview() {
    CourseGrabTheme { StatusIndicator(isOpen = true) }
}

@Preview
@Composable
private fun StatusIndicatorClosedPreview() {
    CourseGrabTheme { StatusIndicator(isOpen = false) }
}
