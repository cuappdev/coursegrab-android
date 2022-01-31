package com.cornellappdev.coursegrab.networking

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

fun Endpoint.Companion.initializeSession(googleToken: String, deviceToken: String?): Endpoint {
    val codeJSON = JSONObject()
    try {
        codeJSON.put("token", googleToken)
        codeJSON.put("device_type", "ANDROID")
        codeJSON.put("device_token", deviceToken)
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    val requestBody =
        codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(path = "/session/initialize/", body = requestBody, method = EndpointMethod.POST)
}

fun Endpoint.Companion.updateSession(updateToken: String): Endpoint {
    val codeJSON = JSONObject()
    val authHeaders = mapOf(Pair("Authorization", "Bearer $updateToken"))
    val requestBody =
        codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(
        path = "/session/update/",
        headers = authHeaders,
        body = requestBody,
        method = EndpointMethod.POST
    )
}

fun Endpoint.Companion.getTracking(accessToken: String): Endpoint {
    val codeJSON = JSONObject()
    val authHeaders = mapOf(Pair("Authorization", "Bearer $accessToken"))
    val requestBody =
        codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(
        path = "/users/tracking/",
        headers = authHeaders,
        body = requestBody,
        method = EndpointMethod.GET
    )
}

fun Endpoint.Companion.searchCourses(accessToken: String, query: String): Endpoint {
    val codeJSON = JSONObject()
    try {
        codeJSON.put("query", query)
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    val authHeaders = mapOf(Pair("Authorization", "Bearer $accessToken"))
    val requestBody =
        codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(
        path = "/courses/search/",
        headers = authHeaders,
        body = requestBody,
        method = EndpointMethod.POST
    )
}

fun Endpoint.Companion.addTracking(accessToken: String, courseId: Int): Endpoint {
    val codeJSON = JSONObject()
    try {
        codeJSON.put("course_id", courseId)
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    val authHeaders = mapOf(Pair("Authorization", "Bearer $accessToken"))
    val requestBody =
        codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(
        path = "/sections/track/",
        headers = authHeaders,
        body = requestBody,
        method = EndpointMethod.POST
    )
}

fun Endpoint.Companion.removeTracking(accessToken: String, courseId: Int): Endpoint {
    val codeJSON = JSONObject()
    try {
        codeJSON.put("course_id", courseId)
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    val authHeaders = mapOf(Pair("Authorization", "Bearer $accessToken"))
    val requestBody =
        codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(
        path = "/sections/untrack/",
        headers = authHeaders,
        body = requestBody,
        method = EndpointMethod.POST
    )
}

fun Endpoint.Companion.deviceToken(accessToken: String, deviceToken: String): Endpoint {
    val codeJSON = JSONObject()
    try {
        codeJSON.put("device_token", deviceToken)
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    val authHeaders = mapOf(Pair("Authorization", "Bearer $accessToken"))
    val requestBody =
        codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(
        path = "/users/device-token/",
        headers = authHeaders,
        body = requestBody,
        method = EndpointMethod.POST
    )
}

fun Endpoint.Companion.setNotification(accessToken: String, notifSetting: String): Endpoint {
    val codeJSON = JSONObject()
    try {
        codeJSON.put("notification", notifSetting)
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    val authHeaders = mapOf(Pair("Authorization", "Bearer $accessToken"))
    val requestBody =
        codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(
        path = "/users/notification/",
        headers = authHeaders,
        body = requestBody,
        method = EndpointMethod.POST
    )
}

fun Endpoint.Companion.getCourseByID(accessToken: String, courseId: Int): Endpoint {
    val codeJSON = JSONObject()
    val authHeaders = mapOf(Pair("Authorization", "Bearer $accessToken"))
    val requestBody =
        codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(
        path = "/courses/1240/",
        headers = authHeaders,
        body = requestBody,
        method = EndpointMethod.GET
    )
}
