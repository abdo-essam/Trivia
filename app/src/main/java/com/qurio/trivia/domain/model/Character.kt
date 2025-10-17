package com.qurio.trivia.domain.model

enum class Character(
    val characterName: String,
    val displayName: String,
    val age: String,
    val description: String,
    val unlockCost: Int
) {
    RIKA(
        characterName = "rika",
        displayName = "Rika",
        age = "Age: 12 Sunblooms",
        description = "Nature's little explorer! Rika talks to mushrooms and swears squirrels give her battle advice. Always ready for an adventure.",
        unlockCost = 0
    ),

    KAIYO(
        characterName = "kaiyo",
        displayName = "Kaiyo",
        age = "Age: 14 Storms",
        description = "A calm storm in human form. Kaiyo trains with ancient scrolls by day and drinks spicy tea by night. Sword sharp, heart sharper.",
        unlockCost = 300
    ),

    MIMI(
        characterName = "mimi",
        displayName = "Mimi",
        age = "Age: 10 Volcano Puffs",
        description = "Tiny but terrifying! Mimi is always grumpy, but don't let that scare you—unless you like pranks involving firecrackers.",
        unlockCost = 700
    ),

    YORU(
        characterName = "yoru",
        displayName = "Yoru",
        age = "Age: 13 Shadows",
        description = "Quiet, mysterious, and probably watching you right now. Yoru shows up when you least expect it.",
        unlockCost = 1000
    ),

    KURO(
        characterName = "kuro",
        displayName = "Kuro",
        age = "Age: 15 Thunder Beats",
        description = "Cool jacket, cooler moves. Kuro never backs down from a challenge.",
        unlockCost = 3000
    ),

    MIKO(
        characterName = "miko",
        displayName = "Miko",
        age = "Age: 11 Leaf Turns",
        description = "Energetic, cheerful, and faster than a leaf in the wind. Miko can turn any trivia into a giggle-fest.",
        unlockCost = 7000
    ),

    AORI(
        characterName = "aori",
        displayName = "Aori",
        age = "Age: 13 Blade Echoes",
        description = "The sword chooses the wielder—and it chose Aori. Calm, focused.",
        unlockCost = 12000
    ),

    NARA(
        characterName = "nara",
        displayName = "Nara",
        age = "Age: 12 Crystal Songs",
        description = "Part magic, part sass. Nara sparkles even when she's mad.",
        unlockCost = 30000
    ),

    RENJI(
        characterName = "renji",
        displayName = "Renji",
        age = "Age: 11 Hero Coins",
        description = "Small but mighty! Renji dreams of glory, carries a shield too big for him.",
        unlockCost = 50000
    );

    /**
     * Check if this character is free (unlocked by default)
     */
    fun isFree(): Boolean = unlockCost == 0

    /**
     * Check if user can unlock this character
     */
    fun canBeUnlockedWith(userCoins: Int): Boolean = userCoins >= unlockCost

    companion object {
        /**
         * Find character by name (case-insensitive)
         */
        fun fromName(name: String): Character? {
            return entries.find {
                it.characterName.equals(name, ignoreCase = true) ||
                        it.displayName.equals(name, ignoreCase = true)
            }
        }

        /**
         * Get default character (Rika)
         */
        fun default(): Character = RIKA

        /**
         * Get all characters as list
         */
        fun all(): List<Character> = entries

        /**
         * Get free characters
         */
        fun free(): List<Character> = entries.filter { it.isFree() }

        /**
         * Get locked characters
         */
        fun locked(): List<Character> = entries.filter { !it.isFree() }
    }
}