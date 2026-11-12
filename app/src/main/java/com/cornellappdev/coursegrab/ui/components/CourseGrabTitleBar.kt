package com.cornellappdev.coursegrab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTextStyles

@Composable
fun CourseGrabTitleBar(title: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(30.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_cg_back),
                contentDescription = stringResource(R.string.back)
            )
        }
        Text(title, style = CourseGrabTextStyles.H1, color = CourseGrabColors.Black)
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun CourseGrabTitleBarPreview() {
    CourseGrabTitleBar(title = "Settings", onBack = {})
}
