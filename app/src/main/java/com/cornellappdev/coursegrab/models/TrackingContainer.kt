package com.cornellappdev.coursegrab.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrackingContainer(
    val sections: List<Course>
) : Parcelable