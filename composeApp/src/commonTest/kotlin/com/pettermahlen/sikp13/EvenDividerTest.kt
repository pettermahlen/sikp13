package com.pettermahlen.sikp13

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EvenDividerTest {
    private val divider = EvenDivider()

    @Test
    fun `empty list returns empty result`() {
        val result = divider.divideIntoGroups(emptyList(), 2)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invalid group count returns single group with all players`() {
        val players = listOf(
            Player("Player 1", SkillLevel.HARD),
            Player("Player 2", SkillLevel.EASY)
        )
        val result = divider.divideIntoGroups(players, 0)
        assertEquals(1, result.size)
        assertEquals(players, result[0])
    }

    @Test
    fun `evenly distributes players of different skill levels between two groups`() {
        val players = listOf(
            Player("Player 1", SkillLevel.HARD),
            Player("Player 2", SkillLevel.HARD),
            Player("Player 3", SkillLevel.MEDIUM),
            Player("Player 4", SkillLevel.MEDIUM),
            Player("Player 5", SkillLevel.EASY),
            Player("Player 6", SkillLevel.EASY)
        )
        
        val result = divider.divideIntoGroups(players, 2)
        
        assertEquals(2, result.size)
        // Each group should have 3 players
        assertEquals(3, result[0].size)
        assertEquals(3, result[1].size)
        
        // Each group should have one player of each skill level
        val group1SkillLevels = result[0].map { it.skillLevel }.sorted()
        val group2SkillLevels = result[1].map { it.skillLevel }.sorted()
        
        assertEquals(listOf(SkillLevel.EASY, SkillLevel.MEDIUM, SkillLevel.HARD), group1SkillLevels)
        assertEquals(listOf(SkillLevel.EASY, SkillLevel.MEDIUM, SkillLevel.HARD), group2SkillLevels)
    }

    @Test
    fun `distributes players evenly across three groups`() {
        val players = listOf(
            Player("Player 1", SkillLevel.HARD),
            Player("Player 2", SkillLevel.HARD),
            Player("Player 3", SkillLevel.HARD),
            Player("Player 4", SkillLevel.MEDIUM),
            Player("Player 5", SkillLevel.MEDIUM),
            Player("Player 6", SkillLevel.MEDIUM),
            Player("Player 7", SkillLevel.EASY),
            Player("Player 8", SkillLevel.EASY),
            Player("Player 9", SkillLevel.EASY)
        )
        
        val result = divider.divideIntoGroups(players, 3)
        
        assertEquals(3, result.size)
        // Each group should have 3 players
        result.forEach { group ->
            assertEquals(3, group.size)
        }
        
        // Each group should have one player of each skill level
        result.forEach { group ->
            val skillLevels = group.map { it.skillLevel }.sorted()
            assertEquals(listOf(SkillLevel.EASY, SkillLevel.MEDIUM, SkillLevel.HARD), skillLevels)
        }
    }

    @Test
    fun `handles uneven distribution while maintaining balance`() {
        val players = listOf(
            Player("Player 1", SkillLevel.HARD),
            Player("Player 2", SkillLevel.HARD),
            Player("Player 3", SkillLevel.MEDIUM),
            Player("Player 4", SkillLevel.MEDIUM),
            Player("Player 5", SkillLevel.EASY)
        )
        
        val result = divider.divideIntoGroups(players, 2)
        
        assertEquals(2, result.size)
        // First group should have 3 players, second group 2 players
        assertEquals(3, result[0].size)
        assertEquals(2, result[1].size)
        
        // Verify that skill levels are distributed evenly
        val group1SkillCount = result[0].groupBy { it.skillLevel }.mapValues { it.value.size }
        val group2SkillCount = result[1].groupBy { it.skillLevel }.mapValues { it.value.size }
        
        // No group should have more than one extra player of any skill level
        group1SkillCount.forEach { (_, count) ->
            assertTrue(count <= 2)
        }
        group2SkillCount.forEach { (_, count) ->
            assertTrue(count <= 2)
        }
    }

    @Test
    fun `maintains snake pattern distribution for optimal balance`() {
        val players = listOf(
            Player("Player 1", SkillLevel.HARD),   // Group 1
            Player("Player 2", SkillLevel.HARD),   // Group 2
            Player("Player 3", SkillLevel.HARD),   // Group 2
            Player("Player 4", SkillLevel.HARD),   // Group 1
            Player("Player 5", SkillLevel.MEDIUM), // Group 1
            Player("Player 6", SkillLevel.MEDIUM), // Group 2
            Player("Player 7", SkillLevel.EASY),   // Group 2
            Player("Player 8", SkillLevel.EASY)    // Group 1
        )
        
        val result = divider.divideIntoGroups(players, 2)
        
        assertEquals(2, result.size)
        assertEquals(4, result[0].size)
        assertEquals(4, result[1].size)
        
        // Verify snake pattern distribution
        // Group 1 should have first HARD, last HARD, first MEDIUM, last EASY
        val group1Skills = result[0].map { it.skillLevel }
        assertTrue(group1Skills.contains(SkillLevel.HARD))
        assertTrue(group1Skills.contains(SkillLevel.MEDIUM))
        assertTrue(group1Skills.contains(SkillLevel.EASY))
        
        // Group 2 should have second HARD, third HARD, last MEDIUM, first EASY
        val group2Skills = result[1].map { it.skillLevel }
        assertTrue(group2Skills.count { it == SkillLevel.HARD } == 2)
        assertTrue(group2Skills.contains(SkillLevel.MEDIUM))
        assertTrue(group2Skills.contains(SkillLevel.EASY))
    }
} 