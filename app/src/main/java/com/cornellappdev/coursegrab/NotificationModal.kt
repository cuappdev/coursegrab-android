package com.cornellappdev.coursegrab

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cornellappdev.coursegrab.databinding.ActivityNotificationModalBinding
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.CourseNotification

class NotificationModal : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationModalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationModalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val course: Course =
            (intent.getParcelableExtra<CourseNotification>("courseDetails") as CourseNotification).section

        binding.courseTitle.text = "${course.subject_code} ${course.course_num}: ${course.title}"
        binding.courseSection.text = course.section
        binding.coursePin.text = course.catalog_num.toString()

        binding.buttonStudentCenter.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://studentcenter.cornell.edu"))
            startActivity(browserIntent)
        }

        binding.buttonBackHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
