package com.cornellappdev.coursegrab.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme

@Composable
fun CourseGrabTopBar(
    @StringRes title: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CourseGrabTopBar(title = stringResource(title), onBack = onBack, modifier = modifier)
}

@Composable
fun CourseGrabTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CourseGrabTopBar(onBack = onBack, modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = BACK_BUTTON_WIDTH)
        )
    }
}

@Composable
fun CourseGrabTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Black,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HEIGHT)
        ) {
            content()
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(BACK_BUTTON_WIDTH)
                    .fillMaxHeight()
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White
                )
            }
        }
    }
}

private val HEIGHT = 60.dp
val BACK_BUTTON_WIDTH = 50.dp

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CourseGrabTopBarPreview() {
    CourseGrabTheme {
        CourseGrabTopBar(title = R.string.settings, onBack = {})
    }
}
