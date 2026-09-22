package com.cornellappdev.coursegrab.networking

import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchContainer
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.models.TrackingContainer
import com.cornellappdev.coursegrab.models.UserSession
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CourseGrabService {

    @POST("session/initialize/")
    suspend fun initializeSession(
        @Body body: InitializeSessionRequest
    ): ApiResponse<UserSession>

    @POST("session/update/")
    suspend fun updateSession(
        @Header("Authorization") auth: String
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
data class InitializeSessionRequest(
    val token: String,
    @SerialName("device_type") val deviceType: String = "ANDROID",
    @SerialName("device_token") val deviceToken: String? = null
)

@Serializable
data class SearchRequest(val query: String)

@Serializable
data class CourseIdRequest(@SerialName("course_id") val courseId: Int)

@Serializable
data class DeviceTokenRequest(@SerialName("device_token") val deviceToken: String)

@Serializable
data class NotificationRequest(val notification: String)
