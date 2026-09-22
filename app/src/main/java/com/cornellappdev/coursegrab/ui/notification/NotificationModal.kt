package com.cornellappdev.coursegrab.ui.notification

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import com.cornellappdev.coursegrab.models.CourseNotification
import com.cornellappdev.coursegrab.ui.main.MainActivity
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme

class NotificationModal : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notification = IntentCompat.getParcelableExtra(
            intent,
            EXTRA_COURSE_DETAILS,
            CourseNotification::class.java
        )

        if (notification == null) {
            // Launched without a payload; there is nothing to announce.
            finish()
            return
        }

        setContent {
            CourseGrabTheme {
                NotificationModalScreen(
                    course = notification.section,
                    onOpenStudentCenter = {
                        startActivity(Intent(Intent.ACTION_VIEW, STUDENT_CENTER_URL.toUri()))
                    },
                    onBackHome = {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_COURSE_DETAILS = "courseDetails"
        private const val STUDENT_CENTER_URL = "http://studentcenter.cornell.edu"
    }
}
