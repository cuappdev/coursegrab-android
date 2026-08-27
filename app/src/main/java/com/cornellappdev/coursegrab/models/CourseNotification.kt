package com.cornellappdev.coursegrab.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CourseNotification(
    val title: String,
    val body: String,
    val section: Course,
    val timestamp: Long
) : Parcelable