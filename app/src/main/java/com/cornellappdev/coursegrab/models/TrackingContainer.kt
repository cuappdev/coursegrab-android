package com.cornellappdev.coursegrab.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackingContainer(
    val sections: List<Course>
) : Parcelable