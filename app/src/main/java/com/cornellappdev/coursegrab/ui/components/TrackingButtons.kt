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

/**
 * Starts tracking a section. See [OutlinedActionButton] for why these are not Material3
 * defaults.
 *
 * Size is the caller's job, because usages differ: the course rows give it the full row
 * width at 44dp, while the section rows use 26dp and wrap. Pass both through [modifier].
 */
@Composable
fun TrackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedActionButton(
        label = R.string.track_button,
        accent = Color.Black,
        // track_button_background.xml
        cornerRadius = 1.dp,
        onClick = onClick,
        modifier = modifier
    )
}

/** Stops tracking a section. Sized by the caller, as [TrackButton] is. */
@Composable
fun RemoveButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedActionButton(
        label = R.string.remove_button,
        accent = RemovalRed,
        // remove_button_background.xml — 3dp where track uses 1dp.
        cornerRadius = 3.dp,
        onClick = onClick,
        modifier = modifier
    )
}

/**
 * The shape both buttons share: white fill and a 1dp outline in the accent color, matching
 * the XML button drawables. A filled M3 Button looks nothing like the shipped app.
 *
 * Track and remove are separate composables rather than one with an `isTracking` flag,
 * because most call sites know statically which one they want — the course rows on the
 * tracked-courses screen only ever remove — and a boolean there reads as a toggle that
 * could go either way.
 */
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
            // US-only app: invariant casing keeps the locale out of composition.
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
