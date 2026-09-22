package com.cornellappdev.coursegrab.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cornellappdev.coursegrab.ui.details.CourseDetailsActivity
import com.cornellappdev.coursegrab.ui.search.SearchActivity
import com.cornellappdev.coursegrab.ui.settings.SettingsActivity
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CourseGrabTheme {
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { granted ->
                    if (!granted) viewModel.reportNotificationsBlocked()
                }

                LaunchedEffect(Unit) {
                    if (!hasNotificationPermission()) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                MainRoute(
                    onOpenCourse = { course ->
                        startActivity(
                            Intent(this, CourseDetailsActivity::class.java).apply {
                                putExtra(CourseDetailsActivity.EXTRA_COURSE_DETAILS, course)
                            }
                        )
                    },
                    onOpenSettings = {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    },
                    onOpenSearch = {
                        startActivity(Intent(this, SearchActivity::class.java))
                    },
                    onEnroll = {
                        startActivity(Intent(Intent.ACTION_VIEW, STUDENT_CENTER_URL.toUri()))
                    },
                    viewModel = viewModel
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun hasNotificationPermission(): Boolean =
        Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    private companion object {
        const val STUDENT_CENTER_URL = "http://studentcenter.cornell.edu"
    }
}
