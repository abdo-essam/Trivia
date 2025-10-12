package com.qurio.trivia.di

import android.content.Context
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.presentation.ui.dialogs.achievement.AchievementInfoDialog
import com.qurio.trivia.presentation.ui.dialogs.achievements.AchievementsDialog
import com.qurio.trivia.presentation.ui.dialogs.buycharacter.BuyCharacterDialog
import com.qurio.trivia.presentation.ui.dialogs.buylife.BuyLifeDialog
import com.qurio.trivia.presentation.ui.dialogs.characterinfo.CharacterInfoDialog
import com.qurio.trivia.presentation.ui.dialogs.characterselection.CharacterSelectionDialog
import com.qurio.trivia.presentation.ui.dialogs.difficulty.DifficultyDialogFragment
import com.qurio.trivia.presentation.ui.dialogs.settings.SettingsDialogFragment
import com.qurio.trivia.presentation.ui.game.GameFragment
import com.qurio.trivia.presentation.ui.games.GamesFragment
import com.qurio.trivia.presentation.ui.home.HomeFragment
import com.qurio.trivia.presentation.ui.lastgames.LastGamesFragment
import com.qurio.trivia.presentation.ui.onboarding.OnboardingFragment
import com.qurio.trivia.presentation.ui.result.GameResultFragment
import com.qurio.trivia.presentation.ui.splash.SplashActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        RepositoryModule::class,
        MapperModule::class,
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    // App
    fun inject(app: QuriοApp)

    // Activities
    fun inject(activity: SplashActivity)

    // Fragments
    fun inject(fragment: OnboardingFragment)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: GameFragment)
    fun inject(fragment: GameResultFragment)
    fun inject(fragment: GamesFragment)
    fun inject(fragment: LastGamesFragment)

    // Dialogs
    fun inject(dialog: AchievementsDialog)
    fun inject(dialog: AchievementInfoDialog)
    fun inject(dialog: SettingsDialogFragment)
    fun inject(dialog: CharacterSelectionDialog)
    fun inject(dialog: CharacterInfoDialog)
    fun inject(dialog: DifficultyDialogFragment)
    fun inject(dialog: BuyLifeDialog)
    fun inject(dialog: BuyCharacterDialog)
}