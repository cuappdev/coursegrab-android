package com.cornellappdev.coursegrab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cornellappdev.coursegrab.fragments.TrackingFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager = supportFragmentManager;
        fragmentManager.beginTransaction()
            .add(R.id.fragment_container, TrackingFragment(this))
            .addToBackStack("tracking")
            .commit()


        //TODO: remove
//        addCourseButton.setOnClickListener {
//            addCourse(addCourseEditText.text.toString().toInt(), this)
//            addCourseEditText.clearFocus()
//            addCourseEditText.text.clear()
//            val inputMethodManager =
//                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
//        }
//
//        addCourseEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {}
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                addCourseButton.isEnabled = (s!!.length > 3)
//            }
//        })
//
//        addCourseEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
//                addCourse(addCourseEditText.text.toString().toInt(), this)
//                addCourseEditText.clearFocus()
//                addCourseEditText.text.clear()
//                val inputMethodManager =
//                    getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
//                return@OnKeyListener true
//            }
//            false
//        })
    }

//    override fun onResume() {
//        super.onResume()
//        refreshAwaiting()
//    }

    override fun onBackPressed() {}


    //TODO
//    private fun addCourse(courseId: Int, context: Context) {
//        val addTracking = Endpoint.addTracking(preferencesHelper.sessionToken.toString(), courseId)
//
//        CoroutineScope(Dispatchers.Main).launch {
//            val typeToken = object : TypeToken<ApiResponse<Course>>() {}.type
//            val response = withContext(Dispatchers.IO) {
//                Request.makeRequest<ApiResponse<Course>>(
//                    addTracking.okHttpRequest(),
//                    typeToken
//                )
//            }
//
//            refreshAwaiting()
//
//            if (!response!!.success)
//                Toast.makeText(
//                    context,
//                    response.data.errors!![0],
//                    Toast.LENGTH_SHORT
//                ).show()
//        }
//    }
}
