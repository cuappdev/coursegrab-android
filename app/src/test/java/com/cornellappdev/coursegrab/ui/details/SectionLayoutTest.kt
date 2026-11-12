package com.cornellappdev.coursegrab.ui.details

import com.cornellappdev.coursegrab.models.Course
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SectionLayoutTest {

    private fun section(name: String) = Course(catalogNum = name.hashCode(), section = name)

    @Test
    fun `a single lecture is the primary section and the rest are grouped by type`() {
        val layout = layoutSections(
            listOf(section("LEC 001 / MW 10:10AM"), section("DIS 201 / R 9:05AM"), section("DIS 202 / R 10:10AM"))
        )
        assertEquals("LEC 001 / MW 10:10AM", layout.primary?.section)
        assertEquals(listOf("DIS"), layout.groups.map { it.type })
        assertEquals(2, layout.groups.single().sections.size)
    }

    @Test
    fun `multiple lectures are listed as a group with no primary section`() {
        val layout = layoutSections(
            listOf(section("LEC 001 / MW 10:10AM"), section("LEC 002 / TR 1:25PM"), section("LAB 401 / F 2:30PM"))
        )
        assertNull(layout.primary)
        assertEquals(listOf("LEC", "LAB"), layout.groups.map { it.type })
    }

    @Test
    fun `no sections gives an empty layout`() {
        val layout = layoutSections(emptyList())
        assertNull(layout.primary)
        assertEquals(emptyList<SectionGroup>(), layout.groups)
    }
}
