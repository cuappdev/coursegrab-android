package com.cornellappdev.coursegrab

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.coursegrab.fragments.TrackingFragment
import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.TrackingContainer
import com.cornellappdev.coursegrab.networking.Endpoint
import com.cornellappdev.coursegrab.networking.Request
import com.cornellappdev.coursegrab.networking.getTracking
import com.cornellappdev.coursegrab.networking.removeTracking
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//TODO: this is temp - will remove when the MainActivity is refactored
class MainPresenter(
    val context: Context
) {
    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(context)
    }

    fun removeCourse(courseId: Int, _fragment: TrackingFragment) {
        val removeTracking =
            Endpoint.removeTracking(preferencesHelper.sessionToken.toString(), courseId)

        CoroutineScope(Dispatchers.Main).launch {
            val typeToken = object : TypeToken<ApiResponse<Course>>() {}.type
            val response = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<Course>>(
                    removeTracking.okHttpRequest(),
                    typeToken
                )
            }

            _fragment.refreshAwaiting()

            if (!response!!.success)
                Toast.makeText(
                    context,
                    response.data.errors!![0],
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    fun enrollCourse(courseId: Int) {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("http://studentcenter.cornell.edu"))
        startActivity(context, browserIntent, null)
    }

    // Get list of open and awaiting courses
    suspend fun refreshCourseList() : Map<String, List<Course>> {
        val listOpen = mutableListOf<Course>()
        val listAwaiting = mutableListOf<Course>()

        val getTracking = Endpoint.getTracking(preferencesHelper.sessionToken.toString())

            val typeToken = object : TypeToken<ApiResponse<TrackingContainer>>() {}.type
            Repository.courseList = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<TrackingContainer>>(
                    getTracking.okHttpRequest(),
                    typeToken
                )
            }!!.data.sections

            for (course in Repository.courseList) {
                if (course.status == "OPEN")
                    listOpen.add(course)
                else
                    listAwaiting.add(course)
            }

            return mapOf(
                "open" to listOpen,
                "awaiting" to listAwaiting
            )
    }
}