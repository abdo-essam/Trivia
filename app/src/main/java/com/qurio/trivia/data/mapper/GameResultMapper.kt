package com.qurio.trivia.data.mapper

import com.qurio.trivia.data.database.entity.GameResultEntity
import com.qurio.trivia.domain.model.GameResult
import javax.inject.Inject

class GameResultMapper @Inject constructor() {

    fun toDomain(entity: GameResultEntity): GameResult {
        return GameResult(
            id = entity.id,
            date = entity.date,
            category = entity.category,
            totalQuestions = entity.totalQuestions,
            correctAnswers = entity.correctAnswers,
            incorrectAnswers = entity.incorrectAnswers,
            skippedAnswers = entity.skippedAnswers,
            stars = entity.stars,
            coins = entity.coins,
            timeTaken = entity.timeTaken,
            timestamp = entity.timestamp
        )
    }

    fun toDomainList(entities: List<GameResultEntity>): List<GameResult> {
        return entities.map { toDomain(it) }
    }
    fun toEntity(domain: GameResult): GameResultEntity {
        return GameResultEntity(
            id = domain.id,
            date = domain.date,
            category = domain.category,
            totalQuestions = domain.totalQuestions,
            correctAnswers = domain.correctAnswers,
            incorrectAnswers = domain.incorrectAnswers,
            skippedAnswers = domain.skippedAnswers,
            stars = domain.stars,
            coins = domain.coins,
            timeTaken = domain.timeTaken,
            timestamp = domain.timestamp
        )
    }
}