package com.cornellappdev.coursegrab.models

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiResponseTest {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Test
    fun `success decodes data as the payload type`() {
        val response = json.decodeFromString<ApiResponse<SearchResult>>(
            """
            {"success": true, "timestamp": 1, "data": {
                "id": 1342, "subject_code": "CS", "course_num": 2110,
                "title": "Object-Oriented Programming and Data Structures",
                "sections": [{"catalog_num": 6582, "course_id": 1342, "section": "LEC 001"}]
            }}
            """
        )

        assertTrue(response.success)
        assertEquals("CS", response.data?.subjectCode)
        assertEquals(6582, response.data?.sections?.single()?.catalogNum)
        assertTrue(response.errors.isEmpty())
    }

    @Test
    fun `failure reads errors instead of decoding data as the payload type`() {
        val response = json.decodeFromString<ApiResponse<SearchResult>>(
            """{"success": false, "timestamp": 1, "data": {"errors": ["Catalog number is not valid."]}}"""
        )

        assertFalse(response.success)
        assertNull(response.data)
        assertEquals(listOf("Catalog number is not valid."), response.errors)
    }

    @Test
    fun `failure without errors decodes to an empty list`() {
        val response = json.decodeFromString<ApiResponse<SearchResult>>(
            """{"success": false, "data": null}"""
        )

        assertFalse(response.success)
        assertTrue(response.errors.isEmpty())
    }

    @Test
    fun `status-only success with null data decodes`() {
        val response = json.decodeFromString<ApiResponse<Unit>>(
            """{"success": true, "data": null}"""
        )

        assertTrue(response.success)
        assertNull(response.data)
    }
}
