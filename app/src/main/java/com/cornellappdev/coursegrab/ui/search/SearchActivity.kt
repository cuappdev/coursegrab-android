package com.cornellappdev.coursegrab.ui.search

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cornellappdev.coursegrab.ui.details.CourseDetailsActivity
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CourseGrabTheme {
                SearchRoute(
                    onOpenCourse = { course ->
                        startActivity(
                            Intent(this, CourseDetailsActivity::class.java).apply {
                                putExtra("courseDetails", course)
                            }
                        )
                    },
                    onBack = ::finish
                )
            }
        }
    }
}
