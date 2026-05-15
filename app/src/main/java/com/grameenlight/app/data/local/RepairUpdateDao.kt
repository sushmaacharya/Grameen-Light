package com.grameenlight.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairUpdateDao {
    @Insert
    suspend fun insertRepairUpdate(update: RepairUpdateEntity)

    @Query("SELECT * FROM repair_updates WHERE complaint_id = :complaintId ORDER BY updated_at DESC")
    fun observeUpdatesForComplaint(complaintId: String): Flow<List<RepairUpdateEntity>>

    @Query("SELECT * FROM repair_updates ORDER BY updated_at DESC")
    fun observeAllUpdates(): Flow<List<RepairUpdateEntity>>
}
