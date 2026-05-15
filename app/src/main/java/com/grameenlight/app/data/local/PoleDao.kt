package com.grameenlight.app.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.grameenlight.app.data.model.PoleStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PoleDao {
    @Query("SELECT * FROM poles ORDER BY ward_no, street_name, pole_id")
    fun observePoles(): Flow<List<PoleEntity>>

    @Query("SELECT * FROM poles WHERE pole_id = :poleId LIMIT 1")
    suspend fun getPole(poleId: String): PoleEntity?

    @Query("SELECT COUNT(*) FROM poles")
    suspend fun countPoles(): Int

    @Upsert
    suspend fun upsertPoles(poles: List<PoleEntity>)

    @Upsert
    suspend fun upsertPole(pole: PoleEntity)

    @Query("UPDATE poles SET current_status = :status, last_updated_at = :updatedAt WHERE pole_id = :poleId")
    suspend fun updateStatus(poleId: String, status: PoleStatus, updatedAt: Long)
}
