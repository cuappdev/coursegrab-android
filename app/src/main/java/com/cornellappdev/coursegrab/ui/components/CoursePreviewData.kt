package com.cornellappdev.coursegrab.ui.components

import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.SearchResult

/**
 * Sample data for `@Preview`s.
 *
 * Status is a backend string that only [Course.isOpen] knows how to read — and it reads it
 * as "anything that isn't OPEN is closed", so there is no `CLOSED` constant to share.
 * Previews say [isOpen] and this is the single place that turns it back into the wire value.
 */
internal fun sampleCourse(
    catalogNum: Int = 10032,
    subjectCode: String = "CS",
    courseNum: Int = 1998,
    title: String = "Intro to Android Development",
    section: String = "LEC 001 / M 7:30PM",
    isOpen: Boolean = true,
    isTracking: Boolean = false,
    numTracking: Int = 12
): Course = Course(
    catalogNum = catalogNum,
    courseNum = courseNum,
    section = section,
    instructors = listOf(SAMPLE_INSTRUCTOR),
    isTracking = isTracking,
    status = if (isOpen) STATUS_OPEN else STATUS_CLOSED,
    subjectCode = subjectCode,
    title = title,
    numTracking = numTracking
)

internal fun sampleSearchResult(
    subjectCode: String = "CS",
    courseNum: Int = 1998,
    title: String = "Intro to Android Development",
    sections: List<Course> = listOf(sampleCourse())
): SearchResult = SearchResult(
    subjectCode = subjectCode,
    courseNum = courseNum,
    title = title,
    sections = sections
)

internal const val SAMPLE_INSTRUCTOR = "Adrian Sampson (als485)"

private const val STATUS_OPEN = "OPEN"
private const val STATUS_CLOSED = "CLOSED"
