package com.cornellappdev.coursegrab.ui.details

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.networking.CourseGrabRepository
import com.cornellappdev.coursegrab.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseDetailsState(
    val subjectCode: String = "",
    val courseNum: Int = 0,
    val title: String = "",
    val sections: List<Course> = emptyList(),
    val hasTrackingChanges: Boolean = false
) {
    val code: String get() = "$subjectCode $courseNum"
}

data class SectionGroup(val type: String, val sections: List<Course>)

data class SectionLayout(val primary: Course?, val groups: List<SectionGroup>)

fun layoutSections(sections: List<Course>): SectionLayout {
    val groups = sections
        .groupBy { it.section.substringBefore(' ') }
        .map { (type, members) -> SectionGroup(type, members) }
    val first = groups.firstOrNull()
    return if (first != null && first.sections.size == 1) {
        SectionLayout(primary = first.sections.single(), groups = groups.drop(1))
    } else {
        SectionLayout(primary = null, groups = groups)
    }
}

data class TrackingError(val message: String?, @StringRes val fallback: Int)

@HiltViewModel
class CourseDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CourseGrabRepository
) : ViewModel() {

    private val _trackingErrors = Channel<TrackingError>(Channel.BUFFERED)
    val trackingErrors = _trackingErrors.receiveAsFlow()

    private val course: SearchResult =
        savedStateHandle.toRoute<Route.CourseDetails>(Route.CourseDetails.typeMap).course

    private val _state = MutableStateFlow(
        CourseDetailsState(
            subjectCode = course.subjectCode,
            courseNum = course.courseNum,
            title = course.title,
            sections = course.sections
        )
    )
    val state: StateFlow<CourseDetailsState> = _state.asStateFlow()

    private val initialTracking = course.sections.associate { it.catalogNum to it.isTracking }

    fun setTracking(catalogNum: Int, tracking: Boolean) {
        val before = _state.value.sections
        updateSection(catalogNum) { it.copy(isTracking = tracking) }

        viewModelScope.launch {
            val result =
                if (tracking) repository.addTracking(catalogNum)
                else repository.removeTracking(catalogNum)

            result.onFailure { error ->
                Log.e(
                    TAG,
                    "Failed to ${if (tracking) "track" else "untrack"} $catalogNum",
                    error
                )
                _state.value = _state.value.copy(
                    sections = before,
                    hasTrackingChanges = before.any { it.isTracking != initialTracking[it.catalogNum] }
                )
                _trackingErrors.send(
                    TrackingError(
                        message = error.message,
                        fallback = if (tracking) R.string.track_course_failed
                        else R.string.untrack_course_failed
                    )
                )
            }
        }
    }

    private fun updateSection(catalogNum: Int, transform: (Course) -> Course) {
        val sections = _state.value.sections.map { section ->
            if (section.catalogNum == catalogNum) transform(section) else section
        }
        _state.value = _state.value.copy(
            sections = sections,
            hasTrackingChanges = sections.any { it.isTracking != initialTracking[it.catalogNum] }
        )
    }

    private companion object {
        const val TAG = "CourseDetailsViewModel"
    }
}
