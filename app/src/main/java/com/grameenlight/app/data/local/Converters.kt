package com.grameenlight.app.data.local

import androidx.room.TypeConverter
import com.grameenlight.app.data.model.PoleStatus
import com.grameenlight.app.data.model.ReportStatus
import com.grameenlight.app.data.model.SyncStatus
import com.grameenlight.app.data.model.ThemeMode
import com.grameenlight.app.data.model.TrackerStatus

class Converters {
    @TypeConverter
    fun fromPoleStatus(value: PoleStatus): String = value.name

    @TypeConverter
    fun toPoleStatus(value: String): PoleStatus = enumValueOf(value)

    @TypeConverter
    fun fromReportStatus(value: ReportStatus): String = value.name

    @TypeConverter
    fun toReportStatus(value: String): ReportStatus = enumValueOf(value)

    @TypeConverter
    fun fromTrackerStatus(value: TrackerStatus): String = value.name

    @TypeConverter
    fun toTrackerStatus(value: String): TrackerStatus = enumValueOf(value)

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = enumValueOf(value)

    @TypeConverter
    fun fromThemeMode(value: ThemeMode): String = value.name

    @TypeConverter
    fun toThemeMode(value: String): ThemeMode = enumValueOf(value)
}
