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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.coursegrab.databinding.ActivityCourseDetailsBinding
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CourseDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseDetailsBinding

    private val viewModel: CourseDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val courseDetails: SearchResult = intent.getParcelableExtra<SearchResult>("courseDetails") as SearchResult

        binding.headerTextView.text = "${courseDetails.subject_code} ${courseDetails.course_num}"

        binding.courseTitle.text = courseDetails.title
        binding.courseDetails.text =
            if (courseDetails.sections.isNotEmpty()) courseDetails.sections.first().instructors.first() else "To Be Assigned"

        binding.sectionsRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@CourseDetailsActivity)
            adapter = SectionAdapter(courseDetails.sections, this@CourseDetailsActivity)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trackingErrors.collect { error ->
                    Toast.makeText(this@CourseDetailsActivity, error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.backBtn.setOnClickListener { finish() }
    }

    fun addCourse(courseId: Int) = viewModel.addCourse(courseId)

    fun removeCourse(courseId: Int) = viewModel.removeCourse(courseId)

    class SectionAdapter(
        private val availableCourses: List<Course>,
        private val context: Context
    ) :
        RecyclerView.Adapter<SectionAdapter.ViewHolder>() {

        class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            val sectionTitle: TextView = itemView.findViewById(R.id.section_title)
            val sectionStatus: ImageView = itemView.findViewById(R.id.section_status)
            val removeButton: Button = itemView.findViewById(R.id.button_remove)
            val trackButton: Button = itemView.findViewById(R.id.button_track)
            val trackText: TextView = itemView.findViewById(R.id.TrackingText)

            override fun onClick(view: View) {}
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.course_section_item, parent, false) as View
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.sectionTitle.text = availableCourses[position].section
            holder.sectionStatus.setImageResource(if (availableCourses[position].isOpen) R.drawable.ic_status_open else R.drawable.ic_status_closed)

            //change tracking text
            holder.trackText.text = availableCourses[position].num_tracking.toString() + " Tracking"

            //Change the track button to remove button
            holder.removeButton.visibility =
                if (availableCourses[position].is_tracking) View.VISIBLE else View.GONE
            holder.trackButton.visibility =
                if (!availableCourses[position].is_tracking) View.VISIBLE else View.GONE

            holder.removeButton.setOnClickListener {
                (context as CourseDetailsActivity).removeCourse(
                    availableCourses[position].catalog_num
                )
                holder.removeButton.visibility = View.GONE
                holder.trackButton.visibility = View.VISIBLE

                Toast.makeText(
                    context,
                    "Stop Tracking: " + availableCourses[position].section,
                    Toast.LENGTH_SHORT
                ).show()
            }

            holder.trackButton.setOnClickListener {
                (context as CourseDetailsActivity).addCourse(
                    availableCourses[position].catalog_num
                )
                holder.trackButton.visibility = View.GONE
                holder.removeButton.visibility = View.VISIBLE

                Toast.makeText(
                    context,
                    "Now Tracking: " + availableCourses[position].section,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = availableCourses.size
    }
}
