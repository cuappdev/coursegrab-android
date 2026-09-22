package com.cornellappdev.coursegrab.ui.main

import androidx.annotation.ColorRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.ui.components.CourseGrabTopBar
import com.cornellappdev.coursegrab.ui.components.EffectHandler
import com.cornellappdev.coursegrab.ui.components.EmptyState
import com.cornellappdev.coursegrab.ui.components.sampleCourse
import com.cornellappdev.coursegrab.ui.components.EnrollButton
import com.cornellappdev.coursegrab.ui.components.RemoveButton
import com.cornellappdev.coursegrab.ui.components.CourseRow
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import com.cornellappdev.coursegrab.ui.theme.StatusGreen

@Composable
fun MainRoute(
    onOpenCourse: (SearchResult) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit,
    onEnroll: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EffectHandler(viewModel.effects) { effect ->
        when (effect) {
            is MainEffect.Message -> snackbarHostState.showSnackbar(effect.text)
            is MainEffect.OpenCourse -> onOpenCourse(effect.course)
        }
    }

    MainScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onRefresh = viewModel::refresh,
        onOpenCourse = viewModel::openCourse,
        onRemoveCourse = viewModel::removeCourse,
        onEnroll = onEnroll,
        onOpenSettings = onOpenSettings,
        onOpenSearch = onOpenSearch,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: TrackedCoursesState,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onOpenCourse: (Int) -> Unit,
    onRemoveCourse: (Int) -> Unit,
    onEnroll: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            MainTopBar(onOpenSettings = onOpenSettings, onOpenSearch = onOpenSearch)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.hasLoaded && state.available.isEmpty() && state.awaiting.isEmpty()) {
                EmptyState(
                    icon = R.drawable.ic_status_open,
                    title = R.string.no_courses_alert,
                    subtitle = R.string.no_courses_alert_subtext
                )
            } else {
                // One list, where the XML nested two RecyclerViews in a NestedScrollView and
                // toggled each section's visibility. Section headers are plain items: they
                // scroll away with their courses, as they did before.
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (state.available.isNotEmpty()) {
                        item(key = SECTION_AVAILABLE) {
                            SectionHeader(
                                text = stringResource(
                                    R.string.available_count,
                                    state.available.size
                                ),
                                color = StatusGreen
                            )
                        }
                        items(state.available, key = { it.catalogNum }) { course ->
                            CourseRow(
                                course = course,
                                onClick = { onOpenCourse(course.catalogNum) }
                            ) {
                                RemoveButton(
                                    onClick = { onRemoveCourse(course.catalogNum) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                                EnrollButton(
                                    onClick = onEnroll,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                        }
                    }

                    if (state.awaiting.isNotEmpty()) {
                        item(key = SECTION_AWAITING) {
                            SectionHeader(
                                text = stringResource(
                                    R.string.awaiting_count,
                                    state.awaiting.size
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        items(state.awaiting, key = { it.catalogNum }) { course ->
                            CourseRow(
                                course = course,
                                onClick = { onOpenCourse(course.catalogNum) }
                            ) {
                                RemoveButton(
                                    onClick = { onRemoveCourse(course.catalogNum) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Main's bar carries an icon on each side instead of a back button, so it builds on the
 * plain [androidx.compose.material3.Surface] shape rather than [CourseGrabTopBar], whose
 * start slot is always the back arrow.
 */
@Composable
private fun MainTopBar(
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Black,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = stringResource(R.string.settings),
                    tint = Color.White
                )
            }
            IconButton(
                onClick = onOpenSearch,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search_courses_hint),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        color = color,
        modifier = modifier.padding(top = 2.dp, bottom = 4.dp)
    )
}

private const val SECTION_AVAILABLE = "header-available"
private const val SECTION_AWAITING = "header-awaiting"

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun MainScreenPreview() {
    CourseGrabTheme {
        MainScreen(
            state = TrackedCoursesState(
                available = listOf(sampleCourse(isTracking = true)),
                awaiting = listOf(
                    sampleCourse(
                        catalogNum = 10755,
                        subjectCode = "COGST",
                        courseNum = 1101,
                        title = "Introduction to Cognitive Science",
                        section = "LEC 001 / TR 11:40AM",
                        isOpen = false,
                        isTracking = true
                    )
                ),
                hasLoaded = true
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onRefresh = {},
            onOpenCourse = {},
            onRemoveCourse = {},
            onEnroll = {},
            onOpenSettings = {},
            onOpenSearch = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun MainScreenEmptyPreview() {
    CourseGrabTheme {
        MainScreen(
            state = TrackedCoursesState(hasLoaded = true),
            snackbarHostState = remember { SnackbarHostState() },
            onRefresh = {},
            onOpenCourse = {},
            onRemoveCourse = {},
            onEnroll = {},
            onOpenSettings = {},
            onOpenSearch = {}
        )
    }
}
