package com.cornellappdev.coursegrab.ui.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.networking.CourseGrabRepository
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
    val instructor: String = "",
    val sections: List<Course> = emptyList()
)

@HiltViewModel
class CourseDetailsViewModel @Inject constructor(
    private val repository: CourseGrabRepository
) : ViewModel() {

    private val _trackingErrors = Channel<String>(Channel.BUFFERED)
    val trackingErrors = _trackingErrors.receiveAsFlow()

    private val _state = MutableStateFlow(CourseDetailsState())
    val state: StateFlow<CourseDetailsState> = _state.asStateFlow()

    private var seeded = false

    fun seed(course: SearchResult) {
        if (seeded) return
        seeded = true
        _state.value = CourseDetailsState(
            subjectCode = course.subjectCode,
            courseNum = course.courseNum,
            title = course.title,
            instructor = course.sections.firstOrNull()?.instructors?.firstOrNull()
                ?: UNASSIGNED_INSTRUCTOR,
            sections = course.sections
        )
    }

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
                _state.value = _state.value.copy(sections = before)
                _trackingErrors.send(
                    error.message ?: if (tracking) "Couldn't track that course."
                    else "Couldn't remove that course."
                )
            }
        }
    }

    private fun updateSection(catalogNum: Int, transform: (Course) -> Course) {
        _state.value = _state.value.copy(
            sections = _state.value.sections.map { section ->
                if (section.catalogNum == catalogNum) transform(section) else section
            }
        )
    }

    private companion object {
        const val TAG = "CourseDetailsViewModel"
        const val UNASSIGNED_INSTRUCTOR = "To Be Assigned"
    }
}
