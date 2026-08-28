package com.cornellappdev.coursegrab.networking

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object Request {
    val httpClient = OkHttpClient()
}

/**
 * Suspend extension that allows suspend [Call] inside coroutine.
 *
 * @return Result of request or throw exception
 */
suspend fun Call.await(recordStackTrace: Boolean = true): Response {
    val recordStackTrace =
        if (recordStackTrace) IOException("Exception occurred while awaiting Call.") else null
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : okhttp3.Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }

            override fun onFailure(call: Call, e: IOException) {
                if (recordStackTrace != null) {
                    recordStackTrace.initCause(e)
                    continuation.resumeWithException(recordStackTrace)
                } else {
                    continuation.resumeWithException(e)
                }
            }
        })

        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (_: Throwable) {
            }
        }
    }
}
