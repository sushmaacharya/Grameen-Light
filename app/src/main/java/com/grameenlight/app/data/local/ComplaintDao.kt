package com.grameenlight.app.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.grameenlight.app.data.model.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplaintDao {
    @Query("SELECT * FROM complaints ORDER BY updated_at DESC")
    fun observeComplaints(): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE complaint_id = :complaintId LIMIT 1")
    suspend fun getComplaint(complaintId: String): ComplaintEntity?

    @Query("SELECT * FROM complaints WHERE sync_status != 'SYNCED'")
    suspend fun getPendingSync(): List<ComplaintEntity>

    @Query("SELECT COUNT(*) FROM complaints WHERE complaint_id LIKE :datePrefix")
    suspend fun countForDate(datePrefix: String): Int

    @Upsert
    suspend fun upsertComplaint(complaint: ComplaintEntity)

    @Query("UPDATE complaints SET sync_status = :syncStatus, updated_at = :updatedAt WHERE complaint_id = :complaintId")
    suspend fun updateSyncStatus(complaintId: String, syncStatus: SyncStatus, updatedAt: Long)
}
