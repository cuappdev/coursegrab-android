package com.cornellappdev.coursegrab.ui.components

import com.cornellappdev.coursegrab.models.Course

val Course.displayCode: String get() = "$subjectCode $courseNum"

// Backend instructors look like "Matthew Eichhorn (mae226)"; the design shows "Eichhorn, M".
fun Course.displayInstructor(): String? {
    val first = instructors.firstOrNull()?.substringBefore(" (")?.trim()?.takeIf { it.isNotEmpty() }
        ?: return null
    val names = first.split(' ').filter { it.isNotEmpty() }
    val name = if (names.size < 2) first else "${names.last()}, ${names.first().first()}"
    val others = instructors.size - 1
    return if (others > 0) "$name +$others" else name
}

private val TimeOfDay = Regex("""(\d{1,2}:\d{2})\s?([ap]m)""", RegexOption.IGNORE_CASE)

val Course.displaySection: String
    get() = TimeOfDay.replace(section) { "${it.groupValues[1]} ${it.groupValues[2].uppercase()}" }
