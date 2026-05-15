package com.grameenlight.app.data.repository

import com.grameenlight.app.data.local.EnergySavingDao
import com.grameenlight.app.data.local.EnergySavingEntity
import com.grameenlight.app.data.model.EnergySummary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EnergyRepository(
    private val energySavingDao: EnergySavingDao,
    private val costPerKwh: Double = 8.0
) {
    fun observeCurrentMonthSummary(): Flow<EnergySummary> {
        val monthKey = currentMonthKey()
        return energySavingDao.observeForMonth(monthKey).map { rows ->
            val kwh = rows.sumOf { it.kwhSaved }
            EnergySummary(
                kwhSaved = kwh,
                inrSaved = kwh * costPerKwh,
                fixedDayBurningCount = rows.size
            )
        }
    }

    suspend fun recordDayBurningFixIfNeeded(
        complaintId: String,
        fixedAt: Long,
        lampWatt: Double = 40.0,
        hoursSaved: Double = 5.0
    ) {
        if (energySavingDao.getByComplaintId(complaintId) != null) return
        energySavingDao.insertEnergySaving(
            EnergySavingEntity(
                complaintId = complaintId,
                monthKey = monthKey(fixedAt),
                lampWatt = lampWatt,
                hoursSaved = hoursSaved,
                kwhSaved = hoursSaved * lampWatt / 1000.0
            )
        )
    }

    private fun currentMonthKey(): String = monthKey(System.currentTimeMillis())

    private fun monthKey(timestamp: Long): String {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(MONTH_FORMAT)
    }

    companion object {
        private val MONTH_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
    }
}
