package com.cornellappdev.coursegrab

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.coursegrab.models.Course

class TrackingAdapter(
    private val coursesList: List<Course>,
    private val presenter: MainPresenter,
    private val context: Context,
    private val isAwaiting: Boolean
) :
    RecyclerView.Adapter<TrackingAdapter.ViewHolder>() {

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val courseTitle: TextView = itemView.findViewById(R.id.course_title)
        val courseStatus: ImageView = itemView.findViewById(R.id.course_status)
        val courseTime: TextView = itemView.findViewById(R.id.course_time)
        val courseNumber: TextView = itemView.findViewById(R.id.catalog_number)
        val courseMode: TextView = itemView.findViewById(R.id.course_modality)
        val removeButton: Button = itemView.findViewById(R.id.button_remove)
        val enrollButton: Button = itemView.findViewById(R.id.button_enroll)
        //TODO: add modality
        override fun onClick(view: View) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_tracking_list_item, parent, false) as View
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.courseTitle.text =
            "${coursesList[position].subject_code} ${coursesList[position].course_num}: ${coursesList[position].title}"
        holder.courseTime.text = coursesList[position].section
        holder.courseNumber.text = coursesList[position].catalog_num.toString()
        holder.courseMode.text = coursesList[position].mode
        holder.courseStatus.setImageResource(if (coursesList[position].status == "OPEN") R.drawable.ic_status_open else R.drawable.ic_status_closed)

        holder.removeButton.setOnClickListener {
            //TODO
            presenter.removeCourse(coursesList[position].catalog_num)
        }

        if (isAwaiting) {
            holder.enrollButton.visibility = View.GONE
        } else {
            holder.enrollButton?.setOnClickListener {
                presenter.enrollCourse(coursesList[position].catalog_num)
            }
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = coursesList.size
}