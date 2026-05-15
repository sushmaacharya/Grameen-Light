package com.grameenlight.app.data.repository

import com.google.firebase.firestore.ListenerRegistration
import com.grameenlight.app.data.local.ComplaintDao
import com.grameenlight.app.data.local.ComplaintEntity
import com.grameenlight.app.data.local.RepairUpdateDao
import com.grameenlight.app.data.local.RepairUpdateEntity
import com.grameenlight.app.data.model.PoleStatus
import com.grameenlight.app.data.model.ReportStatus
import com.grameenlight.app.data.model.SyncStatus
import com.grameenlight.app.data.model.TrackerStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class ReportResult(
    val complaintId: String,
    val syncStatus: SyncStatus
)

class ComplaintRepository(
    private val complaintDao: ComplaintDao,
    private val repairUpdateDao: RepairUpdateDao,
    private val poleRepository: PoleRepository,
    private val energyRepository: EnergyRepository,
    private val syncRepository: SyncRepository
) {
    val complaints: Flow<List<ComplaintEntity>> = complaintDao.observeComplaints()
    val repairHistory: Flow<Map<String, List<RepairUpdateEntity>>> =
        repairUpdateDao.observeAllUpdates().map { updates -> updates.groupBy { it.complaintId } }
    private var remoteListeners: List<ListenerRegistration> = emptyList()
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun submitReport(
        poleId: String,
        selectedStatus: ReportStatus,
        note: String?
    ): ReportResult {
        val now = System.currentTimeMillis()
        val pole = requireNotNull(poleRepository.getPole(poleId)) { "Pole not found: $poleId" }
        val complaint = ComplaintEntity(
            complaintId = generateComplaintId(now),
            poleId = poleId,
            streetName = pole.streetName,
            selectedStatus = selectedStatus,
            trackerStatus = if (selectedStatus == ReportStatus.Working) TrackerStatus.Closed else TrackerStatus.Reported,
            note = note?.take(150)?.ifBlank { null },
            createdAt = now,
            updatedAt = now,
            syncStatus = SyncStatus.PENDING
        )
        complaintDao.upsertComplaint(complaint)
        poleRepository.updatePoleStatus(poleId, selectedStatus.toPoleStatus(), now)
        queueSync(complaint.complaintId)
        return ReportResult(complaint.complaintId, SyncStatus.PENDING)
    }

    suspend fun updateTrackerStatus(complaintId: String, newStatus: TrackerStatus) {
        val now = System.currentTimeMillis()
        val existing = complaintDao.getComplaint(complaintId) ?: return
        if (existing.trackerStatus == newStatus) return
        val updated = existing.copy(
            trackerStatus = newStatus,
            updatedAt = now,
            syncStatus = SyncStatus.PENDING
        )
        complaintDao.upsertComplaint(updated)
        repairUpdateDao.insertRepairUpdate(
            RepairUpdateEntity(
                complaintId = complaintId,
                oldStatus = existing.trackerStatus,
                newStatus = newStatus,
                updatedAt = now
            )
        )
        when (newStatus) {
            TrackerStatus.Assigned -> poleRepository.updatePoleStatus(existing.poleId, PoleStatus.Assigned, now)
            TrackerStatus.Fixed -> {
                poleRepository.updatePoleStatus(existing.poleId, PoleStatus.Working, now)
                if (existing.selectedStatus == ReportStatus.BurningInDay) {
                    energyRepository.recordDayBurningFixIfNeeded(complaintId, now)
                }
            }
            TrackerStatus.Closed -> poleRepository.updatePoleStatus(existing.poleId, PoleStatus.Working, now)
            TrackerStatus.Reported -> poleRepository.updatePoleStatus(existing.poleId, existing.selectedStatus.toPoleStatus(), now)
        }
        queueSync(complaintId)
    }

    suspend fun retryPendingSync() {
        complaintDao.getPendingSync().forEach { complaint ->
            syncComplaintById(complaint.complaintId)
        }
    }

    fun startRemoteSync(scope: CoroutineScope) {
        if (remoteListeners.isNotEmpty()) return
        remoteListeners = syncRepository.listenForRemoteChanges(
            onComplaint = { remote -> scope.launch { applyRemoteComplaint(remote) } },
            onPole = { remote -> scope.launch { poleRepository.upsertRemotePole(remote) } }
        )
    }

    fun stopRemoteSync() {
        remoteListeners.forEach { it.remove() }
        remoteListeners = emptyList()
    }

    private fun queueSync(complaintId: String) {
        syncScope.launch {
            syncComplaintById(complaintId)
        }
    }

    private suspend fun syncComplaintById(complaintId: String) {
        val complaint = complaintDao.getComplaint(complaintId) ?: return
        val pole = poleRepository.getPole(complaint.poleId)
        val syncStatus = syncRepository.syncComplaint(complaint, pole)
        complaintDao.updateSyncStatus(complaintId, syncStatus, System.currentTimeMillis())
    }

    private suspend fun applyRemoteComplaint(remote: ComplaintEntity) {
        val local = complaintDao.getComplaint(remote.complaintId)
        if (local != null && local.updatedAt > remote.updatedAt) return
        complaintDao.upsertComplaint(remote)
        if (remote.trackerStatus == TrackerStatus.Fixed && remote.selectedStatus == ReportStatus.BurningInDay) {
            energyRepository.recordDayBurningFixIfNeeded(remote.complaintId, remote.updatedAt)
        }
    }

    private suspend fun generateComplaintId(now: Long): String {
        val dateKey = Instant.ofEpochMilli(now)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(DATE_FORMAT)
        val prefix = "GL-$dateKey-"
        val nextNumber = complaintDao.countForDate("$prefix%") + 1
        return "$prefix${nextNumber.toString().padStart(3, '0')}"
    }

    private fun ReportStatus.toPoleStatus(): PoleStatus = when (this) {
        ReportStatus.Working -> PoleStatus.Working
        ReportStatus.Fused -> PoleStatus.Fused
        ReportStatus.BurningInDay -> PoleStatus.BurningInDay
    }

    companion object {
        private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    }
}
