package com.cornellappdev.coursegrab.ui.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SectionStatus
import com.cornellappdev.coursegrab.ui.components.SectionStatusIcon
import com.cornellappdev.coursegrab.ui.components.StatusBarIcons
import com.cornellappdev.coursegrab.ui.components.displaySection
import com.cornellappdev.coursegrab.ui.components.meshGradientBackground
import com.cornellappdev.coursegrab.ui.components.sampleCourse
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTextStyles

private val PinStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 60.sp)

@Composable
fun NotificationModalScreen(
    course: Course,
    onOpenStudentCenter: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarIcons(onDarkBackground = true)

    Column(
        modifier = modifier
            .fillMaxSize()
            .meshGradientBackground()
            .safeDrawingPadding()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionStatusIcon(SectionStatus.Open)
            Text(
                text = stringResource(R.string.available_now),
                style = CourseGrabTextStyles.H1,
                color = CourseGrabColors.Green
            )
        }
        Text(
            text = stringResource(
                R.string.course_title_format,
                course.subjectCode,
                course.courseNum,
                course.title
            ),
            style = CourseGrabTextStyles.H1.copy(fontSize = 24.sp),
            color = CourseGrabColors.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = course.displaySection,
            style = CourseGrabTextStyles.H3,
            color = CourseGrabColors.BellGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = course.catalogNum.toString(),
            style = PinStyle,
            color = CourseGrabColors.White,
            modifier = Modifier.padding(top = 32.dp)
        )
        Text(
            text = stringResource(R.string.course_id),
            style = CourseGrabTextStyles.Body,
            color = CourseGrabColors.BellGray
        )

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = onOpenStudentCenter,
            modifier = Modifier
                .fillMaxWidth()
                .height(43.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CourseGrabColors.White,
                contentColor = CourseGrabColors.Black
            )
        ) {
            Text(stringResource(R.string.open_student_center).uppercase(), style = CourseGrabTextStyles.Label)
        }
        TextButton(
            onClick = onBackHome,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(43.dp)
        ) {
            Text(
                text = stringResource(R.string.back_to_home).uppercase(),
                style = CourseGrabTextStyles.Label,
                color = CourseGrabColors.BellGray
            )
        }
    }
}

@Preview(widthDp = 402, heightDp = 874)
@Composable
private fun NotificationModalScreenPreview() {
    NotificationModalScreen(
        course = sampleCourse(),
        onOpenStudentCenter = {},
        onBackHome = {}
    )
}
