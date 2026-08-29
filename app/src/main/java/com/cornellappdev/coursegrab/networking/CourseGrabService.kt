package com.cornellappdev.coursegrab.networking

import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchContainer
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.models.TrackingContainer
import com.cornellappdev.coursegrab.models.UserSession
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * The Authorization header is per-endpoint rather than an interceptor: `initializeSession`
 * sends none, and `updateSession` sends the update token instead of the session token.
 */
interface CourseGrabService {

    @POST("session/initialize/")
    suspend fun initializeSession(
        @Body body: InitializeSessionRequest
    ): ApiResponse<UserSession>

    @POST("session/update/")
    suspend fun updateSession(
        @Header("Authorization") auth: String,
        @Body body: EmptyRequest = EmptyRequest()
    ): ApiResponse<UserSession>

    @GET("users/tracking/")
    suspend fun getTracking(
        @Header("Authorization") auth: String
    ): ApiResponse<TrackingContainer>

    @POST("courses/search/")
    suspend fun searchCourses(
        @Header("Authorization") auth: String,
        @Body body: SearchRequest
    ): ApiResponse<SearchContainer>

    @GET("courses/{courseId}/")
    suspend fun getCourseById(
        @Header("Authorization") auth: String,
        @Path("courseId") courseId: Int
    ): ApiResponse<SearchResult>

    @POST("sections/track/")
    suspend fun addTracking(
        @Header("Authorization") auth: String,
        @Body body: CourseIdRequest
    ): ApiResponse<Course>

    @POST("sections/untrack/")
    suspend fun removeTracking(
        @Header("Authorization") auth: String,
        @Body body: CourseIdRequest
    ): ApiResponse<Course>

    @POST("users/device-token/")
    suspend fun sendDeviceToken(
        @Header("Authorization") auth: String,
        @Body body: DeviceTokenRequest
    ): ApiResponse<Course>

    @POST("users/notification/")
    suspend fun setNotifications(
        @Header("Authorization") auth: String,
        @Body body: NotificationRequest
    ): ApiResponse<Course>
}

@Serializable
class EmptyRequest

@Serializable
data class InitializeSessionRequest(
    val token: String,
    val device_type: String = "ANDROID",
    val device_token: String? = null
)

@Serializable
data class SearchRequest(val query: String)

@Serializable
data class CourseIdRequest(val course_id: Int)

@Serializable
data class DeviceTokenRequest(val device_token: String)

@Serializable
data class NotificationRequest(val notification: String)
