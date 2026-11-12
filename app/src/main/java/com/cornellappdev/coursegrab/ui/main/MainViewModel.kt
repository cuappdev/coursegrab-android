package com.cornellappdev.coursegrab.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.models.SectionStatus
import com.cornellappdev.coursegrab.networking.CourseGrabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

data class TrackedCoursesState(
    val courses: List<Course> = emptyList(),
    val isRefreshing: Boolean = false,
    val hasLoaded: Boolean = false,
    val isOffline: Boolean = false
) {
    fun count(status: SectionStatus): Int = courses.count { it.sectionStatus == status }
}

sealed interface MainEffect {
    data class Message(val text: String) : MainEffect
    data class OpenCourse(val course: SearchResult) : MainEffect
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CourseGrabRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TrackedCoursesState())
    val state: StateFlow<TrackedCoursesState> = _state.asStateFlow()

    private val _effects = Channel<MainEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        refresh()
    }

    fun refresh() {
        if (_state.value.isRefreshing) return
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            repository.getTracking()
                .onSuccess { courses ->
                    _state.value = TrackedCoursesState(
                        courses = courses.sortedBy { it.sectionStatus.ordinal },
                        isRefreshing = false,
                        hasLoaded = true
                    )
                }
                .onFailure { error ->
                    val offline = error is IOException && !_state.value.hasLoaded
                    _state.update { it.copy(isRefreshing = false, isOffline = offline) }
                    if (!offline) _effects.send(MainEffect.Message("Couldn't load your courses."))
                }
        }
    }

    fun removeCourse(catalogNum: Int) {
        viewModelScope.launch {
            repository.removeTracking(catalogNum).onFailure { error ->
                _effects.send(MainEffect.Message(error.message ?: "Couldn't remove that course."))
            }
            refresh()
        }
    }

    fun reportNotificationsBlocked() {
        viewModelScope.launch {
            _effects.send(
                MainEffect.Message(
                    "Enable notifications in settings to receive course updates."
                )
            )
        }
    }

    fun openCourse(courseId: Int) {
        viewModelScope.launch {
            repository.getCourseById(courseId)
                .onSuccess { _effects.send(MainEffect.OpenCourse(it)) }
                .onFailure { _effects.send(MainEffect.Message("Couldn't open that course.")) }
        }
    }
}
