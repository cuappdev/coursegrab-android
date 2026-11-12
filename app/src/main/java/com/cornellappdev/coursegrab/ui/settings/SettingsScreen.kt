package com.cornellappdev.coursegrab.ui.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.components.CourseGrabTitleBar
import com.cornellappdev.coursegrab.ui.components.EffectHandler
import com.cornellappdev.coursegrab.ui.components.StatusBarIcons
import com.cornellappdev.coursegrab.ui.components.courseGrabShadow
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTextStyles
import kotlinx.coroutines.launch

private const val ACADEMIC_CALENDAR_URL = "https://registrar.cornell.edu/academic-calendar"
private val LinkBlue = Color(0xFF4A90E2)
private val LinkShape = RoundedCornerShape(8.dp)

@Composable
fun SettingsScreen(
    onOpenLink: (String) -> Unit,
    onSignedOut: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = LocalActivity.current

    StatusBarIcons(onDarkBackground = false)

    var notificationsPermitted by remember { mutableStateOf(hasNotificationPermission(context)) }
    LifecycleResumeEffect(Unit) {
        notificationsPermitted = hasNotificationPermission(context)
        onPauseOrDispose {}
    }

    var onPermissionGranted by remember { mutableStateOf<(() -> Unit)?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationsPermitted = granted
        if (granted) onPermissionGranted?.invoke()
        onPermissionGranted = null
        val canAskAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            activity != null &&
            ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.POST_NOTIFICATIONS
        )
        if (!granted && !canAskAgain) openNotificationSettings(context)
    }

    val onRequestNotifications: (() -> Unit) -> Unit = { onGranted ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onPermissionGranted = onGranted
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            openNotificationSettings(context)
        }
    }

    EffectHandler(viewModel.effects) { effect ->
        when (effect) {
            is SettingsEffect.Message -> scope.launch { snackbarHostState.showSnackbar(effect.text) }
            SettingsEffect.SignedOut -> onSignedOut()
        }
    }

    SettingsContent(
        state = state,
        notificationsPermitted = notificationsPermitted,
        snackbarHostState = snackbarHostState,
        onMobileAlertsChange = { enabled ->
            if (notificationsPermitted) viewModel.setMobileAlerts(enabled)
            else onRequestNotifications { viewModel.setMobileAlerts(enabled) }
        },
        onOpenLink = onOpenLink,
        onSignOut = viewModel::signOut,
        onBack = onBack,
        modifier = modifier
    )
}

private fun hasNotificationPermission(context: Context): Boolean =
    NotificationManagerCompat.from(context).areNotificationsEnabled()

private fun openNotificationSettings(context: Context) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    } else {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, "package:${context.packageName}".toUri())
    }
    context.startActivity(intent)
}

@Composable
private fun SettingsContent(
    state: SettingsState,
    notificationsPermitted: Boolean,
    snackbarHostState: SnackbarHostState,
    onMobileAlertsChange: (Boolean) -> Unit,
    onOpenLink: (String) -> Unit,
    onSignOut: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CourseGrabColors.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CourseGrabTitleBar(title = stringResource(R.string.settings), onBack = onBack)
            AppDevCredit()
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                SettingsToggle(
                    label = R.string.mobile_alerts,
                    checked = state.mobileAlertsEnabled && notificationsPermitted,
                    onCheckedChange = onMobileAlertsChange
                )
                AcademicCalendarLink(onClick = { onOpenLink(ACADEMIC_CALENDAR_URL) })
            }
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.email.orEmpty(),
                    style = CourseGrabTextStyles.H3,
                    color = CourseGrabColors.DarkGray,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.sign_out),
                    style = CourseGrabTextStyles.H3,
                    color = CourseGrabColors.Red,
                    modifier = Modifier.clickable(role = Role.Button, onClick = onSignOut)
                )
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}

@Composable
private fun AppDevCredit() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_cg_appdev_rocket),
            contentDescription = null,
            modifier = Modifier.size(width = 36.712.dp, height = 79.372.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.designed_and_developed_by),
                style = CourseGrabTextStyles.H4,
                color = CourseGrabColors.DarkGray
            )
            Text(
                text = buildAnnotatedString {
                    append("Cornell")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("AppDev") }
                },
                fontSize = 24.sp,
                letterSpacing = 2.sp,
                color = CourseGrabColors.Black
            )
        }
    }
}

@Composable
private fun SettingsToggle(
    @StringRes label: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(label), style = CourseGrabTextStyles.H3, color = CourseGrabColors.Black)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = CourseGrabColors.Green,
                checkedThumbColor = CourseGrabColors.White,
                checkedBorderColor = CourseGrabColors.Green,
                uncheckedTrackColor = CourseGrabColors.Gray,
                uncheckedThumbColor = CourseGrabColors.White,
                uncheckedBorderColor = CourseGrabColors.Gray
            )
        )
    }
}

@Composable
private fun AcademicCalendarLink(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .courseGrabShadow(LinkShape, blur = 10.dp)
            .clip(LinkShape)
            .background(CourseGrabColors.White)
            .border(0.5.dp, CourseGrabColors.Gray, LinkShape)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.cornell_academic_calendar),
            style = CourseGrabTextStyles.H3,
            color = LinkBlue
        )
        Image(
            painter = painterResource(R.drawable.ic_cg_link_out),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(widthDp = 402, heightDp = 874)
@Composable
private fun SettingsScreenPreview() {
    SettingsContent(
        state = SettingsState(mobileAlertsEnabled = true, email = "jj775@cornell.edu"),
        notificationsPermitted = true,
        snackbarHostState = remember { SnackbarHostState() },
        onMobileAlertsChange = {},
        onOpenLink = {},
        onSignOut = {},
        onBack = {}
    )
}
