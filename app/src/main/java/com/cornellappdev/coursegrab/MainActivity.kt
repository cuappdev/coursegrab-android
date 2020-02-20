package com.cornellappdev.coursegrab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.coursegrab.models.Course
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var availableRecyclerView: RecyclerView
    private lateinit var availableViewAdapter: RecyclerView.Adapter<*>
    private lateinit var availableViewManager: RecyclerView.LayoutManager

    private lateinit var awaitingRecyclerView: RecyclerView
    private lateinit var awaitingViewAdapter: RecyclerView.Adapter<*>
    private lateinit var awaitingViewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tCourse0 = Course(
            10032, 1998, "LEC 606 / M 7:30PM", "OPEN",
            "CS", "Intro to Android"
        )
        val tCourse1 = Course(
            12401, 3110, "DIS 212 / TR 12:20pm - 1:10pm",
            "OPEN", "CS", "Data Structures and Functional Programming"
        )
        val templateList = listOf(tCourse0, tCourse1)

        val tCourse2 = Course(
            17982, 4021, "LEC 001 / MW 2:55pm - 4:10pm",
            "CLOSED", "LAW", "Competition Law and Policy"
        )
        val tCourse3 = Course(
            4959, 2701, "STU 501 / MW 1:25pm - 4:25pm", "CLOSED",
            "ART", "Digital Media: Introduction to Digital Media"
        )
        val tCourse4 = Course(
            11373, 3152, "DIS 201 / TR 11:15am - 12:05pm",
            "CLOSED", "CS", "Introduction to Computer Game Architecture"
        )
        val templateListAwaiting = listOf(tCourse2, tCourse3, tCourse4)

        // Available Courses Adapter
        availableViewManager = LinearLayoutManager(this)
        availableViewAdapter = AvailableAdapter(templateList)

        availableRecyclerView = findViewById<RecyclerView>(R.id.available_list).apply {
            layoutManager = availableViewManager
            adapter = availableViewAdapter
        }

        available_title.text = "${available_list.adapter?.itemCount} Available"

        // Awaiting Courses Adapter
        awaitingViewManager = LinearLayoutManager(this)
        awaitingViewAdapter = AwaitingAdapter(templateListAwaiting)

        awaitingRecyclerView = findViewById<RecyclerView>(R.id.awaiting_list).apply {
            layoutManager = awaitingViewManager
            adapter = awaitingViewAdapter
        }

        awaiting_title.text = "${awaiting_list.adapter?.itemCount} Awaiting"
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
