package com.grameenlight.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.grameenlight.app.data.model.PoleStatus
import com.grameenlight.app.data.model.ReportStatus
import com.grameenlight.app.data.model.SyncStatus
import com.grameenlight.app.data.model.ThemeMode
import com.grameenlight.app.data.model.TrackerStatus

@Entity(tableName = "poles")
data class PoleEntity(
    @PrimaryKey
    @ColumnInfo(name = "pole_id")
    val poleId: String,
    @ColumnInfo(name = "street_name")
    val streetName: String,
    @ColumnInfo(name = "ward_no")
    val wardNo: Int,
    @ColumnInfo(name = "current_status")
    val currentStatus: PoleStatus = PoleStatus.Unknown,
    @ColumnInfo(name = "last_updated_at")
    val lastUpdatedAt: Long
)

@Entity(
    tableName = "complaints",
    foreignKeys = [
        ForeignKey(
            entity = PoleEntity::class,
            parentColumns = ["pole_id"],
            childColumns = ["pole_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pole_id")]
)
data class ComplaintEntity(
    @PrimaryKey
    @ColumnInfo(name = "complaint_id")
    val complaintId: String,
    @ColumnInfo(name = "pole_id")
    val poleId: String,
    @ColumnInfo(name = "street_name")
    val streetName: String,
    @ColumnInfo(name = "selected_status")
    val selectedStatus: ReportStatus,
    @ColumnInfo(name = "tracker_status")
    val trackerStatus: TrackerStatus,
    val note: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus
)

@Entity(
    tableName = "repair_updates",
    foreignKeys = [
        ForeignKey(
            entity = ComplaintEntity::class,
            parentColumns = ["complaint_id"],
            childColumns = ["complaint_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("complaint_id")]
)
data class RepairUpdateEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "update_id")
    val updateId: Long = 0,
    @ColumnInfo(name = "complaint_id")
    val complaintId: String,
    @ColumnInfo(name = "old_status")
    val oldStatus: TrackerStatus,
    @ColumnInfo(name = "new_status")
    val newStatus: TrackerStatus,
    @ColumnInfo(name = "updated_by")
    val updatedBy: String = "Panchayat",
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)

@Entity(
    tableName = "energy_savings",
    foreignKeys = [
        ForeignKey(
            entity = ComplaintEntity::class,
            parentColumns = ["complaint_id"],
            childColumns = ["complaint_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("complaint_id"), Index("month_key")]
)
data class EnergySavingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "energy_id")
    val energyId: Long = 0,
    @ColumnInfo(name = "complaint_id")
    val complaintId: String,
    @ColumnInfo(name = "month_key")
    val monthKey: String,
    @ColumnInfo(name = "lamp_watt")
    val lampWatt: Double = 40.0,
    @ColumnInfo(name = "hours_saved")
    val hoursSaved: Double = 5.0,
    @ColumnInfo(name = "kwh_saved")
    val kwhSaved: Double
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    @ColumnInfo(name = "setting_key")
    val settingKey: String,
    @ColumnInfo(name = "setting_value")
    val settingValue: ThemeMode,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)

@Entity(tableName = "admin_pins")
data class AdminPinEntity(
    @PrimaryKey
    @ColumnInfo(name = "pin_key")
    val pinKey: String,
    @ColumnInfo(name = "pin_value")
    val pinValue: String,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
