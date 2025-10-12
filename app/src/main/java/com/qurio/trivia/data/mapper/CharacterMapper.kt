package com.qurio.trivia.data.mapper

import com.qurio.trivia.data.database.entity.CharacterEntity
import com.qurio.trivia.domain.model.Character
import javax.inject.Inject

/**
 * Maps between CharacterEntity (data layer) and Character (domain layer)
 */
class CharacterMapper @Inject constructor() {

    fun toDomain(entity: CharacterEntity, isSelected: Boolean = false): Character {
        return Character(
            name = entity.name,
            displayName = entity.displayName,
            age = entity.age,
            description = entity.description,
            imageRes = entity.imageRes,
            lockedImageRes = entity.lockedImageRes,
            unlockCost = entity.unlockCost,
            isLocked = !entity.isUnlocked,
            isSelected = isSelected
        )
    }

    fun toDomainList(
        entities: List<CharacterEntity>,
        selectedCharacterName: String?
    ): List<Character> {
        return entities.map { entity ->
            toDomain(entity, isSelected = entity.name == selectedCharacterName)
        }
    }

    fun toEntity(domain: Character): CharacterEntity {
        return CharacterEntity(
            name = domain.name,
            displayName = domain.displayName,
            age = domain.age,
            description = domain.description,
            imageRes = domain.imageRes,
            lockedImageRes = domain.lockedImageRes,
            unlockCost = domain.unlockCost,
            isUnlocked = !domain.isLocked,
            unlockedAt = if (!domain.isLocked) System.currentTimeMillis() else null
        )
    }
}