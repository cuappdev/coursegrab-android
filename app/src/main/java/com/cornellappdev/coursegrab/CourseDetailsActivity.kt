package com.cornellappdev.coursegrab

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.models.Section
import com.cornellappdev.coursegrab.networking.Endpoint
import com.cornellappdev.coursegrab.networking.Request
import com.cornellappdev.coursegrab.networking.addTracking
import com.cornellappdev.coursegrab.networking.removeTracking
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_course_details.*
import kotlinx.android.synthetic.main.activity_search.back_btn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseDetailsActivity : AppCompatActivity() {
    private lateinit var sectionsRecyclerView: RecyclerView
    private lateinit var sectionsViewAdapter: RecyclerView.Adapter<*>
    private lateinit var sectionsViewManager: RecyclerView.LayoutManager

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_details)

        var courseDetails: SearchResult = intent.getParcelableExtra("courseDetails") as SearchResult

        header_textView.text = "${courseDetails.subject_code} ${courseDetails.course_num}"

        course_title.text = courseDetails.title
        course_details.text =
            if (courseDetails.sections.isNotEmpty()) courseDetails.sections.first().instructors.first() else "To Be Assigned"

        // Available Courses Adapter
        sectionsViewManager = LinearLayoutManager(this@CourseDetailsActivity)
        sectionsViewAdapter = SectionAdapter(courseDetails.sections, this@CourseDetailsActivity)

        sectionsRecyclerView = findViewById<RecyclerView>(R.id.sections_recyclerview).apply {
            layoutManager = sectionsViewManager
            adapter = sectionsViewAdapter
        }

        back_btn.setOnClickListener { finish() }
    }

    fun addCourse(courseId: Int, context: Context) {
        val addTracking = Endpoint.addTracking(preferencesHelper.sessionToken.toString(), courseId)

        CoroutineScope(Dispatchers.Main).launch {
            val typeToken = object : TypeToken<ApiResponse<Course>>() {}.type
            val response = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<Course>>(
                    addTracking.okHttpRequest(),
                    typeToken
                )
            }

            if (!response!!.success)
                Toast.makeText(
                    context,
                    response.data.errors!![0],
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    fun removeCourse(courseId: Int, context: Context) {
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

            if (!response!!.success)
                Toast.makeText(
                    context,
                    response.data.errors!![0],
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    class SectionAdapter(
        private val availableCourses: List<Section>,
        private val context: Context
    ) :
        RecyclerView.Adapter<SectionAdapter.ViewHolder>() {

        class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            val sectionTitle: TextView = itemView.findViewById(R.id.section_title)
            val sectionStatus: ImageView = itemView.findViewById(R.id.section_status)
            val removeButton: Button = itemView.findViewById(R.id.button_remove)
            val trackButton: Button = itemView.findViewById(R.id.button_track)

            override fun onClick(view: View) {}
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.course_section_item, parent, false) as View
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.sectionTitle.text = availableCourses[position].section
            holder.sectionStatus.setImageResource(if (availableCourses[position].status == "OPEN") R.drawable.ic_status_open else R.drawable.ic_status_closed)

            holder.removeButton.visibility =
                if (availableCourses[position].is_tracking) View.VISIBLE else View.GONE
            holder.trackButton.visibility =
                if (!availableCourses[position].is_tracking) View.VISIBLE else View.GONE

            holder.removeButton.setOnClickListener {
                (context as CourseDetailsActivity).removeCourse(
                    availableCourses[position].catalog_num,
                    context
                )
                holder.removeButton.visibility = View.GONE
                holder.trackButton.visibility = View.VISIBLE
            }

            holder.trackButton.setOnClickListener {
                (context as CourseDetailsActivity).addCourse(
                    availableCourses[position].catalog_num,
                    context
                )
                holder.trackButton.visibility = View.GONE
                holder.removeButton.visibility = View.VISIBLE
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = availableCourses.size
    }
}
