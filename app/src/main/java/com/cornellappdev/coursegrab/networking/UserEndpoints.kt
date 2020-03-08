package com.cornellappdev.coursegrab.networking

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

fun Endpoint.Companion.initializeSession(googleToken: String): Endpoint {
    val codeJSON = JSONObject()
    try {
        codeJSON.put("token", googleToken)
        codeJSON.put("is_ios", false)
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    val requestBody = codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(path = "/session/initialize/", body = requestBody, method = EndpointMethod.POST)
}

fun Endpoint.Companion.updateSession(updateToken: String): Endpoint {
    val codeJSON = JSONObject()
    val authHeaders = mapOf(Pair("Authorization", "Bearer $updateToken"))
    val requestBody = codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(path = "/session/update/", headers = authHeaders, body = requestBody, method = EndpointMethod.POST)
}

fun Endpoint.Companion.getTracking(accessToken: String): Endpoint {
    val codeJSON = JSONObject()
    val authHeaders = mapOf(Pair("Authorization", "Bearer $accessToken"))
    val requestBody = codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(path = "/users/tracking/", headers = authHeaders, body = requestBody, method = EndpointMethod.GET)
}

fun Endpoint.Companion.addTracking(accessToken: String, courseId: Int): Endpoint {
    val codeJSON = JSONObject()
    try {
        codeJSON.put("course_id", courseId)
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    val authHeaders = mapOf(Pair("Authorization", "Bearer $accessToken"))
    val requestBody = codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(path = "/sections/track/", headers = authHeaders, body = requestBody, method = EndpointMethod.POST)
}

fun Endpoint.Companion.removeTracking(accessToken: String, courseId: Int): Endpoint {
    val codeJSON = JSONObject()
    try {
        codeJSON.put("course_id", courseId)
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    val authHeaders = mapOf(Pair("Authorization", "Bearer $accessToken"))
    val requestBody = codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(path = "/sections/untrack/", headers = authHeaders, body = requestBody, method = EndpointMethod.POST)
}
