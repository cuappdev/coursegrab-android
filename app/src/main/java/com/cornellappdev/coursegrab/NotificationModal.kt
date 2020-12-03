package com.cornellappdev.coursegrab

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cornellappdev.coursegrab.models.CourseNotification
import com.cornellappdev.coursegrab.models.Section
import kotlinx.android.synthetic.main.activity_notification_modal.*

class NotificationModal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_modal)

        val course: Section =
            (intent.getParcelableExtra<CourseNotification>("courseDetails") as CourseNotification).section

        course_title.text = "${course.subject_code} ${course.course_num}: ${course.title}"
        course_section.text = course.section
        course_pin.text = course.catalog_num.toString()

        button_student_center.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://studentcenter.cornell.edu"))
            startActivity(browserIntent)
        }

        button_back_home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
