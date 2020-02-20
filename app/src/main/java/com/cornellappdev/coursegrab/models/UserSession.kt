package com.cornellappdev.coursegrab.models

class UserSession(
    val session_token: String,
    val update_token: String,
    val session_expiration: String,
    val isActive: Boolean
)