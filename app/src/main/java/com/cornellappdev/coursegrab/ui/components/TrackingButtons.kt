package com.cornellappdev.coursegrab.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import com.cornellappdev.coursegrab.ui.theme.RemovalRed
import java.util.Locale

@Composable
fun TrackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedActionButton(
        label = R.string.track_button,
        accent = Color.Black,
        cornerRadius = 1.dp,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun RemoveButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedActionButton(
        label = R.string.remove_button,
        accent = RemovalRed,
        cornerRadius = 3.dp,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun OutlinedActionButton(
    @StringRes label: Int,
    accent: Color,
    cornerRadius: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(1.dp, accent),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = accent
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
    ) {
        Text(
            text = stringResource(label).uppercase(Locale.ROOT),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TrackButtonPreview() {
    CourseGrabTheme {
        TrackButton(onClick = {}, modifier = Modifier.height(26.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun RemoveButtonPreview() {
    CourseGrabTheme {
        RemoveButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        )
    }
}
