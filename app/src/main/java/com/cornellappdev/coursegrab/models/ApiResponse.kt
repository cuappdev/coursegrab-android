package com.cornellappdev.coursegrab.models

import kotlinx.serialization.Serializable

@Serializable
class ApiResponse<T>(val success: Boolean, val data: T, val timestamp: Long = 0L)
