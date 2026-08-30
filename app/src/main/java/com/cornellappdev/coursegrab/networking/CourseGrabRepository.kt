package com.cornellappdev.coursegrab.networking

import com.cornellappdev.coursegrab.data.PreferencesHelper
import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.models.UserSession
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

class ApiException(message: String) : Exception(message)

@Singleton
class CourseGrabRepository @Inject constructor(
    private val service: CourseGrabService,
    private val preferencesHelper: PreferencesHelper
) {
    private val auth: String
        get() = "Bearer ${preferencesHelper.sessionToken.orEmpty()}"

    suspend fun initializeSession(googleToken: String, deviceToken: String?): Result<UserSession> =
        call {
            service.initializeSession(
                InitializeSessionRequest(
                    googleToken,
                    deviceToken = deviceToken
                )
            )
        }

    suspend fun updateSession(updateToken: String): Result<UserSession> =
        call { service.updateSession("Bearer $updateToken") }

    suspend fun getTracking(): Result<List<Course>> =
        call { service.getTracking(auth) }.map { it.sections }

    suspend fun searchCourses(query: String): Result<List<SearchResult>> =
        call { service.searchCourses(auth, SearchRequest(query)) }.map { it.courses }

    suspend fun addTracking(courseId: Int): Result<Course> =
        call { service.addTracking(auth, CourseIdRequest(courseId)) }

    suspend fun removeTracking(courseId: Int): Result<Course> =
        call { service.removeTracking(auth, CourseIdRequest(courseId)) }

    suspend fun getCourseById(courseId: Int): Result<SearchResult> =
        call { service.getCourseById(auth, courseId) }

    suspend fun sendDeviceToken(deviceToken: String): Result<Course> =
        call { service.sendDeviceToken(auth, DeviceTokenRequest(deviceToken)) }

    suspend fun setNotifications(enabled: Boolean): Result<Course> =
        call {
            service.setNotifications(
                auth,
                NotificationRequest(if (enabled) "ANDROID" else "NONE")
            )
        }

    private suspend fun <T : Any> call(request: suspend () -> ApiResponse<T>): Result<T> =
        try {
            val envelope = request()
            if (envelope.success) {
                Result.success(envelope.data)
            } else {
                Result.failure(
                    ApiException(
                        (envelope.data as? Course)?.errors?.firstOrNull() ?: "Request failed"
                    )
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
