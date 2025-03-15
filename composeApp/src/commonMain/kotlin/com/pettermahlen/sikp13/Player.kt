package com.pettermahlen.sikp13

enum class SkillLevel {
    EASY,
    MEDIUM,
    HARD
}

data class Player(
    val name: String,
    val skillLevel: SkillLevel
) 