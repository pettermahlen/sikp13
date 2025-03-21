package com.pettermahlen.sikp13

import androidx.compose.ui.graphics.Color

enum class SkillLevel {
    NEW {
        override val displayName = "Ny"
        override val color = Color(0xFF4CAF50) // Green
    },
    EASY {
        override val displayName = "Lätt"
        override val color = Color(0xFF2196F3) // Blue
    },
    MEDIUM {
        override val displayName = "Medel"
        override val color = Color(0xFFF44336) // Red
    },
    HARD {
        override val displayName = "Svår"
        override val color = Color(0xFF000000) // Black
    };

    abstract val displayName: String
    abstract val color: Color
}

data class Player(
    val name: String,
    val skillLevel: SkillLevel
) 