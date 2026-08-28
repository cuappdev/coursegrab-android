package com.cornellappdev.coursegrab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.coursegrab.databinding.ActivitySearchBinding
import com.cornellappdev.coursegrab.models.SearchResult
import com.cornellappdev.coursegrab.networking.CourseGrabRepository
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchViewAdapter: RecyclerView.Adapter<*>
    private lateinit var searchViewManager: RecyclerView.LayoutManager

    private val repository: CourseGrabRepository by lazy {
        CourseGrabRepository(PreferencesHelper(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editTextSearch.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                searchCourses(binding.editTextSearch.text.toString())
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                return@OnKeyListener true
            }
            false
        })

        binding.editTextSearch.doOnTextChanged { text, _, _, _ ->
            if ((text ?: "").length > 2) {
                searchCourses(text.toString())
            } else {
                binding.layoutResults.visibility = View.GONE
                binding.noResultsView.visibility = View.VISIBLE
                binding.noResultsIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_status_warning
                    )
                )
                binding.noResultsTitle.text = getString(R.string.requires_longer_search)
                binding.noResultsSubtitle.text = getString(R.string.requires_longer_search_subtext)
            }
        }

        binding.backBtn.setOnClickListener { finish() }
    }

    private fun searchCourses(query: String) {
        lifecycleScope.launch {
            val result = repository.searchCourses(query)

            // A slower earlier request may land after the user has typed on; ignore it.
            if (binding.editTextSearch.text.toString() != query)
                return@launch

            val courseList = result.getOrElse { error ->
                Log.e(TAG, "Search failed for query \"$query\"", error)
                showSearchError()
                return@launch
            }

            // Results Courses Adapter
            searchViewManager = LinearLayoutManager(this@SearchActivity)
            searchViewAdapter = ResultsAdapter(courseList, this@SearchActivity)

            searchRecyclerView = binding.resultsList.apply {
                layoutManager = searchViewManager
                adapter = searchViewAdapter
            }
            binding.resultTitle.text = "${binding.resultsList.adapter?.itemCount} Results"

            binding.layoutResults.visibility =
                if (courseList.isNotEmpty()) View.VISIBLE else View.GONE
            binding.noResultsView.visibility = if (courseList.isEmpty()) View.VISIBLE else View.GONE
            binding.noResultsIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SearchActivity,
                    R.drawable.ic_status_closed
                )
            )
            binding.noResultsTitle.text = getString(R.string.no_courses_alert)
            binding.noResultsSubtitle.text =
                getString(R.string.no_results_alert_subtext_try_another)
        }
    }

    private fun showSearchError() {
        binding.layoutResults.visibility = View.GONE
        binding.noResultsView.visibility = View.VISIBLE
        binding.noResultsIcon.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_status_warning)
        )
        binding.noResultsTitle.text = getString(R.string.search_failed)
        binding.noResultsSubtitle.text = getString(R.string.search_failed_subtext)
    }

    class ResultsAdapter(
        private val resultsCourses: List<SearchResult>,
        private val context: Context
    ) :
        RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {

        class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            val courseTitle: TextView = itemView.findViewById(R.id.course_title)
            val expandButton: ImageButton = itemView.findViewById(R.id.expand_button)

            override fun onClick(view: View) {}
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.course_search_list_item, parent, false) as View
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.courseTitle.text =
                "${resultsCourses[position].subject_code} ${resultsCourses[position].course_num}: ${resultsCourses[position].title}"

            holder.expandButton.setOnClickListener {
                val intent = Intent(context, CourseDetailsActivity::class.java).apply {
                    putExtra("courseDetails", resultsCourses[position])
                }
                context.startActivity(intent)
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = resultsCourses.size
    }

    companion object {
        private const val TAG = "SearchActivity"
    }
}
