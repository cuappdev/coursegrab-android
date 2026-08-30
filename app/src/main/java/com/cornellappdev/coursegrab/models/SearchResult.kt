package com.cornellappdev.coursegrab.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class SearchResult(
    @SerialName("subject_code") val subjectCode: String,
    @SerialName("course_num") val courseNum: Int,
    val title: String,
    val sections: List<Course>
) : Parcelable