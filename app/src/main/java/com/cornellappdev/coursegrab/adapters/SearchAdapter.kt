package com.cornellappdev.coursegrab.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.models.SearchResult

class SearchAdapter(
    private val resultsCourseList: List<SearchResult>
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        val courseTitle: TextView = itemView.findViewById(R.id.course_title)
        val expandButton: ImageButton = itemView.findViewById(R.id.expand_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_search_list_item, parent, false) as View
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.courseTitle.text =
            "${resultsCourseList[position].subject_code} ${resultsCourseList[position].course_num}: ${resultsCourseList[position].title}"

        holder.expandButton.setOnClickListener {
            //TODO
//            val intent = Intent(context, CourseDetailsActivity::class.java).apply {
//                putExtra("courseDetails", resultsCourseList[position])
//            }
//            context.startActivity(intent)
        }
    }

    override fun getItemCount() = resultsCourseList.size
}