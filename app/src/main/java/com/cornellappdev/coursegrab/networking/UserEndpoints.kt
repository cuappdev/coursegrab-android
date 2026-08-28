package com.cornellappdev.coursegrab.networking

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

/** Builds a JSON request body. A null value omits the field, per [JSONObject.put]. */
private fun jsonBody(vararg fields: Pair<String, Any?>): RequestBody =
    JSONObject().apply { fields.forEach { (key, value) -> put(key, value) } }
        .toString()
        .toRequestBody(JSON)

private fun bearer(token: String): Map<String, String> =
    mapOf("Authorization" to "Bearer $token")

fun Endpoint.Companion.initializeSession(googleToken: String, deviceToken: String?): Endpoint =
    Endpoint(
        path = "/session/initialize/",
        body = jsonBody(
            "token" to googleToken,
            "device_type" to "ANDROID",
            "device_token" to deviceToken
        ),
        method = EndpointMethod.POST
    )

fun Endpoint.Companion.updateSession(updateToken: String): Endpoint =
    Endpoint(
        path = "/session/update/",
        headers = bearer(updateToken),
        body = jsonBody(),
        method = EndpointMethod.POST
    )

fun Endpoint.Companion.getTracking(accessToken: String): Endpoint =
    Endpoint(
        path = "/users/tracking/",
        headers = bearer(accessToken),
        method = EndpointMethod.GET
    )

fun Endpoint.Companion.searchCourses(accessToken: String, query: String): Endpoint =
    Endpoint(
        path = "/courses/search/",
        headers = bearer(accessToken),
        body = jsonBody("query" to query),
        method = EndpointMethod.POST
    )

fun Endpoint.Companion.addTracking(accessToken: String, courseId: Int): Endpoint =
    Endpoint(
        path = "/sections/track/",
        headers = bearer(accessToken),
        body = jsonBody("course_id" to courseId),
        method = EndpointMethod.POST
    )

fun Endpoint.Companion.removeTracking(accessToken: String, courseId: Int): Endpoint =
    Endpoint(
        path = "/sections/untrack/",
        headers = bearer(accessToken),
        body = jsonBody("course_id" to courseId),
        method = EndpointMethod.POST
    )

fun Endpoint.Companion.deviceToken(accessToken: String, deviceToken: String): Endpoint =
    Endpoint(
        path = "/users/device-token/",
        headers = bearer(accessToken),
        body = jsonBody("device_token" to deviceToken),
        method = EndpointMethod.POST
    )

fun Endpoint.Companion.setNotification(accessToken: String, notifSetting: String): Endpoint =
    Endpoint(
        path = "/users/notification/",
        headers = bearer(accessToken),
        body = jsonBody("notification" to notifSetting),
        method = EndpointMethod.POST
    )

fun Endpoint.Companion.getCourseByID(accessToken: String, courseId: Int): Endpoint =
    Endpoint(
        path = "/courses/$courseId/",
        headers = bearer(accessToken),
        method = EndpointMethod.GET
    )
