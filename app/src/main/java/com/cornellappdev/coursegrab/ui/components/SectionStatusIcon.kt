package com.cornellappdev.coursegrab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.models.SectionStatus
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTextStyles

@Composable
fun SectionStatusIcon(status: SectionStatus, modifier: Modifier = Modifier) {
    val description = stringResource(status.label)
    val semantics = modifier.semantics { contentDescription = description }
    when (status) {
        SectionStatus.Open -> Box(
            semantics
                .size(18.dp)
                .background(CourseGrabColors.Green, CircleShape)
        )

        SectionStatus.Closed -> Box(
            semantics
                .size(18.dp)
                .background(CourseGrabColors.Red, RoundedCornerShape(4.dp))
        )

        SectionStatus.Waitlisted -> Image(
            painter = painterResource(R.drawable.ic_cg_status_waitlisted),
            contentDescription = null,
            modifier = semantics.size(22.dp)
        )
    }
}

@Composable
fun SectionStatusPill(status: SectionStatus, count: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .outlinedSurface(PillShape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionStatusIcon(status)
        Text(
            text = stringResource(status.countLabel, count),
            style = CourseGrabTextStyles.Body,
            color = CourseGrabColors.Black,
            maxLines = 1
        )
    }
}

private val SectionStatus.label: Int
    get() = when (this) {
        SectionStatus.Open -> R.string.status_open
        SectionStatus.Closed -> R.string.status_closed
        SectionStatus.Waitlisted -> R.string.status_waitlisted
    }

private val SectionStatus.countLabel: Int
    get() = when (this) {
        SectionStatus.Open -> R.string.status_open_count
        SectionStatus.Closed -> R.string.status_closed_count
        SectionStatus.Waitlisted -> R.string.status_waitlisted_count
    }

@Preview(showBackground = true)
@Composable
private fun SectionStatusPillPreview() {
    Row(
        Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionStatusPill(SectionStatus.Open, 1)
        SectionStatusPill(SectionStatus.Closed, 3)
        SectionStatusPill(SectionStatus.Waitlisted, 0)
    }
}
