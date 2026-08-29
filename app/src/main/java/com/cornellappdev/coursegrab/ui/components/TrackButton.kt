package com.cornellappdev.coursegrab.ui.components

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
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import com.cornellappdev.coursegrab.ui.theme.RemovalRed
import java.util.Locale

/**
 * Matches the existing XML buttons rather than Material3 defaults: white fill and a 1dp
 * outline in the accent color — see `track_button_background.xml` and
 * `remove_button_background.xml`. A filled M3 Button looks nothing like the shipped app.
 *
 * Size is the caller's job, because the two usages differ: the course rows give it the
 * full row width at 44dp, while the section rows use 26dp and wrap. Pass both through
 * [modifier].
 *
 * One control replaces the two mutually-exclusive Buttons the rows toggle between with
 * `View.VISIBLE` / `View.GONE`.
 */
@Composable
fun TrackButton(
    isTracking: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = if (isTracking) RemovalRed else Color.Black
    OutlinedButton(
        onClick = onToggle,
        modifier = modifier,
        // The two drawables differ here: 1dp on track, 3dp on remove.
        shape = RoundedCornerShape(if (isTracking) 3.dp else 1.dp),
        border = BorderStroke(1.dp, accent),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = accent
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
    ) {
        Text(
            text = stringResource(
                if (isTracking) R.string.remove_button else R.string.track_button
            ).uppercase(Locale.ROOT),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TrackButtonSectionPreview() {
    CourseGrabTheme {
        TrackButton(
            isTracking = false,
            onToggle = {},
            modifier = Modifier.height(26.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TrackButtonRowPreview() {
    CourseGrabTheme {
        TrackButton(
            isTracking = true,
            onToggle = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        )
    }
}
