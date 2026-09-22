package com.cornellappdev.coursegrab.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.ui.theme.CardBorder
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import com.cornellappdev.coursegrab.ui.theme.PrimaryText
import java.util.Locale

@Composable
fun CourseRow(
    course: Course,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, CardBorder),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = stringResource(
                        R.string.course_title_format,
                        course.subjectCode,
                        course.courseNum,
                        course.title
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                StatusIndicator(isOpen = course.isOpen, modifier = Modifier.padding(start = 12.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = course.section.uppercase(Locale.ROOT),
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = course.catalogNum.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryText,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .height(BUTTON_ROW_HEIGHT),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
    }
}

private val BUTTON_ROW_HEIGHT = 44.dp

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CourseRowOpenPreview() {
    CourseGrabTheme {
        CourseRow(course = sampleCourse(), onClick = {}) {
            TrackButton(
                onClick = {},
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CourseRowClosedPreview() {
    CourseGrabTheme {
        CourseRow(
            course = sampleCourse(isOpen = false, isTracking = true),
            onClick = {}
        ) {
            RemoveButton(
                onClick = {},
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}
