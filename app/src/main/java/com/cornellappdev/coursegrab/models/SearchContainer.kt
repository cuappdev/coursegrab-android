package com.cornellappdev.coursegrab.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchContainer(
    val courses: List<SearchResult>,
    val query: String
) : Parcelable