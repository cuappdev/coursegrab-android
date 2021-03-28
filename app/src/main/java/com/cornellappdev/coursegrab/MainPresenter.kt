package com.cornellappdev.coursegrab

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    fun removeCourse(courseId: Int) {
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

            //TODO: migrate refresh
//            refreshAwaiting()

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
}