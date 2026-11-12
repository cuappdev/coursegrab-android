package com.cornellappdev.coursegrab.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.cornellappdev.coursegrab.models.SearchResult
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Search : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data class CourseDetails(val course: SearchResult) : Route {
        companion object {
            val typeMap = mapOf(typeOf<SearchResult>() to serializableNavType<SearchResult>())
        }
    }
}

private inline fun <reified T : Any> serializableNavType(): NavType<T> =
    object : NavType<T>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): T? =
            bundle.getString(key)?.let { Json.decodeFromString<T>(it) }

        override fun put(bundle: Bundle, key: String, value: T) =
            bundle.putString(key, Json.encodeToString(value))

        override fun parseValue(value: String): T = Json.decodeFromString(value)

        override fun serializeAsValue(value: T): String =
            Uri.encode(Json.encodeToString(value))
    }
