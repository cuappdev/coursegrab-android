package com.cornellappdev.coursegrab.presenters

import android.content.Context
import com.cornellappdev.coursegrab.PreferencesHelper
import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.SearchContainer
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.networking.Endpoint
import com.cornellappdev.coursegrab.networking.Request
import com.cornellappdev.coursegrab.networking.searchCourses
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchPresenter(mContext: Context) {
    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(mContext)
    }

    suspend fun searchCourses(query: String): List<SearchResult> {
        val getTracking = Endpoint.searchCourses(preferencesHelper.sessionToken.toString(), query)

        val typeToken = object : TypeToken<ApiResponse<SearchContainer>>() {}.type

        return withContext(Dispatchers.IO) {
            Request.makeRequest<ApiResponse<SearchContainer>>(
                getTracking.okHttpRequest(),
                typeToken
            )
        }!!.data.courses
    }
}