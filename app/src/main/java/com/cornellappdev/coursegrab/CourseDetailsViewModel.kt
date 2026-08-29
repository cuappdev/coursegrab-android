package com.cornellappdev.coursegrab

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.coursegrab.networking.CourseGrabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseDetailsViewModel @Inject constructor(
    private val repository: CourseGrabRepository
) : ViewModel() {

    private val _trackingErrors = Channel<String>(Channel.BUFFERED)
    val trackingErrors = _trackingErrors.receiveAsFlow()

    fun addCourse(courseId: Int) = track(courseId, adding = true)

    fun removeCourse(courseId: Int) = track(courseId, adding = false)

    private fun track(courseId: Int, adding: Boolean) {
        viewModelScope.launch {
            val result = if (adding) repository.addTracking(courseId)
            else repository.removeTracking(courseId)

            result.onFailure { error ->
                Log.e(TAG, "Failed to ${if (adding) "track" else "untrack"} $courseId", error)
                _trackingErrors.send(
                    error.message ?: if (adding) "Couldn't track that course."
                    else "Couldn't remove that course."
                )
            }
        }
    }

    private companion object {
        const val TAG = "CourseDetailsViewModel"
    }
}
