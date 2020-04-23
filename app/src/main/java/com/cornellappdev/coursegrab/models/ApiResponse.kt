package com.cornellappdev.coursegrab.models

class ApiResponse<T>(val success: Boolean, val data: T, val timestamp: Long)
