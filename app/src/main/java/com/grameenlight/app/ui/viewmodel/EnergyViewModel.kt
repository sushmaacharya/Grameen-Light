package com.grameenlight.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grameenlight.app.data.model.EnergySummary
import com.grameenlight.app.data.repository.EnergyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class EnergyViewModel(energyRepository: EnergyRepository) : ViewModel() {
    val summary: StateFlow<EnergySummary> = energyRepository
        .observeCurrentMonthSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EnergySummary())
}
