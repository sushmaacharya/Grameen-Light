package com.grameenlight.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grameenlight.app.data.model.ReportStatus
import com.grameenlight.app.data.repository.ComplaintRepository
import com.grameenlight.app.data.repository.ReportResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ReportUiState(
    val isSaving: Boolean = false,
    val lastResult: ReportResult? = null,
    val errorMessage: String? = null
)

class ReportViewModel(private val complaintRepository: ComplaintRepository) : ViewModel() {
    private val mutableUiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = mutableUiState

    fun submitReport(poleId: String, status: ReportStatus, note: String?) {
        viewModelScope.launch {
            mutableUiState.value = ReportUiState(isSaving = true)
            runCatching {
                complaintRepository.submitReport(poleId, status, note)
            }.onSuccess { result ->
                mutableUiState.value = ReportUiState(lastResult = result)
            }.onFailure { error ->
                mutableUiState.value = ReportUiState(errorMessage = error.message ?: "Report failed")
            }
        }
    }

    fun clearMessage() {
        mutableUiState.value = mutableUiState.value.copy(lastResult = null, errorMessage = null)
    }
}
