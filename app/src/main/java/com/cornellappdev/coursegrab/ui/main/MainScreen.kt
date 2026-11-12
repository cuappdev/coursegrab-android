package com.cornellappdev.coursegrab.ui.main

import android.content.ClipData
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.models.SectionStatus
import com.cornellappdev.coursegrab.ui.components.CardShape
import com.cornellappdev.coursegrab.ui.components.CourseGrabCard
import com.cornellappdev.coursegrab.ui.components.CourseGrabHeader
import com.cornellappdev.coursegrab.ui.components.CourseGrabOutlinedButton
import com.cornellappdev.coursegrab.ui.components.CourseGrabPrimaryButton
import com.cornellappdev.coursegrab.ui.components.EffectHandler
import com.cornellappdev.coursegrab.ui.components.SearchFieldButton
import com.cornellappdev.coursegrab.ui.components.SectionStatusIcon
import com.cornellappdev.coursegrab.ui.components.SectionStatusPill
import com.cornellappdev.coursegrab.ui.components.StatusBarIcons
import com.cornellappdev.coursegrab.ui.components.StatusMessage
import com.cornellappdev.coursegrab.ui.components.StopTrackingDialog
import com.cornellappdev.coursegrab.ui.components.displayCode
import com.cornellappdev.coursegrab.ui.components.displayInstructor
import com.cornellappdev.coursegrab.ui.components.displaySection
import com.cornellappdev.coursegrab.ui.components.rememberNotificationPermission
import com.cornellappdev.coursegrab.ui.components.sampleCourse
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTextStyles
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    onOpenCourse: (SearchResult) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit,
    onEnroll: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val pinCopied = stringResource(R.string.pin_copied)
    var pendingRemoval by remember { mutableStateOf<Course?>(null) }

    StatusBarIcons(onDarkBackground = true)

    val notificationPermission = rememberNotificationPermission()
    var askedForNotifications by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!askedForNotifications && notificationPermission.shouldPrompt) {
            askedForNotifications = true
            notificationPermission.request(onDenied = { viewModel.reportNotificationsBlocked() })
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose {}
    }

    EffectHandler(viewModel.effects) { effect ->
        when (effect) {
            is MainEffect.Message -> snackbarHostState.showSnackbar(effect.text)
            is MainEffect.OpenCourse -> onOpenCourse(effect.course)
        }
    }

    MainContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onRefresh = viewModel::refresh,
        onOpenCourse = viewModel::openCourse,
        onRemoveCourse = { pendingRemoval = it },
        onCopyPin = { pin ->
            scope.launch {
                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("PIN", pin)))
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    snackbarHostState.showSnackbar(pinCopied)
                }
            }
        },
        onEnroll = onEnroll,
        onOpenSettings = onOpenSettings,
        onOpenSearch = onOpenSearch,
        modifier = modifier
    )

    pendingRemoval?.let { course ->
        StopTrackingDialog(
            courseCode = course.displayCode,
            onConfirm = {
                viewModel.removeCourse(course.catalogNum)
                pendingRemoval = null
            },
            onDismiss = { pendingRemoval = null }
        )
    }
}

