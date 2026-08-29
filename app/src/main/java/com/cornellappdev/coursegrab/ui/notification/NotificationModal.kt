package com.cornellappdev.coursegrab.ui.notification

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.databinding.ActivityNotificationModalBinding
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.CourseNotification
import com.cornellappdev.coursegrab.ui.main.MainActivity

class NotificationModal : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationModalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationModalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val course: Course =
            (intent.getParcelableExtra<CourseNotification>("courseDetails") as CourseNotification).section

        binding.courseTitle.text = getString(
            R.string.course_title_format,
            course.subjectCode,
            course.courseNum,
            course.title
        )
        binding.courseSection.text = course.section
        binding.coursePin.text = getString(R.string.course_pin_format, course.catalogNum)

        binding.buttonStudentCenter.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, "http://studentcenter.cornell.edu".toUri())
            startActivity(browserIntent)
        }

        binding.buttonBackHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
