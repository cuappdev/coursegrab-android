package com.cornellappdev.coursegrab

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.TrackingContainer
import com.cornellappdev.coursegrab.networking.*
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {
    private lateinit var availableRecyclerView: RecyclerView
    private lateinit var availableViewAdapter: RecyclerView.Adapter<*>
    private lateinit var availableViewManager: RecyclerView.LayoutManager

    private lateinit var awaitingRecyclerView: RecyclerView
    private lateinit var awaitingViewAdapter: RecyclerView.Adapter<*>
    private lateinit var awaitingViewManager: RecyclerView.LayoutManager

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refreshAwaiting()

        refresh_courses_layout.setOnRefreshListener {
            refreshAwaiting()
        }

        settings_btn.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }

        search_btn.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        addCourseButton.setOnClickListener {
            addCourse(addCourseEditText.text.toString().toInt(), this)
            addCourseEditText.clearFocus()
            addCourseEditText.text.clear()
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }

        addCourseEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCourseButton.isEnabled = (s!!.length > 3)
            }
        })

        addCourseEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                addCourse(addCourseEditText.text.toString().toInt(), this)
                addCourseEditText.clearFocus()
                addCourseEditText.text.clear()
                val inputMethodManager =
                    getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                return@OnKeyListener true
            }
            false
        })
    }

    override fun onResume() {
        super.onResume()

        refreshAwaiting()
    }

    override fun onBackPressed() {}

    private fun refreshAwaiting() {
        val listOpen = mutableListOf<Course>()
        val listAwaiting = mutableListOf<Course>()

        val getTracking = Endpoint.getTracking(preferencesHelper.sessionToken.toString())

        CoroutineScope(Dispatchers.Main).launch {
            val typeToken = object : TypeToken<ApiResponse<TrackingContainer>>() {}.type
            val courseList = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<TrackingContainer>>(
                    getTracking.okHttpRequest(),
                    typeToken
                )
            }!!.data.sections

            for (course in courseList) {
                if (course.status == "OPEN")
                    listOpen.add(course)
                else
                    listAwaiting.add(course)
            }

            // Available Courses Adapter
            availableViewManager = LinearLayoutManager(this@MainActivity)
            availableViewAdapter = TrackingAdapter(listOpen, this@MainActivity, false)

            availableRecyclerView = available_list.apply {
                layoutManager = availableViewManager
                adapter = availableViewAdapter
            }
            available_title.text = "${available_list.adapter?.itemCount} Available"

            // Awaiting Courses Adapter
            awaitingViewManager = LinearLayoutManager(this@MainActivity)
            awaitingViewAdapter = TrackingAdapter(listAwaiting, this@MainActivity, true)

            awaitingRecyclerView = awaiting_list.apply {
                layoutManager = awaitingViewManager
                adapter = awaitingViewAdapter
            }
            awaiting_title.text = "${awaiting_list.adapter?.itemCount} Awaiting"

            layout_available.visibility = if (listOpen.isNotEmpty()) View.VISIBLE else View.GONE
            layout_awaiting.visibility = if (listAwaiting.isNotEmpty()) View.VISIBLE else View.GONE

            no_courses_view.visibility =
                if (listOpen.isEmpty() && listAwaiting.isEmpty()) View.VISIBLE else View.GONE

            refresh_courses_layout.isRefreshing = false
        }
    }

    private fun addCourse(courseId: Int, context: Context) {
        val addTracking = Endpoint.addTracking(preferencesHelper.sessionToken.toString(), courseId)

        CoroutineScope(Dispatchers.Main).launch {
            val typeToken = object : TypeToken<ApiResponse<Course>>() {}.type
            val response = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<Course>>(
                    addTracking.okHttpRequest(),
                    typeToken
                )
            }

            refreshAwaiting()

            if (!response!!.success)
                Toast.makeText(
                    context,
                    response.data.errors!![0],
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    private fun removeCourse(courseId: Int, context: Context) {
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

            refreshAwaiting()

            if (!response!!.success)
                Toast.makeText(
                    context,
                    response.data.errors!![0],
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    private fun enrollCourse(courseId: Int, context: Context) {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("http://studentcenter.cornell.edu"))
        startActivity(browserIntent)
    }
}
