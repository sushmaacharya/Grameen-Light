package com.grameenlight.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.grameenlight.app.AppContainer

class PoleViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PoleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PoleViewModel(container.poleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ReportViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(container.complaintRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TrackerViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackerViewModel(container.complaintRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class EnergyViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnergyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EnergyViewModel(container.energyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class PanchayatViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PanchayatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PanchayatViewModel(
                complaintRepository = container.complaintRepository,
                aiSummaryRepository = container.aiSummaryRepository,
                settingsRepository = container.settingsRepository,
                isFirebaseConfigured = container.syncRepository.isFirebaseConfigured
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SettingsViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(
                settingsRepository = container.settingsRepository,
                complaintRepository = container.complaintRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
