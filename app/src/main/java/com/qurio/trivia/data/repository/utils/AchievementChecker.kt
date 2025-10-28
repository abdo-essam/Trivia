package com.qurio.trivia.data.repository.utils

import com.qurio.trivia.data.database.entity.GameResultEntity
import com.qurio.trivia.data.database.entity.UserProgressEntity
import com.qurio.trivia.domain.model.Achievement

/**
 * Centralized achievement criteria checker
 * Single Responsibility: Only checks if achievements should unlock
 */
object AchievementChecker {

    private const val SPEED_THRESHOLD_MS = 60000L

    /**
     * Check if achievement criteria is met
     * @return AchievementProgress with current progress and unlock status
     */
    fun checkCriteria(
        achievement: Achievement,
        gameResults: List<GameResultEntity>,
        userProgress: UserProgressEntity?
    ): AchievementProgress {
        return when (achievement) {
            Achievement.QUIZ_ROOKIE -> checkQuizRookie(gameResults)
            Achievement.STREAK_STARTER -> checkStreak(userProgress, achievement.maxProgress)
            Achievement.LUCKY_GUESS -> checkConsecutiveCorrect(gameResults, achievement.maxProgress)
            Achievement.EXPLORER -> checkCategories(gameResults, achievement.maxProgress)
            Achievement.TRIVIA_CHAMP -> checkTotalGames(gameResults, achievement.maxProgress)
            Achievement.COLLECTOR -> checkCoins(userProgress, achievement.maxProgress)
            Achievement.COLLECTOR_MASTER -> checkCoins(userProgress, achievement.maxProgress)
            Achievement.UNTOUCHABLE -> checkPerfectScores(gameResults, achievement.maxProgress)
            Achievement.QUICK_THINKER -> checkSpeedGames(gameResults)
            Achievement.LUCKY_GUESS_MASTER -> checkConsecutiveCorrect(gameResults, achievement.maxProgress)
            Achievement.LEGEND -> AchievementProgress(0, false) // Handled separately
        }
    }

    private fun checkQuizRookie(games: List<GameResultEntity>): AchievementProgress {
        val progress = games.size.coerceAtMost(1)
        return AchievementProgress(progress, games.isNotEmpty())
    }

    private fun checkStreak(userProgress: UserProgressEntity?, maxProgress: Int): AchievementProgress {
        val streak = userProgress?.currentStreak ?: 0
        return AchievementProgress(
            progress = streak.coerceAtMost(maxProgress),
            shouldUnlock = streak >= maxProgress
        )
    }

    private fun checkConsecutiveCorrect(games: List<GameResultEntity>, maxProgress: Int): AchievementProgress {
        val maxConsecutive = calculateMaxConsecutive(games)
        return AchievementProgress(
            progress = maxConsecutive.coerceAtMost(maxProgress),
            shouldUnlock = maxConsecutive >= maxProgress
        )
    }

    private fun checkCategories(games: List<GameResultEntity>, maxProgress: Int): AchievementProgress {
        val categories = games.map { it.category }.distinct().size
        return AchievementProgress(
            progress = categories.coerceAtMost(maxProgress),
            shouldUnlock = categories >= maxProgress
        )
    }

    private fun checkTotalGames(games: List<GameResultEntity>, maxProgress: Int): AchievementProgress {
        val progress = games.size.coerceAtMost(maxProgress)
        return AchievementProgress(progress, games.size >= maxProgress)
    }

    private fun checkCoins(userProgress: UserProgressEntity?, maxProgress: Int): AchievementProgress {
        val coins = userProgress?.totalCoins ?: 0
        return AchievementProgress(
            progress = coins.coerceAtMost(maxProgress),
            shouldUnlock = coins >= maxProgress
        )
    }

    private fun checkPerfectScores(games: List<GameResultEntity>, maxProgress: Int): AchievementProgress {
        val perfectScores = games.count { it.stars == 3 }
        return AchievementProgress(
            progress = perfectScores.coerceAtMost(maxProgress),
            shouldUnlock = perfectScores >= maxProgress
        )
    }

    private fun checkSpeedGames(games: List<GameResultEntity>): AchievementProgress {
        val fastGames = games.count { it.timeTaken < SPEED_THRESHOLD_MS }
        return AchievementProgress(
            progress = fastGames.coerceAtMost(1),
            shouldUnlock = fastGames > 0
        )
    }

    private fun calculateMaxConsecutive(games: List<GameResultEntity>): Int {
        if (games.isEmpty()) return 0

        var maxConsecutive = 0
        var currentConsecutive = 0

        games.sortedBy { it.timestamp }.forEach { game ->
            if (game.correctAnswers == game.totalQuestions) {
                currentConsecutive += game.correctAnswers
                maxConsecutive = maxOf(maxConsecutive, currentConsecutive)
            } else {
                currentConsecutive = 0
            }
        }

        return maxConsecutive
    }

    data class AchievementProgress(
        val progress: Int,
        val shouldUnlock: Boolean
    )
}