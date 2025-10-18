package com.qurio.trivia.utils.sound

import androidx.fragment.app.Fragment
import com.qurio.trivia.QuriοApp

fun Fragment.getSoundManager(): SoundManager? {
    return (requireActivity().application as? QuriοApp)?.soundManager
}

fun Fragment.playSound(soundId: Int) {
    getSoundManager()?.playSound(soundId)
}

fun Fragment.playBackgroundMusic(resId: Int, loop: Boolean = true) {
    getSoundManager()?.playBackgroundMusic(resId, loop)
}