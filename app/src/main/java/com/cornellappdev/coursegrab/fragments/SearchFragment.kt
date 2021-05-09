package com.cornellappdev.coursegrab.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.SearchActivity
import com.cornellappdev.coursegrab.adapters.SearchAdapter
import com.cornellappdev.coursegrab.presenters.SearchPresenter
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.layout_results
import kotlinx.android.synthetic.main.activity_search.no_results_view
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchFragment(mContext: Context) : Fragment() {
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchViewAdapter: RecyclerView.Adapter<*>
    private lateinit var searchViewManager: RecyclerView.LayoutManager

    lateinit var mCallback: FragmentChangeListener

    private val searchPresenter = SearchPresenter(mContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_search, container, false)
        val warningIcon: ImageView = rootView.findViewById(R.id.no_results_icon)
        val warningText: TextView = rootView.findViewById(R.id.no_results_title)
        val warningSubtitle: TextView = rootView.findViewById(R.id.no_results_subtitle)
        val resultsRecyclerView: RecyclerView = rootView.findViewById(R.id.results_list)
        val resultAmountText: TextView = rootView.findViewById(R.id.result_title)

        mCallback = activity as FragmentChangeListener
        rootView.back_btn.setOnClickListener {
            mCallback.onBackButtonPressed()
        }

        rootView.editText_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // check for no text in search or text length less than 3 char
                if (p0.isNullOrEmpty() || p0.length < 3) {
                    layout_results.visibility = View.GONE
                    no_results_view.visibility = View.VISIBLE
                    warningIcon.setImageResource(R.drawable.ic_warning)
                    warningText.text = getString(R.string.not_enough_character_alert)
                    warningSubtitle.text = getString(R.string.not_enough_character_subtext)
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        val searchResultsList = searchPresenter.searchCourses(p0.toString())
                        Log.d("lesley", searchResultsList.toString())

                        // set up results courses adapter
                        searchViewManager = LinearLayoutManager(activity?.applicationContext)
                        searchViewAdapter = SearchAdapter(searchResultsList)

                        searchRecyclerView = resultsRecyclerView.apply {
                            layoutManager = searchViewManager
                            adapter = searchViewAdapter
                        }
                        resultAmountText.text = "${resultsRecyclerView.adapter?.itemCount} Results"

                        // change warning text if there are no results
                        if (searchResultsList.isEmpty()) {
                            warningIcon.setImageResource(R.drawable.ic_status_closed)
                            warningText.text = getString(R.string.no_results_alert)
                            warningSubtitle.text = getString(R.string.no_results_alert_subtext_try_another)
                        }

                        layout_results.visibility = if (searchResultsList.isNotEmpty()) View.VISIBLE else View.GONE
                        no_results_view.visibility = if (searchResultsList.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(mContext: Context) = SearchFragment(mContext)
    }

    interface FragmentChangeListener {
        fun onBackButtonPressed()
    }
}