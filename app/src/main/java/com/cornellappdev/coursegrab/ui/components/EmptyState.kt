package com.cornellappdev.coursegrab.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme

@Composable
fun EmptyState(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes subtitle: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // The status drawables are 16dp intrinsic — the empty-state ImageViews scale them
        // to 96dp. Without an explicit size these render as the tiny row-status dot.
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(ICON_SIZE)
        )
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private val ICON_SIZE = 96.dp

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun EmptyStateNoResultsPreview() {
    CourseGrabTheme {
        EmptyState(
            icon = R.drawable.ic_status_closed,
            title = R.string.no_courses_alert,
            subtitle = R.string.no_results_alert_subtext_try_another
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun EmptyStateFailedPreview() {
    CourseGrabTheme {
        EmptyState(
            icon = R.drawable.ic_status_warning,
            title = R.string.search_failed,
            subtitle = R.string.search_failed_subtext
        )
    }
}
