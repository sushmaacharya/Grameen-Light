package com.grameenlight.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EnergySavingDao {
    @Query("SELECT * FROM energy_savings WHERE month_key = :monthKey ORDER BY energy_id DESC")
    fun observeForMonth(monthKey: String): Flow<List<EnergySavingEntity>>

    @Query("SELECT * FROM energy_savings WHERE complaint_id = :complaintId LIMIT 1")
    suspend fun getByComplaintId(complaintId: String): EnergySavingEntity?

    @Insert
    suspend fun insertEnergySaving(entity: EnergySavingEntity)
}
