package com.cornellappdev.coursegrab

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.coursegrab.databinding.ActivityMainBinding
import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.models.TrackingContainer
import com.cornellappdev.coursegrab.networking.Endpoint
import com.cornellappdev.coursegrab.networking.Request
import com.cornellappdev.coursegrab.networking.addTracking
import com.cornellappdev.coursegrab.networking.getCourseByID
import com.cornellappdev.coursegrab.networking.getTracking
import com.cornellappdev.coursegrab.networking.removeTracking
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale.getDefault
import androidx.core.net.toUri


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        refreshAwaiting()

        binding.refreshCoursesLayout.setOnRefreshListener {
            refreshAwaiting()
        }

        binding.settingsBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.searchBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.addCourseButton.setOnClickListener {
            addCourse(binding.addCourseEditText.text.toString().toInt(), this)
            binding.addCourseEditText.clearFocus()
            binding.addCourseEditText.text.clear()
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.addCourseEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.addCourseButton.isEnabled = (s!!.length > 3)
            }
        })

        binding.addCourseEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                addCourse(binding.addCourseEditText.text.toString().toInt(), this)
                binding.addCourseEditText.clearFocus()
                binding.addCourseEditText.text.clear()
                val inputMethodManager =
                    getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                return@OnKeyListener true
            }
            false
        })

        // Check if permission is already granted
        if (Build.VERSION.SDK_INT >= 33 && (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1,
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(
                this,
                "Please enable notifications in settings to receive course updates",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()

        refreshAwaiting()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
    }

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
            }!!.data.sections ?: listOf()

            for (course in courseList) {
                if (course.status == "OPEN")
                    listOpen.add(course)
                else
                    listAwaiting.add(course)
            }

            // Available Courses Adapter
            availableViewManager = LinearLayoutManager(this@MainActivity)
            availableViewAdapter = AvailableAdapter(listOpen, this@MainActivity)

            availableRecyclerView = binding.availableList.apply {
                layoutManager = availableViewManager
                adapter = availableViewAdapter
            }
            binding.availableTitle.text = "${binding.availableList.adapter?.itemCount} Available"

            // Awaiting Courses Adapter
            awaitingViewManager = LinearLayoutManager(this@MainActivity)
            awaitingViewAdapter = AwaitingAdapter(listAwaiting, this@MainActivity)

            awaitingRecyclerView = binding.awaitingList.apply {
                layoutManager = awaitingViewManager
                adapter = awaitingViewAdapter
            }
            binding.awaitingTitle.text = "${binding.awaitingList.adapter?.itemCount} Awaiting"

            binding.layoutAvailable.visibility =
                if (listOpen.isNotEmpty()) View.VISIBLE else View.GONE
            binding.layoutAwaiting.visibility =
                if (listAwaiting.isNotEmpty()) View.VISIBLE else View.GONE

            binding.noCoursesView.visibility =
                if (listOpen.isEmpty() && listAwaiting.isEmpty()) View.VISIBLE else View.GONE

            binding.refreshCoursesLayout.isRefreshing = false
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

    private fun editCourse(courseId: Int, context: Context) {
        val editCourse =
            Endpoint.getCourseByID(preferencesHelper.sessionToken.toString(), courseId)

        CoroutineScope(Dispatchers.Main).launch {
            val typeToken = object : TypeToken<ApiResponse<SearchResult>>() {}.type
            val course = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<SearchResult>>(
                    editCourse.okHttpRequest(),
                    typeToken
                )
            }!!.data


//            context.setOnClickListener {
            val intent = Intent(context, CourseDetailsActivity::class.java).apply {
                putExtra("courseDetails", course)
            }
            context.startActivity(intent)
//            }


        }
    }

    private fun enrollCourse() {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, "http://studentcenter.cornell.edu".toUri())
        startActivity(browserIntent)
    }

    class AvailableAdapter(
        private val availableCourses: List<Course>,
        private val context: Context
    ) :
        RecyclerView.Adapter<AvailableAdapter.ViewHolder>() {

        class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            val courseTitle: TextView = itemView.findViewById(R.id.course_title)
            val courseStatus: ImageView = itemView.findViewById(R.id.course_status)
            val courseTime: TextView = itemView.findViewById(R.id.course_time)
            val coursePin: TextView = itemView.findViewById(R.id.course_pin)
            val enrollButton: Button = itemView.findViewById(R.id.button_enroll)
            val removeButton: Button = itemView.findViewById(R.id.button_remove)
            val backgroundButton: Button = itemView.findViewById(R.id.background_Button)

            override fun onClick(view: View) {}
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.course_available_list_item_v2, parent, false) as View
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.courseTitle.text =
                "${availableCourses[position].subject_code} ${availableCourses[position].course_num}: ${availableCourses[position].title}"
            holder.courseTime.text = availableCourses[position].section.uppercase(getDefault())
            holder.coursePin.text = availableCourses[position].catalog_num.toString()
            holder.courseStatus.setImageResource(if (availableCourses[position].status == "OPEN") R.drawable.ic_status_open else R.drawable.ic_status_closed)

            holder.removeButton.setOnClickListener {
                (context as MainActivity).removeCourse(
                    availableCourses[position].catalog_num, context
                )
            }

            holder.enrollButton.setOnClickListener {
                (context as MainActivity).enrollCourse(
                )
            }

            holder.backgroundButton.setOnClickListener {
                (context as MainActivity).editCourse(
                    availableCourses[position].catalog_num, context
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
            val courseTitle: TextView = itemView.findViewById(R.id.course_title)
            val courseStatus: ImageView = itemView.findViewById(R.id.course_status)
            val courseTime: TextView = itemView.findViewById(R.id.course_time)
            val coursePin: TextView = itemView.findViewById(R.id.course_pin)
            val removeButton: Button = itemView.findViewById(R.id.button_remove)
            val backgroundButton: Button = itemView.findViewById(R.id.background_Button)
            override fun onClick(view: View) {

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.course_awaiting_list_item_v2, parent, false) as View
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.courseTitle.text =
                "${awaitingCourses[position].subject_code} ${awaitingCourses[position].course_num}: ${awaitingCourses[position].title}"
            holder.courseTime.text = awaitingCourses[position].section.uppercase(getDefault())
            holder.coursePin.text = awaitingCourses[position].catalog_num.toString()
            holder.courseStatus.setImageResource(if (awaitingCourses[position].status == "OPEN") R.drawable.ic_status_open else R.drawable.ic_status_closed)

            holder.removeButton.setOnClickListener {
                (context as MainActivity).removeCourse(
                    awaitingCourses[position].catalog_num, context
                )
            }
            holder.backgroundButton.setOnClickListener {
                (context as MainActivity).editCourse(
                    awaitingCourses[position].catalog_num, context
                )
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = awaitingCourses.size
    }
}
