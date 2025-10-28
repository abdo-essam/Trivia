package com.qurio.trivia.domain.model

data class Settings(
    val soundVolume: Float,
    val musicVolume: Float
) {
    fun isValid(): Boolean {
        return soundVolume in MIN_VOLUME..MAX_VOLUME &&
                musicVolume in MIN_VOLUME..MAX_VOLUME
    }

    companion object {
        private const val MIN_VOLUME = 0f
        private const val MAX_VOLUME = 100f

        val DEFAULT = Settings(
            soundVolume = 80f,
            musicVolume = 60f
        )
    }
}