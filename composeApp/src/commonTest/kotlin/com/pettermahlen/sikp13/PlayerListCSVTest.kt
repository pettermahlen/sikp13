package com.pettermahlen.sikp13

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PlayerListCSVTest {
    @Test
    fun testParseEmptyCSV() {
        val result = parseCSV("")
        assertTrue(result.isEmpty())
    }

    @Test
    fun testParseCSVWithHeaderOnly() {
        val result = parseCSV("name,skill_level")
        assertTrue(result.isEmpty())
    }

    @Test
    fun testParseValidCSV() {
        val csv = """
            name,skill_level
            John Doe,NEW
            Jane Smith,MEDIUM
        """.trimIndent()
        
        val result = parseCSV(csv)
        assertEquals(2, result.size)
        assertEquals("John Doe", result[0].name)
        assertEquals(SkillLevel.NEW, result[0].skillLevel)
        assertEquals("Jane Smith", result[1].name)
        assertEquals(SkillLevel.MEDIUM, result[1].skillLevel)
    }

    @Test
    fun testInvalidSkillLevel() {
        val csv = """
            name,skill_level
            John Doe,INVALID_LEVEL
        """.trimIndent()
        
        assertFailsWith<IllegalArgumentException> {
            parseCSV(csv)
        }
    }

    @Test
    fun testInvalidCSVFormat() {
        val csv = """
            name,skill_level
            John Doe,NEW,extra
        """.trimIndent()
        
        assertFailsWith<IllegalArgumentException> {
            parseCSV(csv)
        }
    }


    @Test
    fun testToCSVEmpty() {
        val result = PlayerListCSV.toCSV(emptyList())
        assertEquals("name,skill_level", result)
    }

    @Test
    fun testToCSVWithPlayers() {
        val players = listOf(
            Player("John Doe", SkillLevel.NEW),
            Player("Jane Smith", SkillLevel.HARD)
        )
        
        val expected = """
            name,skill_level
            John Doe,NEW
            Jane Smith,HARD
        """.trimIndent()
        
        assertEquals(expected, PlayerListCSV.toCSV(players))
    }

    @Test
    fun testParseCSVWithWhitespace() {
        val csv = """
            name,skill_level
              John Doe  ,  EASY  
              Jane Smith,MEDIUM  
                Bob Wilson   ,   HARD   
        """.trimIndent()
        
        val result = parseCSV(csv)
        assertEquals(3, result.size)
        assertEquals("John Doe", result[0].name)
        assertEquals(SkillLevel.EASY, result[0].skillLevel)
        assertEquals("Jane Smith", result[1].name)
        assertEquals(SkillLevel.MEDIUM, result[1].skillLevel)
        assertEquals("Bob Wilson", result[2].name)
        assertEquals(SkillLevel.HARD, result[2].skillLevel)
    }
} 