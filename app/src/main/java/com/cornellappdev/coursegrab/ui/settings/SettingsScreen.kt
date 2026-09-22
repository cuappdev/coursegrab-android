package com.cornellappdev.coursegrab.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.components.CourseGrabTopBar
import com.cornellappdev.coursegrab.ui.components.EffectHandler
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme

@Composable
fun SettingsRoute(
    notificationsPermitted: Boolean,
    onRequestNotifications: () -> Unit,
    onOpenLink: (String) -> Unit,
    onSignedOut: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EffectHandler(viewModel.effects) { effect ->
        when (effect) {
            is SettingsEffect.Message -> snackbarHostState.showSnackbar(effect.text)
            SettingsEffect.SignedOut -> onSignedOut()
        }
    }

    SettingsScreen(
        state = state,
        notificationsPermitted = notificationsPermitted,
        snackbarHostState = snackbarHostState,
        onEmailAlertsChange = viewModel::setEmailAlerts,
        onMobileAlertsChange = { enabled ->
            if (notificationsPermitted) viewModel.setMobileAlerts(enabled)
            else onRequestNotifications()
        },
        onOpenLink = onOpenLink,
        onSignOut = viewModel::signOut,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    notificationsPermitted: Boolean,
    snackbarHostState: SnackbarHostState,
    onEmailAlertsChange: (Boolean) -> Unit,
    onMobileAlertsChange: (Boolean) -> Unit,
    onOpenLink: (String) -> Unit,
    onSignOut: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { CourseGrabTopBar(title = R.string.settings, onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            @Suppress("ConstantConditionIf")
            if (SHOW_EMAIL_ALERTS) {
                SettingsSwitch(
                    label = R.string.email_alerts,
                    checked = state.emailAlertsEnabled,
                    onCheckedChange = onEmailAlertsChange
                )
            }

            SettingsSwitch(
                label = R.string.mobile_alerts,
                checked = state.mobileAlertsEnabled && notificationsPermitted,
                onCheckedChange = onMobileAlertsChange
            )

            SettingsLink(
                label = R.string.class_roster,
                onClick = { onOpenLink(CLASS_ROSTER_URL) }
            )
            SettingsLink(
                label = R.string.cornell_academic_calendar,
                onClick = { onOpenLink(ACADEMIC_CALENDAR_URL) }
            )

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(1.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = stringResource(R.string.sign_out),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun SettingsSwitch(
    @StringRes label: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsLink(
    @StringRes label: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        shape = RoundedCornerShape(0.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primaryContainer,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private const val SHOW_EMAIL_ALERTS = false
private const val CLASS_ROSTER_URL = "https://classes.cornell.edu/"
private const val ACADEMIC_CALENDAR_URL = "https://registrar.cornell.edu/academic-calendar"

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SettingsScreenPreview() {
    CourseGrabTheme {
        SettingsScreen(
            state = SettingsState(mobileAlertsEnabled = true),
            notificationsPermitted = true,
            snackbarHostState = remember { SnackbarHostState() },
            onEmailAlertsChange = {},
            onMobileAlertsChange = {},
            onOpenLink = {},
            onSignOut = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SettingsScreenNoPermissionPreview() {
    CourseGrabTheme {
        SettingsScreen(
            state = SettingsState(mobileAlertsEnabled = true),
            notificationsPermitted = false,
            snackbarHostState = remember { SnackbarHostState() },
            onEmailAlertsChange = {},
            onMobileAlertsChange = {},
            onOpenLink = {},
            onSignOut = {},
            onBack = {}
        )
    }
}
