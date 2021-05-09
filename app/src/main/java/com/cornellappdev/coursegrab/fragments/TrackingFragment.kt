package com.cornellappdev.coursegrab.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.coursegrab.*
import com.cornellappdev.coursegrab.adapters.TrackingAdapter
import com.cornellappdev.coursegrab.presenters.TrackingPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tracking.*
import kotlinx.android.synthetic.main.fragment_tracking.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrackingFragment(mContext: Context) : Fragment() {
    private lateinit var availableRecyclerView: RecyclerView
    private lateinit var availableViewAdapter: RecyclerView.Adapter<*>
    private lateinit var availableViewManager: RecyclerView.LayoutManager

    private lateinit var awaitingRecyclerView: RecyclerView
    private lateinit var awaitingViewAdapter: RecyclerView.Adapter<*>
    private lateinit var awaitingViewManager: RecyclerView.LayoutManager

    lateinit var mCallback: FragmentChangeListener

    private val mainPresenter = TrackingPresenter(mContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refreshAwaiting()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View =
            inflater.inflate(R.layout.fragment_tracking, container, false)
        mCallback = activity as FragmentChangeListener

        rootView.refresh_courses_layout.setOnRefreshListener {
            refreshAwaiting()
        }

        rootView.settings_btn.setOnClickListener {
            //TODO: show bottom settings module
        }

        rootView.search_btn.setOnClickListener {
            mCallback.onSearchButtonPressed()
        }
        return rootView
    }

    override fun onResume() {
        super.onResume()
        refreshAwaiting()
    }

    fun refreshAwaiting() {
        CoroutineScope(Dispatchers.Main).launch {
            val courses = mainPresenter.refreshCourseList()
            val listOpen = courses?.let { courses["open"]} ?: emptyList()
            val listAwaiting = courses?.let { courses["awaiting"]} ?: emptyList()

            // Set available courses adapter
            availableViewManager = LinearLayoutManager(activity?.applicationContext)
            availableViewAdapter = TrackingAdapter(listOpen, mainPresenter, false, this@TrackingFragment)

            availableRecyclerView = available_list.apply {
                layoutManager = availableViewManager
                adapter = availableViewAdapter
            }
            available_title.text = "${available_list.adapter?.itemCount} Available"

            // Set awaiting courses adapter
            awaitingViewManager = LinearLayoutManager(context)
            awaitingViewAdapter = TrackingAdapter(listAwaiting, mainPresenter,true, this@TrackingFragment)

            awaitingRecyclerView = awaiting_list.apply {
                layoutManager = awaitingViewManager
                adapter = awaitingViewAdapter
            }
            awaiting_title.text = "${awaiting_list.adapter?.itemCount} Awaiting"

            // hide list layouts if the list is empty
            layout_available.visibility = if (listOpen.isNotEmpty()) View.VISIBLE else View.GONE
            layout_awaiting.visibility = if (listAwaiting.isNotEmpty()) View.VISIBLE else View.GONE

            no_courses_view.visibility =
                if (listOpen.isEmpty() && listAwaiting.isEmpty()) View.VISIBLE else View.GONE

            refresh_courses_layout.isRefreshing = false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(context: Context) = TrackingFragment(context)
    }

    interface FragmentChangeListener {
        fun onSearchButtonPressed()
    }
}