package com.cornellappdev.coursegrab.ui.details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourseDetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!intent.hasExtra(EXTRA_COURSE_DETAILS)) {
            finish()
            return
        }

        setContent {
            CourseGrabTheme {
                CourseDetailsScreen(onBack = ::finish)
            }
        }
    }

    companion object {
        const val EXTRA_COURSE_DETAILS = "courseDetails"
    }
}
