package com.cornellappdev.coursegrab.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserSession(
    @SerialName("session_token") val sessionToken: String? = null,
    @SerialName("update_token") val updateToken: String? = null,
    @SerialName("session_expiration") val sessionExpiration: Long? = null
)
