package com.cornellappdev.coursegrab.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class SearchResult(
    val subject_code: String,
    val course_num: Int,
    val title: String,
    val sections: List<Course>
) : Parcelable