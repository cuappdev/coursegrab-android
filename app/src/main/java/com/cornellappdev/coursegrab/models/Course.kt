package com.cornellappdev.coursegrab.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Also carries failure payloads, where only [errors] is present — hence the defaults on
 * every other property.
 */
@Serializable
@Parcelize
data class Course(
    val catalog_num: Int = 0,
    val course_num: Int = 0,
    val section: String = "",
    val instructors: List<String> = emptyList(),
    val is_tracking: Boolean = false,
    val status: String = "",
    val subject_code: String = "",
    val title: String = "",
    val num_tracking: Int = 0,
    val mode: String = "",
    val errors: List<String>? = null
) : Parcelable {

    /** The backend reports section availability through [status] rather than a flag. */
    val isOpen: Boolean get() = status == STATUS_OPEN

    private companion object {
        const val STATUS_OPEN = "OPEN"
    }
}
