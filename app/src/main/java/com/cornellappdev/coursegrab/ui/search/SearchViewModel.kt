package com.cornellappdev.coursegrab.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.networking.CourseGrabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SearchState {
    data object QueryTooShort : SearchState
    data class Results(val courses: List<SearchResult>) : SearchState
    data object Failed : SearchState
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CourseGrabRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.QueryTooShort)
    val state: StateFlow<SearchState> = _state.asStateFlow()
    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        searchJob?.cancel()

        if (query.length <= MIN_QUERY_LENGTH) {
            _state.value = SearchState.QueryTooShort
            return
        }

        searchJob = viewModelScope.launch {
            repository.searchCourses(query).onSuccess { _state.value = SearchState.Results(it) }
                .onFailure { error ->
                    Log.e(TAG, "Search failed for query \"$query\"", error)
                    _state.value = SearchState.Failed
                }
        }
    }

    private companion object {
        const val TAG = "SearchViewModel"
        const val MIN_QUERY_LENGTH = 2
    }
}
