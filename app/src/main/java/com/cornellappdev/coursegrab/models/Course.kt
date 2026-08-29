package com.cornellappdev.coursegrab.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Course(
    @SerialName("catalog_num") val catalogNum: Int = 0,
    @SerialName("course_num") val courseNum: Int = 0,
    val section: String = "",
    val instructors: List<String> = emptyList(),
    @SerialName("is_tracking") val isTracking: Boolean = false,
    val status: String = "",
    @SerialName("subject_code") val subjectCode: String = "",
    val title: String = "",
    @SerialName("num_tracking") val numTracking: Int = 0,
    val mode: String = "",
    val errors: List<String>? = null
) : Parcelable {

    val isOpen: Boolean get() = status == STATUS_OPEN

    private companion object {
        const val STATUS_OPEN = "OPEN"
    }
}
