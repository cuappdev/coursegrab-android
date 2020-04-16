package com.cornellappdev.coursegrab.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchResult(
    val subject_code: String,
    val course_num: Int,
    val title: String,
    val sections: List<Section>
) : Parcelable