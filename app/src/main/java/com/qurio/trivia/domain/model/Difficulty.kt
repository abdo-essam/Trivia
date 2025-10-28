package com.qurio.trivia.domain.model


enum class Difficulty(val value: String, val displayName: String) {
    EASY("easy", "Easy"),
    MEDIUM("medium", "Medium"),
    HARD("hard", "Hard")
}