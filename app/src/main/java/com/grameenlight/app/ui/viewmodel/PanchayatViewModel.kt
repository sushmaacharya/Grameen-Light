package com.grameenlight.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grameenlight.app.data.local.ComplaintEntity
import com.grameenlight.app.data.model.TrackerStatus
import com.grameenlight.app.data.repository.AiSummaryRepository
import com.grameenlight.app.data.repository.ComplaintRepository
import com.grameenlight.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PanchayatUiState(
    val isUnlocked: Boolean = false,
    val pinError: String? = null,
    val pinMessage: String? = null,
    val complaints: List<ComplaintEntity> = emptyList(),
    val prioritySummary: String = "No summary generated yet.",
    val isFirebaseConfigured: Boolean = false
)

class PanchayatViewModel(
    private val complaintRepository: ComplaintRepository,
    private val aiSummaryRepository: AiSummaryRepository,
    private val settingsRepository: SettingsRepository,
    isFirebaseConfigured: Boolean
) : ViewModel() {
    private val unlocked = MutableStateFlow(false)
    private val pinError = MutableStateFlow<String?>(null)
    private val pinMessage = MutableStateFlow<String?>(null)
    private val summary = MutableStateFlow("No summary generated yet.")
    private val firebaseConfigured = MutableStateFlow(isFirebaseConfigured)
    private val currentPin = settingsRepository.panchayatPin
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsRepository.DEFAULT_PIN)
    private val unlockState = combine(unlocked, pinError, pinMessage) { isUnlocked, error, message ->
        Triple(isUnlocked, error, message)
    }

    val uiState: StateFlow<PanchayatUiState> = combine(
        unlockState,
        complaintRepository.complaints,
        summary,
        firebaseConfigured
    ) { state, complaints, prioritySummary, firebaseReady ->
        val (isUnlocked, error, message) = state
        PanchayatUiState(
            isUnlocked = isUnlocked,
            pinError = error,
            pinMessage = message,
            complaints = complaints.filter {
                it.trackerStatus == TrackerStatus.Reported || it.trackerStatus == TrackerStatus.Assigned
            },
            prioritySummary = prioritySummary,
            isFirebaseConfigured = firebaseReady
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PanchayatUiState())

    fun unlock(pin: String) {
        if (!pin.matches(Regex("\\d{4}"))) {
            pinError.value = "PIN must be exactly 4 digits"
            pinMessage.value = null
        } else if (pin == currentPin.value) {
            unlocked.value = true
            pinError.value = null
            pinMessage.value = null
        } else {
            pinError.value = "Wrong PIN"
            pinMessage.value = null
        }
    }

    fun changePin(currentPinInput: String, newPin: String, confirmPin: String) {
        when {
            !unlocked.value -> {
                pinError.value = "Unlock Panchayat mode first"
                pinMessage.value = null
            }
            currentPinInput != currentPin.value -> {
                pinError.value = "Current PIN is wrong"
                pinMessage.value = null
            }
            !currentPinInput.matches(Regex("\\d{4}")) -> {
                pinError.value = "Current PIN must be exactly 4 digits"
                pinMessage.value = null
            }
            !newPin.matches(Regex("\\d{4}")) -> {
                pinError.value = "New PIN must be exactly 4 digits"
                pinMessage.value = null
            }
            !confirmPin.matches(Regex("\\d{4}")) -> {
                pinError.value = "Confirm PIN must be exactly 4 digits"
                pinMessage.value = null
            }
            newPin != confirmPin -> {
                pinError.value = "New PINs do not match"
                pinMessage.value = null
            }
            else -> {
                viewModelScope.launch {
                    settingsRepository.setPanchayatPin(newPin)
                    pinError.value = null
                    pinMessage.value = "Panchayat PIN updated"
                }
            }
        }
    }

    fun clearPinMessage() {
        pinMessage.value = null
    }

    fun updateStatus(complaintId: String, status: TrackerStatus) {
        viewModelScope.launch { complaintRepository.updateTrackerStatus(complaintId, status) }
    }

    fun summarizeToday() {
        summary.value = aiSummaryRepository.summarizeOpenComplaints(uiState.value.complaints)
    }
}
