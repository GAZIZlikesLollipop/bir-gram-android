package org.lolli.birgram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import org.lolli.birgram.data.TDLibRepository
import org.lolli.birgram.data.UserPreferencesRepository
import org.lolli.birgram.presentation.TGViewModel
import org.lolli.birgram.presentation.TGViewModelFactory
import org.lolli.birgram.presentation.theme.BirGramTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferencesRepository = UserPreferencesRepository(this)
        val tdLibRepository = TDLibRepository(this)
        enableEdgeToEdge()
        setContent {
            val tgViewModel = viewModel<TGViewModel>(factory = TGViewModelFactory(userPreferencesRepository,tdLibRepository))
            val isDarkTheme by tgViewModel.isDarkTheme.collectAsState()
            BirGramTheme(isDarkTheme) {
                App(tgViewModel)
            }
        }
    }
}