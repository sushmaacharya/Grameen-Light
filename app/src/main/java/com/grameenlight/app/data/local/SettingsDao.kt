package com.grameenlight.app.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE setting_key = :key LIMIT 1")
    fun observeSetting(key: String): Flow<SettingsEntity?>

    @Upsert
    suspend fun upsertSetting(entity: SettingsEntity)
}
