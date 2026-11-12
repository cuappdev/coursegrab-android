package com.cornellappdev.coursegrab.ui.details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.ui.components.CourseGrabCard
import com.cornellappdev.coursegrab.ui.components.CourseGrabOutlinedButton
import com.cornellappdev.coursegrab.ui.components.EffectHandler
import com.cornellappdev.coursegrab.ui.components.SectionStatusIcon
import com.cornellappdev.coursegrab.ui.components.StatusBarIcons
import com.cornellappdev.coursegrab.ui.components.displayInstructor
import com.cornellappdev.coursegrab.ui.components.displaySection
import com.cornellappdev.coursegrab.ui.components.sampleCourse
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTextStyles

private val H2 = CourseGrabTextStyles.H3.copy(fontSize = 18.sp)

@Composable
fun CourseDetailsScreen(
    onBack: (trackingChanged: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CourseDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    StatusBarIcons(onDarkBackground = false)
    BackHandler { onBack(state.hasTrackingChanges) }

    EffectHandler(viewModel.trackingErrors) { error ->
        snackbarHostState.showSnackbar(error.message ?: resources.getString(error.fallback))
    }

    CourseDetailsContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onSetTracking = viewModel::setTracking,
        onBack = { onBack(state.hasTrackingChanges) },
        modifier = modifier
    )
}

@Composable
private fun CourseDetailsContent(
    state: CourseDetailsState,
    snackbarHostState: SnackbarHostState,
    onSetTracking: (catalogNum: Int, tracking: Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val layout = remember(state.sections) { layoutSections(state.sections) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CourseGrabColors.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 16.dp,
                bottom = 24.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item(key = "header") { DetailsHeader(code = state.code, onBack = onBack) }
            item(key = "summary") {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(state.title, style = H2, color = CourseGrabColors.Black)
                    layout.primary?.let { PrimarySection(it, onSetTracking) }
                }
            }
            layout.groups.forEach { group ->
                item(key = "group-${group.type}") {
                    Text(
                        text = stringResource(R.string.section_group_title, sectionTypeName(group.type)),
                        style = CourseGrabTextStyles.H3,
                        color = CourseGrabColors.Black
                    )
                }
                items(group.sections, key = { it.catalogNum }) { section ->
                    SectionCard(
                        section = section,
                        onTrackingChange = { onSetTracking(section.catalogNum, it) }
                    )
                }
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
private fun DetailsHeader(code: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(30.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_cg_back),
                contentDescription = stringResource(R.string.back)
            )
        }
        Text(code, style = CourseGrabTextStyles.H1, color = CourseGrabColors.Black)
    }
}

@Composable
private fun PrimarySection(section: Course, onSetTracking: (Int, Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionStatusIcon(section.sectionStatus)
            Text(section.displaySection, style = CourseGrabTextStyles.H3, color = CourseGrabColors.Black)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(section.mode, style = CourseGrabTextStyles.Body, color = CourseGrabColors.DarkGray)
            Text(
                text = section.displayInstructor() ?: stringResource(R.string.instructor_staff),
                style = CourseGrabTextStyles.Body,
                color = CourseGrabColors.DarkGray
            )
        }
        CourseGrabOutlinedButton(
            onClick = { onSetTracking(section.catalogNum, !section.isTracking) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_cg_track_bell),
                    contentDescription = null,
                    modifier = Modifier.size(width = 15.dp, height = 16.dp)
                )
                Text(
                    text = stringResource(
                        if (section.isTracking) R.string.tracking_button else R.string.track_button
                    ).uppercase(),
                    style = CourseGrabTextStyles.Label,
                    color = if (section.isTracking) CourseGrabColors.Black else CourseGrabColors.DarkGray
                )
            }
        }
    }
}

@Composable
private fun SectionCard(section: Course, onTrackingChange: (Boolean) -> Unit) {
    CourseGrabCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SectionStatusIcon(section.sectionStatus, Modifier.padding(top = 1.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(section.displaySection, style = CourseGrabTextStyles.H3, color = CourseGrabColors.Black)
                    Text(
                        text = listOfNotNull(section.mode.ifBlank { null }, section.displayInstructor())
                            .joinToString(" · "),
                        style = CourseGrabTextStyles.Body,
                        color = CourseGrabColors.DarkGray
                    )
                }
            }
            TrackingToggle(tracking = section.isTracking, onChange = onTrackingChange)
        }
    }
}

@Composable
private fun TrackingToggle(tracking: Boolean, onChange: (Boolean) -> Unit) {
    val label = stringResource(R.string.track_button)
    val state = stringResource(if (tracking) R.string.tracking_on else R.string.tracking_off)
    IconButton(
        onClick = { onChange(!tracking) },
        modifier = Modifier
            .size(28.dp)
            .semantics {
                role = Role.Switch
                stateDescription = state
            }
    ) {
        Image(
            painter = painterResource(
                if (tracking) R.drawable.ic_cg_tracking_on else R.drawable.ic_cg_tracking_off
            ),
            contentDescription = label
        )
    }
}

@Composable
private fun sectionTypeName(type: String): String = when (type) {
    "LEC" -> stringResource(R.string.section_type_lecture)
    "DIS" -> stringResource(R.string.section_type_discussion)
    "LAB" -> stringResource(R.string.section_type_lab)
    "SEM" -> stringResource(R.string.section_type_seminar)
    "IND" -> stringResource(R.string.section_type_independent)
    else -> type
}

@Preview(widthDp = 402, heightDp = 874)
@Composable
private fun CourseDetailsPreview() {
    CourseDetailsContent(
        state = CourseDetailsState(
            subjectCode = "ANTHR",
            courseNum = 1400,
            title = "Introduction to Sociocultural Anthropology",
            sections = listOf(
                sampleCourse(catalogNum = 1, section = "LEC 001 / MW 10:10AM"),
                sampleCourse(catalogNum = 2, section = "DIS 201 / R 9:05AM", isOpen = false),
                sampleCourse(catalogNum = 3, section = "DIS 202 / R 10:10AM", isTracking = true)
            )
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onSetTracking = { _, _ -> },
        onBack = {}
    )
}
