package com.cornellappdev.coursegrab.networking

import com.cornellappdev.coursegrab.PreferencesHelper
import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchContainer
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.models.TrackingContainer
import com.cornellappdev.coursegrab.models.UserSession
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type

class ApiException(message: String) : Exception(message)

class CourseGrabRepository(private val preferencesHelper: PreferencesHelper) {

    private val gson = Gson()

    private val token: String
        get() = preferencesHelper.sessionToken.orEmpty()

    suspend fun initializeSession(googleToken: String, deviceToken: String?): Result<UserSession> =
        call(Endpoint.initializeSession(googleToken, deviceToken), userSessionType)

    suspend fun updateSession(updateToken: String): Result<UserSession> =
        call(Endpoint.updateSession(updateToken), userSessionType)

    suspend fun getTracking(): Result<List<Course>> =
        call<TrackingContainer>(Endpoint.getTracking(token), trackingType).map { it.sections }

    suspend fun searchCourses(query: String): Result<List<SearchResult>> =
        call<SearchContainer>(Endpoint.searchCourses(token, query), searchType).map { it.courses }

    suspend fun addTracking(courseId: Int): Result<Course> =
        call(Endpoint.addTracking(token, courseId), courseType)

    suspend fun removeTracking(courseId: Int): Result<Course> =
        call(Endpoint.removeTracking(token, courseId), courseType)

    suspend fun getCourseById(courseId: Int): Result<SearchResult> =
        call(Endpoint.getCourseByID(token, courseId), searchResultType)

    suspend fun sendDeviceToken(deviceToken: String): Result<Course> =
        call(Endpoint.deviceToken(token, deviceToken), courseType)

    suspend fun setNotifications(enabled: Boolean): Result<Course> =
        call(Endpoint.setNotification(token, if (enabled) "ANDROID" else "NONE"), courseType)

    private suspend fun <T : Any> call(endpoint: Endpoint, type: Type): Result<T> =
        withContext(Dispatchers.IO) {
            try {
                val httpResponse = Request.httpClient.newCall(endpoint.okHttpRequest()).await()
                val body = httpResponse.use { it.body.string() }

                val envelope: ApiResponse<T>? = try {
                    gson.fromJson(body, type)
                } catch (_: JsonParseException) {
                    null
                }

                val data = envelope?.data
                when {
                    envelope == null || data == null -> Result.failure(
                        ApiException(
                            if (httpResponse.isSuccessful) "Unreadable response from server"
                            else "Server error (${httpResponse.code})"
                        )
                    )

                    !envelope.success -> Result.failure(
                        ApiException((data as? Course)?.errors?.firstOrNull() ?: "Request failed")
                    )

                    else -> Result.success(data)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private companion object {
        val userSessionType: Type = object : TypeToken<ApiResponse<UserSession>>() {}.type
        val trackingType: Type = object : TypeToken<ApiResponse<TrackingContainer>>() {}.type
        val searchType: Type = object : TypeToken<ApiResponse<SearchContainer>>() {}.type
        val searchResultType: Type = object : TypeToken<ApiResponse<SearchResult>>() {}.type
        val courseType: Type = object : TypeToken<ApiResponse<Course>>() {}.type
    }
}
