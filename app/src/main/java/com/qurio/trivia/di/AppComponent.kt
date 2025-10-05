package com.qurio.trivia.di

import android.content.Context
import com.qurio.trivia.MainActivity
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.ui.achievements.AchievementsFragment
import com.qurio.trivia.ui.buylife.BuyLifeFragment
import com.qurio.trivia.ui.character.CharacterSelectionFragment
import com.qurio.trivia.ui.dialogs.SettingsDialogFragment
import com.qurio.trivia.ui.difficulty.DifficultyFragment
import com.qurio.trivia.ui.game.GameFragment
import com.qurio.trivia.ui.games.GamesFragment
import com.qurio.trivia.ui.home.HomeFragment
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

    fun inject(app: QuriοApp)
    fun inject(fragment: LoadingFragment)
    fun inject(fragment: OnboardingFragment)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: CharacterSelectionFragment)
    fun inject(fragment: DifficultyFragment)
    fun inject(fragment: GameFragment)
    fun inject(fragment: GameResultFragment)
    fun inject(fragment: BuyLifeFragment)
    fun inject(fragment: AchievementsFragment)
    fun inject(dialog: SettingsDialogFragment)
    fun inject(fragment: GamesFragment)
}