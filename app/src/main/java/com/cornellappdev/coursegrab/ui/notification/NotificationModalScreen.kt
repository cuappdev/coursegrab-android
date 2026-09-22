package com.cornellappdev.coursegrab.ui.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import com.cornellappdev.coursegrab.ui.theme.DarkEnough
import com.cornellappdev.coursegrab.ui.theme.LightText
import com.cornellappdev.coursegrab.ui.theme.StatusGreen

/**
 * The full-screen takeover a course-open notification lands on. No ViewModel: everything
 * shown comes from the notification payload the caller already parsed.
 */
@Composable
fun NotificationModalScreen(
    course: Course,
    onOpenStudentCenter: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkEnough)
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.available_now),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 22.sp,
            color = StatusGreen
        )
        Text(
            text = stringResource(
                R.string.course_title_format,
                course.subjectCode,
                course.courseNum,
                course.title
            ),
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 24.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = course.section,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = course.catalogNum.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 60.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 32.dp)
        )
        Text(
            text = stringResource(R.string.course_id),
            style = MaterialTheme.typography.bodyLarge,
            color = LightText
        )

        Spacer(Modifier.height(48.dp))

        OutlinedButton(
            onClick = onOpenStudentCenter,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            // white_rounded_border.xml: transparent fill, white outline.
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color.White),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(R.string.open_student_center),
                fontSize = 18.sp
            )
        }

        TextButton(
            onClick = onBackHome,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.back_to_home),
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}

private val SampleCourse = Course(
    catalogNum = 10032,
    courseNum = 1998,
    section = "LEC 001 / M 7:30PM",
    status = "OPEN",
    subjectCode = "CS",
    title = "Intro to Android Development"
)

@Preview(widthDp = 360, heightDp = 720)
@Composable
private fun NotificationModalScreenPreview() {
    CourseGrabTheme {
        NotificationModalScreen(
            course = SampleCourse,
            onOpenStudentCenter = {},
            onBackHome = {}
        )
    }
}
