package com.cornellappdev.coursegrab.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

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
) : Parcelable