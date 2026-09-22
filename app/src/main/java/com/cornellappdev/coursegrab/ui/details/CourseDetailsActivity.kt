package com.cornellappdev.coursegrab.ui.details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.IntentCompat
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourseDetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val course = IntentCompat.getParcelableExtra(
            intent,
            EXTRA_COURSE_DETAILS,
            SearchResult::class.java
        )

        if (course == null) {
            // Nothing to show without a course; the caller always supplies one.
            finish()
            return
        }

        setContent {
            CourseGrabTheme {
                CourseDetailsRoute(course = course, onBack = ::finish)
            }
        }
    }

    companion object {
        const val EXTRA_COURSE_DETAILS = "courseDetails"
    }
}
