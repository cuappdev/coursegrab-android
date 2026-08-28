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

/** Raised when the backend answers, but the answer isn't a usable payload. */
class ApiException(message: String) : Exception(message)

/**
 * Single entry point for the CourseGrab backend.
 *
 * Callers get a [Result] and never have to touch [Endpoint], Gson type tokens, or the IO
 * dispatcher. The session token is read here rather than at each call site, so screens no
 * longer need a [PreferencesHelper] just to make a request.
 */
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

    /**
     * Runs [endpoint] on the IO dispatcher and unwraps the [ApiResponse] envelope.
     *
     * The body is parsed even for non-2xx responses, since the backend reports failures
     * inside the envelope rather than through the status code.
     */
    private suspend fun <T : Any> call(endpoint: Endpoint, type: Type): Result<T> =
        withContext(Dispatchers.IO) {
            try {
                val httpResponse = Request.httpClient.newCall(endpoint.okHttpRequest()).await()
                val body = httpResponse.use { it.body.string() }

                val envelope: ApiResponse<T>? = try {
                    gson.fromJson<ApiResponse<T>>(body, type)
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
                        // `errors` only exists on Course payloads; other endpoints report
                        // failure through the flag alone.
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
