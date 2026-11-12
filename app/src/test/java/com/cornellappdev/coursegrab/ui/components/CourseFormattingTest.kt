package com.cornellappdev.coursegrab.ui.components

import com.cornellappdev.coursegrab.models.Course
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CourseFormattingTest {

    @Test
    fun `section times are spaced and uppercased`() {
        assertEquals("DIS 211 / F 1:25 PM", Course(section = "DIS 211 / F 1:25pm").displaySection)
        assertEquals("LEC 001 / MWF 10:10 AM", Course(section = "LEC 001 / MWF 10:10AM").displaySection)
        assertEquals("LEC 001 / TBA", Course(section = "LEC 001 / TBA").displaySection)
    }

    @Test
    fun `instructors are shown as last name and first initial`() {
        assertEquals("Eichhorn, M", Course(instructors = listOf("Matthew Eichhorn (mae226)")).displayInstructor())
        assertEquals(
            "Sampson, A +1",
            Course(instructors = listOf("Adrian Sampson (als485)", "Jane Doe (jd1)")).displayInstructor()
        )
        assertEquals("Cher", Course(instructors = listOf("Cher")).displayInstructor())
        assertNull(Course().displayInstructor())
    }
}
