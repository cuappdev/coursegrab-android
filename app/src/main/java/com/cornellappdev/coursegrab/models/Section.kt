package com.cornellappdev.coursegrab.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Section(
    val catalog_num: Int,
    val course_num: Int,
    val section: String,
    val status: String,
    val subject_code: String,
    val title: String,
    val instructors: List<String>,
    val errors: List<String>?
) : Parcelable