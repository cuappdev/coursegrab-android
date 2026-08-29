package com.cornellappdev.coursegrab.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Course(
    val catalog_num: Int,
    val course_num: Int,
    val section: String,
    val instructors: List<String>,
    val is_tracking: Boolean,
    val status: String,
    val subject_code: String,
    val title: String,
    val num_tracking: Int,
    val mode: String,
    val errors: List<String>?
) : Parcelable {

    /** The backend reports section availability through [status] rather than a flag. */
    val isOpen: Boolean get() = status == STATUS_OPEN

    private companion object {
        const val STATUS_OPEN = "OPEN"
    }
}