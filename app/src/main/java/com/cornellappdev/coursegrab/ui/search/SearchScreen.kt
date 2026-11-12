package com.cornellappdev.coursegrab.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.ui.components.CardShape
import com.cornellappdev.coursegrab.ui.components.CourseGrabCard
import com.cornellappdev.coursegrab.ui.components.CourseGrabHeader
import com.cornellappdev.coursegrab.ui.components.SearchFieldRow
import com.cornellappdev.coursegrab.ui.components.StatusBarIcons
import com.cornellappdev.coursegrab.ui.components.StatusMessage
import com.cornellappdev.coursegrab.ui.components.sampleSearchResult
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTextStyles

@Composable
fun SearchScreen(
    onOpenCourse: (SearchResult) -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StatusBarIcons(onDarkBackground = true)

    SearchContent(
        state = state,
        onQueryChanged = viewModel::onQueryChanged,
        onOpenCourse = onOpenCourse,
        onOpenSettings = onOpenSettings,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun SearchContent(
    state: SearchState,
    onQueryChanged: (String) -> Unit,
    onOpenCourse: (SearchResult) -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CourseGrabColors.White)
    ) {
        CourseGrabHeader(onOpenSettings = onOpenSettings) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchField(
                    query = query,
                    onQueryChange = {
                        query = it
                        onQueryChanged(it)
                    },
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = CourseGrabColors.LightGray
                    )
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_cg_clear_search),
                        contentDescription = stringResource(R.string.close_search),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Box(Modifier.fillMaxSize()) {
            when (state) {
                SearchState.QueryTooShort -> if (query.isNotEmpty()) {
                    StatusMessage(
                        illustration = null,
                        title = R.string.requires_longer_search,
                        subtitle = R.string.requires_longer_search_subtext
                    )
                }

                SearchState.Failed -> StatusMessage(
                    illustration = R.drawable.ic_cg_illustration_no_internet,
                    title = R.string.search_failed,
                    subtitle = R.string.search_failed_subtext
                )

                is SearchState.Results -> if (state.courses.isEmpty()) {
                    StatusMessage(
                        illustration = R.drawable.ic_cg_illustration_no_courses,
                        title = R.string.no_results,
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
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    SearchFieldRow(modifier = modifier) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = CourseGrabTextStyles.H4.copy(color = CourseGrabColors.Black),
            cursorBrush = SolidColor(CourseGrabColors.Black),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            decorationBox = { field ->
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search_for_a_course),
                        style = CourseGrabTextStyles.H4,
                        color = CourseGrabColors.DarkGray
                    )
                }
                field()
            }
        )
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
        contentPadding = PaddingValues(
            start = 24.dp,
            end = 24.dp,
            top = 24.dp,
            bottom = 24.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item(key = "count") {
            Text(
                text = pluralStringResource(R.plurals.search_results_count, courses.size, courses.size),
                style = CourseGrabTextStyles.H1,
                color = CourseGrabColors.Black
            )
        }
        items(courses, key = { "${it.subjectCode} ${it.courseNum}" }) { course ->
            SearchResultCard(course = course, onClick = { onOpenCourse(course) })
        }
    }
}

@Composable
private fun SearchResultCard(course: SearchResult, onClick: () -> Unit, modifier: Modifier = Modifier) {
    CourseGrabCard(
        modifier = modifier
            .clip(CardShape)
            .clickable(onClick = onClick),
        contentPadding = PaddingValues(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    R.string.course_title_format,
                    course.subjectCode,
                    course.courseNum,
                    course.title
                ),
                style = CourseGrabTextStyles.H3,
                color = CourseGrabColors.Black,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(R.drawable.ic_cg_caret),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .rotate(180f)
            )
        }
    }
}

@Preview(widthDp = 402, heightDp = 874)
@Composable
private fun SearchScreenResultsPreview() {
    SearchContent(
        state = SearchState.Results(
            List(3) { sampleSearchResult(courseNum = 1400 + it, title = "Introduction to Sociocultural Anthropology") }
        ),
        onQueryChanged = {},
        onOpenCourse = {},
        onOpenSettings = {},
        onBack = {}
    )
}

@Preview(widthDp = 402, heightDp = 874)
@Composable
private fun SearchScreenEmptyPreview() {
    SearchContent(
        state = SearchState.Results(emptyList()),
        onQueryChanged = {},
        onOpenCourse = {},
        onOpenSettings = {},
        onBack = {}
    )
}
