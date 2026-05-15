package com.grameenlight.app.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminPinDao {
    @Query("SELECT * FROM admin_pins WHERE pin_key = :key LIMIT 1")
    fun observePin(key: String): Flow<AdminPinEntity?>

    @Upsert
    suspend fun upsertPin(entity: AdminPinEntity)
}
