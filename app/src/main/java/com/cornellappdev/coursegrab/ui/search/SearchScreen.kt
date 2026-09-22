package com.cornellappdev.coursegrab.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.ui.components.BACK_BUTTON_WIDTH
import com.cornellappdev.coursegrab.ui.components.CourseGrabTopBar
import com.cornellappdev.coursegrab.ui.components.EmptyState
import com.cornellappdev.coursegrab.ui.theme.CardBorder
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import com.cornellappdev.coursegrab.ui.theme.LightText

@Composable
fun SearchRoute(
    onOpenCourse: (SearchResult) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SearchScreen(
        state = state,
        onQueryChanged = viewModel::onQueryChanged,
        onOpenCourse = onOpenCourse,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun SearchScreen(
    state: SearchState,
    onQueryChanged: (String) -> Unit,
    onOpenCourse: (SearchResult) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // The query lives here rather than in the ViewModel: the ViewModel only ever reacts to
    // it, and rememberSaveable already carries it across configuration changes.
    var query by rememberSaveable { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CourseGrabTopBar(onBack = onBack) {
                TextField(
                    value = query,
                    onValueChange = {
                        query = it
                        onQueryChanged(it)
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(start = BACK_BUTTON_WIDTH, end = 8.dp),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_courses_hint),
                            color = LightText
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        onQueryChanged(query)
                        keyboard?.hide()
                    }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = LightText,
                        unfocusedTextColor = LightText,
                        cursorColor = LightText,
                        focusedIndicatorColor = LightText,
                        unfocusedIndicatorColor = LightText
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                SearchState.QueryTooShort -> EmptyState(
                    icon = R.drawable.ic_status_warning,
                    title = R.string.requires_longer_search,
                    subtitle = R.string.requires_longer_search_subtext
                )

                SearchState.Failed -> EmptyState(
                    icon = R.drawable.ic_status_warning,
                    title = R.string.search_failed,
                    subtitle = R.string.search_failed_subtext
                )

                is SearchState.Results ->
                    if (state.courses.isEmpty()) {
                        EmptyState(
                            icon = R.drawable.ic_status_closed,
                            title = R.string.no_courses_alert,
                            subtitle = R.string.no_results_alert_subtext_try_another
                        )
                    } else {
                        Results(courses = state.courses, onOpenCourse = onOpenCourse)
                    }
            }
        }
    }
}

@Composable
private fun Results(
    courses: List<SearchResult>,
    onOpenCourse: (SearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = pluralStringResource(
                    R.plurals.search_results_count,
                    courses.size,
                    courses.size
                ),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
            )
        }
        items(courses, key = { "${it.subjectCode} ${it.courseNum}" }) { course ->
            SearchResultRow(course = course, onClick = { onOpenCourse(course) })
        }
    }
}

/**
 * The XML put the click listener on the expand arrow alone, leaving the rest of a
 * card-shaped row inert. The whole row is the target here; the arrow stays as the
 * affordance.
 */
@Composable
private fun SearchResultRow(
    course: SearchResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, CardBorder),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    R.string.course_title_format,
                    course.subjectCode,
                    course.courseNum,
                    course.title
                ),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_open_arrow),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.padding(start = 14.dp)
            )
        }
    }
}

private val SampleResult = SearchResult(
    subjectCode = "CS",
    courseNum = 1998,
    title = "Intro to Android Development",
    sections = listOf(
        Course(
            catalogNum = 10032,
            courseNum = 1998,
            section = "LEC 001",
            status = "OPEN",
            subjectCode = "CS",
            title = "Intro to Android Development"
        )
    )
)

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun SearchScreenResultsPreview() {
    CourseGrabTheme {
        SearchScreen(
            state = SearchState.Results(listOf(SampleResult)),
            onQueryChanged = {},
            onOpenCourse = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun SearchScreenEmptyPreview() {
    CourseGrabTheme {
        SearchScreen(
            state = SearchState.Results(emptyList()),
            onQueryChanged = {},
            onOpenCourse = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun SearchScreenTooShortPreview() {
    CourseGrabTheme {
        SearchScreen(
            state = SearchState.QueryTooShort,
            onQueryChanged = {},
            onOpenCourse = {},
            onBack = {}
        )
    }
}
