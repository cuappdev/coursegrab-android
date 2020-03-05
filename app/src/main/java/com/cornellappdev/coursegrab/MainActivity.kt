package com.cornellappdev.coursegrab

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.UserSession
import com.cornellappdev.coursegrab.networking.Endpoint
import com.cornellappdev.coursegrab.networking.Request
import com.cornellappdev.coursegrab.networking.getTracking
import com.cornellappdev.coursegrab.networking.initializeSession
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        var listOpen = mutableListOf<Course>()
        var listAwaiting = mutableListOf<Course>()

        val getTracking = Endpoint.getTracking(preferencesHelper.sessionToken.toString())

        CoroutineScope(Dispatchers.Main).launch {
            val typeToken = object : TypeToken<ApiResponse<List<Course>>>() {}.type
            val courseList = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<List<Course>>>(
                    getTracking.okHttpRequest(),
                    typeToken
                )
            }!!.data

            for (course in courseList){
                if (course.status == "OPEN")
                    listOpen.add(course)
                else
                    listAwaiting.add(course)
            }
        }

        // Available Courses Adapter
        availableViewManager = LinearLayoutManager(this)
        availableViewAdapter = AvailableAdapter(listOpen)

        availableRecyclerView = findViewById<RecyclerView>(R.id.available_list).apply {
            layoutManager = availableViewManager
            adapter = availableViewAdapter
        }

        available_title.text = "${available_list.adapter?.itemCount} Available"


        // Awaiting Courses Adapter
        awaitingViewManager = LinearLayoutManager(this)
        awaitingViewAdapter = AwaitingAdapter(listAwaiting)

        awaitingRecyclerView = findViewById<RecyclerView>(R.id.awaiting_list).apply {
            layoutManager = awaitingViewManager
            adapter = awaitingViewAdapter
        }

        awaiting_title.text = "${awaiting_list.adapter?.itemCount} Awaiting"

        layout_available.visibility = if (listOpen.isNotEmpty()) View.VISIBLE else View.GONE
        layout_awaiting.visibility = if (listAwaiting.isNotEmpty()) View.VISIBLE else View.GONE

        no_courses_view.visibility = if (listOpen.isEmpty() && listAwaiting.isEmpty()) View.VISIBLE else View.GONE

        settings_btn.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    class AvailableAdapter(private val availableCourses: List<Course>) :
        RecyclerView.Adapter<AvailableAdapter.ViewHolder>() {

        class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            val courseTitle: TextView
            val courseStatus: ImageView
            val courseTime: TextView
            val coursePin: TextView

            init {
                courseTitle = itemView.findViewById(R.id.course_title)
                courseStatus = itemView.findViewById(R.id.course_status)
                courseTime = itemView.findViewById(R.id.course_time)
                coursePin = itemView.findViewById(R.id.course_pin)
            }

            override fun onClick(view: View) {

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.course_available_list_item, parent, false) as View
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.courseTitle.text =
                "${availableCourses[position].subject_code} ${availableCourses[position].course_num}: ${availableCourses[position].title}"
            holder.courseTime.text = availableCourses[position].section
            holder.coursePin.text = availableCourses[position].catalog_num.toString()
            holder.courseStatus.setImageResource(if (availableCourses[position].status == "OPEN") R.drawable.ic_status_open else R.drawable.ic_status_closed)
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = availableCourses.size
    }

    class AwaitingAdapter(private val awaitingCourses: List<Course>) :
        RecyclerView.Adapter<AwaitingAdapter.ViewHolder>() {

        class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            val courseTitle: TextView
            val courseStatus: ImageView
            val courseTime: TextView
            val coursePin: TextView

            init {
                courseTitle = itemView.findViewById(R.id.course_title)
                courseStatus = itemView.findViewById(R.id.course_status)
                courseTime = itemView.findViewById(R.id.course_time)
                coursePin = itemView.findViewById(R.id.course_pin)
            }

            override fun onClick(view: View) {

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.course_awaiting_list_item, parent, false) as View
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.courseTitle.text =
                "${awaitingCourses[position].subject_code} ${awaitingCourses[position].course_num}: ${awaitingCourses[position].title}"
            holder.courseTime.text = awaitingCourses[position].section
            holder.coursePin.text = awaitingCourses[position].catalog_num.toString()
            holder.courseStatus.setImageResource(if (awaitingCourses[position].status == "OPEN") R.drawable.ic_status_open else R.drawable.ic_status_closed)
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = awaitingCourses.size
    }
}
