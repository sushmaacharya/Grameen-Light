package com.grameenlight.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.grameenlight.app.data.model.ThemeMode
import com.grameenlight.app.ui.screens.GrameenLightApp
import com.grameenlight.app.ui.theme.GrameenLightTheme
import com.grameenlight.app.ui.viewmodel.EnergyViewModel
import com.grameenlight.app.ui.viewmodel.EnergyViewModelFactory
import com.grameenlight.app.ui.viewmodel.PanchayatViewModel
import com.grameenlight.app.ui.viewmodel.PanchayatViewModelFactory
import com.grameenlight.app.ui.viewmodel.PoleViewModel
import com.grameenlight.app.ui.viewmodel.PoleViewModelFactory
import com.grameenlight.app.ui.viewmodel.ReportViewModel
import com.grameenlight.app.ui.viewmodel.ReportViewModelFactory
import com.grameenlight.app.ui.viewmodel.SettingsViewModel
import com.grameenlight.app.ui.viewmodel.SettingsViewModelFactory
import com.grameenlight.app.ui.viewmodel.TrackerViewModel
import com.grameenlight.app.ui.viewmodel.TrackerViewModelFactory

class MainActivity : ComponentActivity() {
    private val container: AppContainer
        get() = (application as GrameenLightApplication).container

    private val poleViewModel: PoleViewModel by viewModels { PoleViewModelFactory(container) }
    private val reportViewModel: ReportViewModel by viewModels { ReportViewModelFactory(container) }
    private val trackerViewModel: TrackerViewModel by viewModels { TrackerViewModelFactory(container) }
    private val energyViewModel: EnergyViewModel by viewModels { EnergyViewModelFactory(container) }
    private val panchayatViewModel: PanchayatViewModel by viewModels { PanchayatViewModelFactory(container) }
    private val settingsViewModel: SettingsViewModel by viewModels { SettingsViewModelFactory(container) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsState()
            GrameenLightTheme(darkTheme = themeMode == ThemeMode.NIGHT) {
                GrameenLightApp(
                    poleViewModel = poleViewModel,
                    reportViewModel = reportViewModel,
                    trackerViewModel = trackerViewModel,
                    energyViewModel = energyViewModel,
                    panchayatViewModel = panchayatViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
