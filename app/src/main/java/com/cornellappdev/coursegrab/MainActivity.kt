package com.cornellappdev.coursegrab

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.cornellappdev.coursegrab.networking.*
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

        refreshAwaiting()

        settings_btn.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }

        search_btn.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        addCourseButton.setOnClickListener {
            addCourse(addCourseEditText.text.toString().toInt())
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
                addCourse(addCourseEditText.text.toString().toInt())
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

    private fun refreshAwaiting() {
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

            for (course in courseList) {
                if (course.status == "OPEN")
                    listOpen.add(course)
                else
                    listAwaiting.add(course)
            }

            // Available Courses Adapter
            availableViewManager = LinearLayoutManager(this@MainActivity)
            availableViewAdapter = AvailableAdapter(listOpen, this@MainActivity)

            availableRecyclerView = findViewById<RecyclerView>(R.id.available_list).apply {
                layoutManager = availableViewManager
                adapter = availableViewAdapter
            }
            available_title.text = "${available_list.adapter?.itemCount} Available"

            // Awaiting Courses Adapter
            awaitingViewManager = LinearLayoutManager(this@MainActivity)
            awaitingViewAdapter = AwaitingAdapter(listAwaiting, this@MainActivity)

            awaitingRecyclerView = findViewById<RecyclerView>(R.id.awaiting_list).apply {
                layoutManager = awaitingViewManager
                adapter = awaitingViewAdapter
            }
            awaiting_title.text = "${awaiting_list.adapter?.itemCount} Awaiting"

            layout_available.visibility = if (listOpen.isNotEmpty()) View.VISIBLE else View.GONE
            layout_awaiting.visibility = if (listAwaiting.isNotEmpty()) View.VISIBLE else View.GONE

            no_courses_view.visibility =
                if (listOpen.isEmpty() && listAwaiting.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun addCourse(courseId: Int) {
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
                    this@MainActivity,
                    response.data.errors!![0],
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    private fun removeCourse(courseId: Int) {
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
                    this@MainActivity,
                    response.data.errors!![0],
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    class AvailableAdapter(
        private val availableCourses: List<Course>,
        private val context: Context
    ) :
        RecyclerView.Adapter<AvailableAdapter.ViewHolder>() {

        class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            val courseTitle: TextView
            val courseStatus: ImageView
            val courseTime: TextView
            val coursePin: TextView
            val removeButton: Button

            init {
                courseTitle = itemView.findViewById(R.id.course_title)
                courseStatus = itemView.findViewById(R.id.course_status)
                courseTime = itemView.findViewById(R.id.course_time)
                coursePin = itemView.findViewById(R.id.course_pin)
                removeButton = itemView.findViewById(R.id.button_remove)
            }

            override fun onClick(view: View) {}
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

            holder.removeButton.setOnClickListener {
                (context as MainActivity).removeCourse(
                    availableCourses[position].catalog_num
                )
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = availableCourses.size
    }

    class AwaitingAdapter(private val awaitingCourses: List<Course>, private val context: Context) :
        RecyclerView.Adapter<AwaitingAdapter.ViewHolder>() {

        class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            val courseTitle: TextView
            val courseStatus: ImageView
            val courseTime: TextView
            val coursePin: TextView
            val removeButton: Button

            init {
                courseTitle = itemView.findViewById(R.id.course_title)
                courseStatus = itemView.findViewById(R.id.course_status)
                courseTime = itemView.findViewById(R.id.course_time)
                coursePin = itemView.findViewById(R.id.course_pin)

                removeButton = itemView.findViewById(R.id.button_remove)
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

            holder.removeButton.setOnClickListener {
                (context as MainActivity).removeCourse(
                    awaitingCourses[position].catalog_num
                )
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = awaitingCourses.size
    }
}
