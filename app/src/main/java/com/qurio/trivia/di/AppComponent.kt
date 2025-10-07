package com.qurio.trivia.di

import android.content.Context
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.ui.dialogs.AchievementInfoDialog
import com.qurio.trivia.ui.dialogs.AchievementsDialog
import com.qurio.trivia.ui.dialogs.BuyLifeDialog
import com.qurio.trivia.ui.dialogs.CharacterInfoDialog
import com.qurio.trivia.ui.dialogs.CharacterSelectionDialog
import com.qurio.trivia.ui.dialogs.DifficultyDialogFragment
import com.qurio.trivia.ui.dialogs.SettingsDialogFragment
import com.qurio.trivia.ui.game.GameFragment
import com.qurio.trivia.ui.games.GamesFragment
import com.qurio.trivia.ui.home.HomeFragment
import com.qurio.trivia.ui.lastgames.LastGamesFragment
import com.qurio.trivia.ui.loading.LoadingFragment
import com.qurio.trivia.ui.onboarding.OnboardingFragment
import com.qurio.trivia.ui.result.GameResultFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    // App
    fun inject(app: QuriοApp)

    // Fragments
    fun inject(fragment: LoadingFragment)
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
}