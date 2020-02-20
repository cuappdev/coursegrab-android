package com.cornellappdev.coursegrab.networking

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

fun Endpoint.Companion.initializeSession(googleToken: String): Endpoint {
    val codeJSON = JSONObject()
    try {
        codeJSON.put("token", googleToken)
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    val requestBody = codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    return Endpoint(path = "/initialize/session/", body = requestBody, method = EndpointMethod.POST)
}
