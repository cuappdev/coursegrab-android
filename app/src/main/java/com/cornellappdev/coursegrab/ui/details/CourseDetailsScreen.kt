package com.cornellappdev.coursegrab.ui.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.ui.components.CourseGrabTopBar
import com.cornellappdev.coursegrab.ui.components.EffectHandler
import com.cornellappdev.coursegrab.ui.components.SAMPLE_INSTRUCTOR
import com.cornellappdev.coursegrab.ui.components.StatusIndicator
import com.cornellappdev.coursegrab.ui.components.sampleCourse
import com.cornellappdev.coursegrab.ui.components.TrackButton
import com.cornellappdev.coursegrab.ui.theme.CardBorder
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme

@Composable
fun CourseDetailsRoute(
    course: SearchResult,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CourseDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(course) { viewModel.seed(course) }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EffectHandler(viewModel.trackingErrors) { message ->
        snackbarHostState.showSnackbar(message)
    }

    CourseDetailsScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onSetTracking = viewModel::setTracking,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun CourseDetailsScreen(
    state: CourseDetailsState,
    snackbarHostState: SnackbarHostState,
    onSetTracking: (catalogNum: Int, tracking: Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CourseGrabTopBar(
                title = stringResource(
                    R.string.course_header_format,
                    state.subjectCode,
                    state.courseNum
                ),
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CourseHeaderCard(
                title = state.title,
                instructor = state.instructor,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(state.sections, key = { it.catalogNum }) { section ->
                    SectionRow(
                        section = section,
                        onToggle = { onSetTracking(section.catalogNum, !section.isTracking) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseHeaderCard(
    title: String,
    instructor: String,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, CardBorder),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(start = 14.dp, top = 12.dp, bottom = 16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = instructor,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun SectionRow(
    section: Course,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusIndicator(isOpen = section.isOpen)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = section.section,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.ic_tracking_ppl_logo),
                    contentDescription = null
                )
                Text(
                    text = pluralStringResource(
                        R.plurals.tracking_count,
                        section.numTracking,
                        section.numTracking
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
        TrackButton(
            isTracking = section.isTracking,
            onToggle = onToggle,
            modifier = Modifier.height(26.dp)
        )
    }
}

private val SampleState = CourseDetailsState(
    subjectCode = "CS",
    courseNum = 1998,
    title = "Intro to Android Development",
    instructor = SAMPLE_INSTRUCTOR,
    sections = listOf(
        sampleCourse(catalogNum = 10032, section = "LEC 001 / M 7:30PM"),
        sampleCourse(
            catalogNum = 10033,
            section = "LEC 002 / W 7:30PM",
            isOpen = false,
            isTracking = true,
            numTracking = 3
        )
    )
)

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun CourseDetailsScreenPreview() {
    CourseGrabTheme {
        CourseDetailsScreen(
            state = SampleState,
            snackbarHostState = remember { SnackbarHostState() },
            onSetTracking = { _, _ -> },
            onBack = {}
        )
    }
}
