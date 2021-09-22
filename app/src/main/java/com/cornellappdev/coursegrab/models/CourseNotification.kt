package com.cornellappdev.coursegrab.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CourseNotification(
    val title: String,
    val body: String,
    val section: Course,
    val timestamp: Long
) : Parcelable