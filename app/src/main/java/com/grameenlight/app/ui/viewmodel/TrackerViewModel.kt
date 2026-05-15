package com.grameenlight.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grameenlight.app.data.local.ComplaintEntity
import com.grameenlight.app.data.local.RepairUpdateEntity
import com.grameenlight.app.data.model.TrackerFilter
import com.grameenlight.app.data.model.TrackerStatus
import com.grameenlight.app.data.repository.ComplaintRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class TrackerCard(
    val complaint: ComplaintEntity,
    val history: List<RepairUpdateEntity>
)

data class TrackerUiState(
    val complaints: List<TrackerCard> = emptyList(),
    val filteredComplaints: List<TrackerCard> = emptyList(),
    val filter: TrackerFilter = TrackerFilter.OPEN,
    val poleQuery: String = ""
)

class TrackerViewModel(private val complaintRepository: ComplaintRepository) : ViewModel() {
    private val filter = MutableStateFlow(TrackerFilter.OPEN)
    private val poleQuery = MutableStateFlow("")

    val uiState: StateFlow<TrackerUiState> = combine(
        complaintRepository.complaints,
        complaintRepository.repairHistory,
        filter,
        poleQuery
    ) { complaints, repairHistory, selectedFilter, query ->
        val cards = complaints.map { complaint ->
            TrackerCard(
                complaint = complaint,
                history = repairHistory[complaint.complaintId].orEmpty()
            )
        }
        val filtered = when (selectedFilter) {
            TrackerFilter.OPEN -> cards.filter {
                it.complaint.trackerStatus == TrackerStatus.Reported ||
                    it.complaint.trackerStatus == TrackerStatus.Assigned
            }
            TrackerFilter.ASSIGNED -> cards.filter { it.complaint.trackerStatus == TrackerStatus.Assigned }
            TrackerFilter.FIXED -> cards.filter {
                it.complaint.trackerStatus == TrackerStatus.Fixed || it.complaint.trackerStatus == TrackerStatus.Closed
            }
            TrackerFilter.POLE_ID -> cards.filter { it.complaint.poleId.contains(query, ignoreCase = true) }
        }
        TrackerUiState(
            complaints = cards,
            filteredComplaints = filtered,
            filter = selectedFilter,
            poleQuery = query
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TrackerUiState())

    fun setFilter(value: TrackerFilter) {
        filter.value = value
    }

    fun setPoleQuery(value: String) {
        poleQuery.value = value
    }
}
