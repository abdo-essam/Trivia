package com.qurio.trivia.utils.sound

import androidx.fragment.app.Fragment
import com.qurio.trivia.QurioApp
import com.qurio.trivia.utils.Constants

fun Fragment.getSoundManager(): SoundManager? {
    return (requireActivity().application as? QurioApp)?.soundManager
}

fun Fragment.playSound(soundId: Int) {
    getSoundManager()?.playSound(soundId)
}

fun Fragment.playDialogOpenSound() {
    playSound(Constants.Sound.SOUND_DIALOG_OPEN)
}

fun Fragment.playDialogCloseSound() {
    playSound(Constants.Sound.SOUND_DIALOG_CLOSE)
}

fun Fragment.playCorrectSound() {
    playSound(Constants.Sound.SOUND_CORRECT)
}

fun Fragment.playWrongSound() {
    playSound(Constants.Sound.SOUND_WRONG)
}

fun Fragment.playCoinsSound() {
    playSound(Constants.Sound.SOUND_COINS)
}

fun Fragment.startClockTicking() {
    getSoundManager()?.playLoopingSound(Constants.Sound.SOUND_CLOCK_TICKING)
}

fun Fragment.stopClockTicking() {
    getSoundManager()?.stopLoopingSound()
}