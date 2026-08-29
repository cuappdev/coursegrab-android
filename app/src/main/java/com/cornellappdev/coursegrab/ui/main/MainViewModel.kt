package com.cornellappdev.coursegrab.ui.main

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

data class TrackedCoursesState(
    val available: List<Course> = emptyList(),
    val awaiting: List<Course> = emptyList(),
    val isRefreshing: Boolean = false,
    val hasLoaded: Boolean = false
)

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
        _state.value = _state.value.copy(isRefreshing = true)
        viewModelScope.launch {
            repository.getTracking()
                .onSuccess { courses ->
                    _state.value = TrackedCoursesState(
                        available = courses.filter { it.isOpen },
                        awaiting = courses.filterNot { it.isOpen },
                        isRefreshing = false,
                        hasLoaded = true
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(isRefreshing = false)
                    _effects.send(MainEffect.Message("Couldn't load your courses."))
                }
        }
    }

    fun addCourse(courseId: Int) = track(courseId, adding = true)

    fun removeCourse(courseId: Int) = track(courseId, adding = false)

    private fun track(courseId: Int, adding: Boolean) {
        viewModelScope.launch {
            val result =
                if (adding) repository.addTracking(courseId)
                else repository.removeTracking(courseId)

            result.onFailure { error ->
                _effects.send(
                    MainEffect.Message(
                        error.message
                            ?: if (adding) "Couldn't track that course."
                            else "Couldn't remove that course."
                    )
                )
            }

            refresh()
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
