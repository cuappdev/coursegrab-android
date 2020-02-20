package com.cornellappdev.coursegrab.networking

import com.cornellappdev.coursegrab.BuildConfig
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Request
import okhttp3.RequestBody

enum class EndpointMethod {
    GET, POST, DELETE, PUT
}

class Endpoint(private val path: String, private val headers: Map<String, String> = mapOf(), private val body: RequestBody? = null, private val method: EndpointMethod) {

    private val host = "https://" + BuildConfig.BACKEND_URI + "/api/"

    companion object

    fun okHttpRequest(): Request {
        val endpoint = host + path
        val headers = headers.toHeaders()

        when (method) {
            EndpointMethod.POST -> {
                return Request.Builder()
                    .url(endpoint)
                    .post(body.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()))
                    .headers(headers)
                    .build()
            }
            EndpointMethod.GET -> {
                return Request.Builder()
                    .url(endpoint)
                    .headers(headers)
                    .get()
                    .build()
            }


            else -> {
                throw IllegalArgumentException("NOT IMPLEMENTED")
            }
        }
    }

}