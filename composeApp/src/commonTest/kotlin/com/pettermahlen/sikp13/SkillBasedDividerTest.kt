package com.pettermahlen.sikp13

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SkillBasedDividerTest {
    private val divider = SkillBasedDivider()

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
    fun `divides players into groups based on skill level`() {
        val players = listOf(
            Player("Player 1", SkillLevel.HARD),
            Player("Player 2", SkillLevel.MEDIUM),
            Player("Player 3", SkillLevel.MEDIUM),
            Player("Player 4", SkillLevel.EASY),
            Player("Player 5", SkillLevel.EASY)
        )
        
        val result = divider.divideIntoGroups(players, 2)
        
        assertEquals(2, result.size)
        // First group should have players 1, 2, 3 (HARD, MEDIUM, MEDIUM)
        assertEquals(3, result[0].size)
        assertEquals(SkillLevel.HARD, result[0][0].skillLevel)
        assertEquals(SkillLevel.MEDIUM, result[0][1].skillLevel)
        assertEquals(SkillLevel.MEDIUM, result[0][2].skillLevel)
        
        // Second group should have players 4, 5 (EASY, EASY)
        assertEquals(2, result[1].size)
        assertEquals(SkillLevel.EASY, result[1][0].skillLevel)
        assertEquals(SkillLevel.EASY, result[1][1].skillLevel)
    }

    @Test
    fun `distributes players into skill-based groups`() {
        val players = listOf(
            Player("Player 1", SkillLevel.HARD),
            Player("Player 2", SkillLevel.HARD),
            Player("Player 3", SkillLevel.MEDIUM),
            Player("Player 4", SkillLevel.MEDIUM),
            Player("Player 5", SkillLevel.EASY),
            Player("Player 6", SkillLevel.EASY)
        )
        
        val result = divider.divideIntoGroups(players, 3)
        
        // Should have 3 groups with 2 players each
        assertEquals(3, result.size)
        result.forEach { group ->
            assertEquals(2, group.size)
        }
        
        // First group should have the HARD players
        assertEquals(SkillLevel.HARD, result[0][0].skillLevel)
        assertEquals(SkillLevel.HARD, result[0][1].skillLevel)
        
        // Second group should have the MEDIUM players
        assertEquals(SkillLevel.MEDIUM, result[1][0].skillLevel)
        assertEquals(SkillLevel.MEDIUM, result[1][1].skillLevel)
        
        // Third group should have the EASY players
        assertEquals(SkillLevel.EASY, result[2][0].skillLevel)
        assertEquals(SkillLevel.EASY, result[2][1].skillLevel)
    }

    @Test
    fun `handles uneven distribution while maintaining skill groups`() {
        val players = listOf(
            Player("Player 1", SkillLevel.HARD),
            Player("Player 2", SkillLevel.HARD),
            Player("Player 3", SkillLevel.MEDIUM),
            Player("Player 4", SkillLevel.EASY),
            Player("Player 5", SkillLevel.EASY)
        )
        
        val result = divider.divideIntoGroups(players, 3)
        
        assertEquals(3, result.size)
        // First group should have the HARD players
        assertEquals(2, result[0].size)
        assertEquals(SkillLevel.HARD, result[0][0].skillLevel)
        assertEquals(SkillLevel.HARD, result[0][1].skillLevel)
        
        // Second group should have the MEDIUM player
        assertEquals(2, result[1].size)
        assertEquals(SkillLevel.MEDIUM, result[1][0].skillLevel)
        assertEquals(SkillLevel.EASY, result[1][1].skillLevel)
        
        // Third group should have remaining EASY player
        assertEquals(1, result[2].size)
        assertEquals(SkillLevel.EASY, result[2][0].skillLevel)
    }

    @Test
    fun `maintains skill level order within groups`() {
        val players = listOf(
            Player("Player 1", SkillLevel.HARD),
            Player("Player 2", SkillLevel.HARD),
            Player("Player 3", SkillLevel.MEDIUM),
            Player("Player 4", SkillLevel.MEDIUM),
            Player("Player 5", SkillLevel.EASY),
            Player("Player 6", SkillLevel.EASY)
        )
        
        val result = divider.divideIntoGroups(players, 2)
        
        // First group should have HARD and MEDIUM players
        assertEquals(3, result[0].size)
        assertEquals(SkillLevel.HARD, result[0][0].skillLevel)
        assertEquals(SkillLevel.HARD, result[0][1].skillLevel)
        assertEquals(SkillLevel.MEDIUM, result[0][2].skillLevel)
        
        // Second group should have MEDIUM and EASY players
        assertEquals(3, result[1].size)
        assertEquals(SkillLevel.MEDIUM, result[1][0].skillLevel)
        assertEquals(SkillLevel.EASY, result[1][1].skillLevel)
        assertEquals(SkillLevel.EASY, result[1][2].skillLevel)
    }
} 