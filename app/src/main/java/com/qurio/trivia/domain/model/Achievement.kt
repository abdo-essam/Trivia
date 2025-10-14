package com.qurio.trivia.domain.model

enum class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val howToGet: String,
    val maxProgress: Int
) {
    QUIZ_ROOKIE(
        id = "quiz_rookie",
        title = "Quiz Rookie",
        description = "Complete your first quiz",
        howToGet = "Play and complete any quiz game.",
        maxProgress = 1
    ),

    STREAK_STARTER(
        id = "streak_starter",
        title = "Streak Starter",
        description = "Maintain a 3-day streak",
        howToGet = "Play at least one quiz every day for 3 days.",
        maxProgress = 3
    ),

    LUCKY_GUESS(
        id = "lucky_guess",
        title = "Lucky Guess",
        description = "Answer 5 questions correctly in a row",
        howToGet = "Keep your winning streak going without mistakes.",
        maxProgress = 5
    ),

    EXPLORER(
        id = "explorer",
        title = "Explorer",
        description = "Play 5 different categories",
        howToGet = "Complete quizzes from 5 different categories.",
        maxProgress = 5
    ),

    TRIVIA_CHAMP(
        id = "trivia_champ",
        title = "Trivia Champ",
        description = "Complete 50 quizzes",
        howToGet = "Play and complete 50 quiz games.",
        maxProgress = 50
    ),

    COLLECTOR(
        id = "collector",
        title = "Collector",
        description = "Earn 1000 coins",
        howToGet = "Play quizzes and accumulate 1000 coins.",
        maxProgress = 1000
    ),

    LEGEND(
        id = "legend",
        title = "Legend",
        description = "Unlock all other achievements",
        howToGet = "Complete all other achievements to become a legend.",
        maxProgress = 10
    ),

    UNTOUCHABLE(
        id = "untouchable",
        title = "Untouchable",
        description = "Get 10 perfect scores",
        howToGet = "Answer all questions correctly in 10 different quizzes.",
        maxProgress = 10
    ),

    QUICK_THINKER(
        id = "quick_thinker",
        title = "Quick Thinker",
        description = "Complete a quiz in under 1 minute",
        howToGet = "Answer all questions quickly and correctly.",
        maxProgress = 1
    ),

    COLLECTOR_MASTER(
        id = "collector_master",
        title = "Collector",
        description = "Earn 5000 coins",
        howToGet = "Play quizzes and accumulate 5000 coins.",
        maxProgress = 5000
    ),

    LUCKY_GUESS_MASTER(
        id = "lucky_guess_master",
        title = "Lucky Guess",
        description = "Answer 10 questions correctly in a row",
        howToGet = "Keep your winning streak going without mistakes.",
        maxProgress = 10
    );

    companion object {
        fun fromId(id: String): Achievement? = Achievement.entries.find { it.id == id }
    }
}