package com.grameenlight.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grameenlight.app.data.model.SyncStatus
import com.grameenlight.app.data.model.ThemeMode
import com.grameenlight.app.data.repository.ComplaintRepository
import com.grameenlight.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SyncSettingsState(
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val lastSyncTime: Long? = null
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    complaintRepository: ComplaintRepository
) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.DAY)

    val syncState: StateFlow<SyncSettingsState> = complaintRepository.complaints
        .map { complaints ->
            val overallStatus = when {
                complaints.any { it.syncStatus == SyncStatus.FAILED } -> SyncStatus.FAILED
                complaints.isNotEmpty() && complaints.all { it.syncStatus == SyncStatus.SYNCED } -> SyncStatus.SYNCED
                else -> SyncStatus.PENDING
            }
            val lastSyncTime = complaints
                .filter { it.syncStatus == SyncStatus.SYNCED }
                .maxByOrNull { it.updatedAt }
                ?.updatedAt
            SyncSettingsState(
                syncStatus = overallStatus,
                lastSyncTime = lastSyncTime
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SyncSettingsState())

    fun toggleTheme() {
        viewModelScope.launch {
            settingsRepository.setThemeMode(
                if (themeMode.value == ThemeMode.DAY) ThemeMode.NIGHT else ThemeMode.DAY
            )
        }
    }
}
