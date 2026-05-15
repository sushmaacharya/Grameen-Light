package com.grameenlight.app.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.grameenlight.app.data.local.ComplaintEntity
import com.grameenlight.app.data.local.PoleEntity
import com.grameenlight.app.data.model.PoleStatus
import com.grameenlight.app.data.model.ReportStatus
import com.grameenlight.app.data.model.SyncStatus
import com.grameenlight.app.data.model.TrackerStatus
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class SyncRepository(private val context: Context) {
    private val connectivityManager: ConnectivityManager? =
        context.applicationContext.getSystemService(ConnectivityManager::class.java)

    private val firestore: FirebaseFirestore?
        get() = try {
            if (FirebaseApp.getApps(context).isEmpty()) null else FirebaseFirestore.getInstance()
        } catch (_: IllegalStateException) {
            null
        }

    val isFirebaseConfigured: Boolean
        get() = firestore != null

    suspend fun syncComplaint(complaint: ComplaintEntity, pole: PoleEntity?): SyncStatus {
        val db = firestore ?: return SyncStatus.PENDING
        if (!isNetworkAvailable()) return SyncStatus.PENDING
        val complaintData = mapOf(
            "complaint_id" to complaint.complaintId,
            "pole_id" to complaint.poleId,
            "street_name" to complaint.streetName,
            "selected_status" to complaint.selectedStatus.name,
            "tracker_status" to complaint.trackerStatus.name,
            "note" to complaint.note.orEmpty(),
            "created_at" to complaint.createdAt,
            "updated_at" to complaint.updatedAt
        )
        val poleData = pole?.let {
            mapOf(
                "pole_id" to it.poleId,
                "street_name" to it.streetName,
                "ward_no" to it.wardNo,
                "current_status" to it.currentStatus.name,
                "last_updated_at" to it.lastUpdatedAt
            )
        }
        return suspendCancellableCoroutine { continuation ->
            val batch = db.batch()
            batch.set(db.collection("complaints").document(complaint.complaintId), complaintData)
            if (poleData != null) {
                batch.set(db.collection("poles").document(complaint.poleId), poleData)
            }
            batch.commit()
                .addOnSuccessListener {
                    if (continuation.isActive) continuation.resume(SyncStatus.SYNCED)
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) continuation.resume(error.toSyncStatus())
                }
        }
    }

    fun listenForRemoteChanges(
        onComplaint: (ComplaintEntity) -> Unit,
        onPole: (PoleEntity) -> Unit
    ): List<ListenerRegistration> {
        val db = firestore ?: return emptyList()
        val complaints = db.collection("complaints").addSnapshotListener { snapshot, _ ->
            snapshot?.documents?.mapNotNull { it.toComplaintEntity() }?.forEach(onComplaint)
        }
        val poles = db.collection("poles").addSnapshotListener { snapshot, _ ->
            snapshot?.documents?.mapNotNull { it.toPoleEntity() }?.forEach(onPole)
        }
        return listOf(complaints, poles)
    }

    private fun DocumentSnapshot.toComplaintEntity(): ComplaintEntity? {
        val poleId = getString("pole_id") ?: return null
        val selectedStatus = enumValue<ReportStatus>(getString("selected_status")) ?: return null
        val trackerStatus = enumValue<TrackerStatus>(getString("tracker_status")) ?: return null
        val updatedAt = getLong("updated_at") ?: return null
        return ComplaintEntity(
            complaintId = getString("complaint_id") ?: id,
            poleId = poleId,
            streetName = getString("street_name").orEmpty(),
            selectedStatus = selectedStatus,
            trackerStatus = trackerStatus,
            note = getString("note")?.ifBlank { null },
            createdAt = getLong("created_at") ?: updatedAt,
            updatedAt = updatedAt,
            syncStatus = SyncStatus.SYNCED
        )
    }

    private fun DocumentSnapshot.toPoleEntity(): PoleEntity? {
        return PoleEntity(
            poleId = getString("pole_id") ?: id,
            streetName = getString("street_name").orEmpty(),
            wardNo = getLong("ward_no")?.toInt() ?: 0,
            currentStatus = enumValue<PoleStatus>(getString("current_status")) ?: PoleStatus.Unknown,
            lastUpdatedAt = getLong("last_updated_at") ?: System.currentTimeMillis()
        )
    }

    private inline fun <reified T : Enum<T>> enumValue(name: String?): T? =
        runCatching { enumValueOf<T>(name.orEmpty()) }.getOrNull()

    private fun isNetworkAvailable(): Boolean {
        val activeNetwork = connectivityManager?.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun Throwable.toSyncStatus(): SyncStatus {
        val firestoreCode = (this as? FirebaseFirestoreException)?.code
        return when (firestoreCode) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED,
            FirebaseFirestoreException.Code.FAILED_PRECONDITION -> SyncStatus.FAILED
            else -> SyncStatus.PENDING
        }
    }
}