@Composable
private fun MainContent(
    state: TrackedCoursesState,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onOpenCourse: (Int) -> Unit,
    onRemoveCourse: (Course) -> Unit,
    onCopyPin: (String) -> Unit,
    onEnroll: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CourseGrabColors.White)
    ) {
        Column(Modifier.fillMaxSize()) {
            CourseGrabHeader(onOpenSettings = onOpenSettings) {
                SearchFieldButton(onClick = onOpenSearch)
            }
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 24.dp,
                        end = 24.dp,
                        top = 24.dp,
                        bottom = 24.dp + WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    when {
                        state.isOffline && state.courses.isEmpty() -> item(key = "offline") {
                            StatusMessage(
                                illustration = R.drawable.ic_cg_illustration_no_internet,
                                title = R.string.no_internet_title,
                                subtitle = R.string.no_internet_subtitle
                            )
                        }

                        state.hasLoaded && state.courses.isEmpty() -> item(key = "empty") {
                            StatusMessage(
                                illustration = R.drawable.ic_cg_illustration_no_courses,
                                title = R.string.no_courses_title,
                                subtitle = R.string.no_courses_subtitle
                            )
                        }

                        state.courses.isNotEmpty() -> {
                            item(key = "title") {
                                Text(
                                    text = stringResource(R.string.tracked_classes),
                                    style = CourseGrabTextStyles.H1,
                                    color = CourseGrabColors.Black
                                )
                            }
                            item(key = "pills") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    listOf(
                                        SectionStatus.Open,
                                        SectionStatus.Closed,
                                        SectionStatus.Waitlisted
                                    ).forEach { status ->
                                        SectionStatusPill(status, state.count(status))
                                    }
                                }
                            }
                            items(state.courses, key = { it.catalogNum }) { course ->
                                TrackedCourseCard(
                                    course = course,
                                    onClick = { onOpenCourse(course.courseId) },
                                    onRemove = { onRemoveCourse(course) },
                                    onCopyPin = { onCopyPin(course.catalogNum.toString()) },
                                    onEnroll = onEnroll
                                )
                            }
                        }
                    }
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
private fun TrackedCourseCard(
    course: Course,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onCopyPin: () -> Unit,
    onEnroll: () -> Unit,
    modifier: Modifier = Modifier
) {
    CourseGrabCard(
        modifier = modifier
            .clip(CardShape)
            .clickable(onClick = onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionStatusIcon(course.sectionStatus)
            Text(
                text = course.displayCode,
                style = CourseGrabTextStyles.H3,
                color = CourseGrabColors.Black,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                Image(
                    painter = painterResource(R.drawable.ic_cg_close),
                    contentDescription = stringResource(R.string.stop_tracking_course, course.displayCode),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(course.title, style = CourseGrabTextStyles.H3, color = CourseGrabColors.Black)
        Spacer(Modifier.height(10.dp))
        Text(course.displaySection, style = CourseGrabTextStyles.Body, color = CourseGrabColors.DarkGray)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(course.mode, style = CourseGrabTextStyles.Body, color = CourseGrabColors.DarkGray)
            Text(
                text = course.displayInstructor() ?: stringResource(R.string.instructor_staff),
                style = CourseGrabTextStyles.Body,
                color = CourseGrabColors.DarkGray
            )
        }
        if (course.isOpen) {
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CourseGrabOutlinedButton(
                    onClick = onCopyPin,
                    modifier = Modifier.weight(1f),
                    trailingIcon = R.drawable.ic_cg_copy
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.pin_label))
                            append(' ')
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(course.catalogNum.toString())
                            }
                        },
                        style = CourseGrabTextStyles.Label
                    )
                }
                CourseGrabPrimaryButton(
                    text = stringResource(R.string.enroll_button),
                    onClick = onEnroll,
                    modifier = Modifier.weight(1f),
                    trailingIcon = R.drawable.ic_cg_external_link
                )
            }
        }
    }
}

private val previewCourses = listOf(
    sampleCourse(catalogNum = 1389, subjectCode = "ANTHR", courseNum = 1400,
        title = "Introduction to Sociocultural Anthropology",
        section = "LEC 001 / MW 10:10AM", isTracking = true),
    sampleCourse(catalogNum = 1390, subjectCode = "ANTHR", courseNum = 1400,
        title = "Introduction to Sociocultural Anthropology",
        section = "DIS 205 / R 1:25PM", isOpen = false, isTracking = true)
).map { it.copy(mode = "In-Person") }

@Preview(widthDp = 402, heightDp = 874)
@Composable
private fun MainScreenPreview() {
    MainContent(
        state = TrackedCoursesState(courses = previewCourses, hasLoaded = true),
        snackbarHostState = remember { SnackbarHostState() },
        onRefresh = {},
        onOpenCourse = {},
        onRemoveCourse = {},
        onCopyPin = {},
        onEnroll = {},
        onOpenSettings = {},
        onOpenSearch = {}
    )
}

@Preview(widthDp = 402, heightDp = 874)
@Composable
private fun MainScreenEmptyPreview() {
    MainContent(
        state = TrackedCoursesState(hasLoaded = true),
        snackbarHostState = remember { SnackbarHostState() },
        onRefresh = {},
        onOpenCourse = {},
        onRemoveCourse = {},
        onCopyPin = {},
        onEnroll = {},
        onOpenSettings = {},
        onOpenSearch = {}
    )
}

@Preview(widthDp = 402, heightDp = 874)
@Composable
private fun MainScreenOfflinePreview() {
    MainContent(
        state = TrackedCoursesState(isOffline = true),
        snackbarHostState = remember { SnackbarHostState() },
        onRefresh = {},
        onOpenCourse = {},
        onRemoveCourse = {},
        onCopyPin = {},
        onEnroll = {},
        onOpenSettings = {},
        onOpenSearch = {}
    )
}
