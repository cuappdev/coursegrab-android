package com.cornellappdev.coursegrab.models

import kotlinx.serialization.Serializable

@Serializable
class UserSession(
    val session_token: String? = null,
    val update_token: String? = null,
    val session_expiration: Long? = null
)
