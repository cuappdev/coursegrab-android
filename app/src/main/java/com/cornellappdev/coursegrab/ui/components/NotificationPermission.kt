package com.cornellappdev.coursegrab.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect

@Stable
class NotificationPermissionState internal constructor(
    private val context: Context,
    private val activity: Activity?
) {
    var canNotify by mutableStateOf(notificationsEnabled())
        private set

    val shouldPrompt: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED

    internal var launcher: ManagedActivityResultLauncher<String, Boolean>? = null
    private var onGranted: () -> Unit = {}
    private var onDenied: (permanently: Boolean) -> Unit = {}

    fun request(onGranted: () -> Unit = {}, onDenied: (permanently: Boolean) -> Unit = {}) {
        val launcher = launcher
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || launcher == null) {
            onDenied(true)
            return
        }
        this.onGranted = onGranted
        this.onDenied = onDenied
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    fun openSystemSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else {
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                "package:${context.packageName}".toUri()
            )
        }
        context.startActivity(intent)
    }

    internal fun refresh() {
        canNotify = notificationsEnabled()
    }

    internal fun onPermissionResult(granted: Boolean) {
        refresh()
        if (granted) onGranted() else onDenied(!canAskAgain())
        onGranted = {}
        onDenied = {}
    }

    private fun canAskAgain(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && activity != null &&
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                )

    private fun notificationsEnabled(): Boolean =
        NotificationManagerCompat.from(context).areNotificationsEnabled()
}

@Composable
fun rememberNotificationPermission(): NotificationPermissionState {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val state = remember(context, activity) { NotificationPermissionState(context, activity) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        state::onPermissionResult
    )
    DisposableEffect(state, launcher) {
        state.launcher = launcher
        onDispose { state.launcher = null }
    }
    LifecycleResumeEffect(state) {
        state.refresh()
        onPauseOrDispose {}
    }
    return state
}
