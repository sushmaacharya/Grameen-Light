package com.grameenlight.app

import android.content.Context
import com.grameenlight.app.data.local.AppDatabase
import com.grameenlight.app.data.repository.AiSummaryRepository
import com.grameenlight.app.data.repository.ComplaintRepository
import com.grameenlight.app.data.repository.EnergyRepository
import com.grameenlight.app.data.repository.PoleRepository
import com.grameenlight.app.data.repository.SettingsRepository
import com.grameenlight.app.data.repository.SyncRepository

class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)
    val syncRepository = SyncRepository(context)
    val poleRepository = PoleRepository(database.poleDao())
    val energyRepository = EnergyRepository(database.energySavingDao())
    val complaintRepository = ComplaintRepository(
        complaintDao = database.complaintDao(),
        repairUpdateDao = database.repairUpdateDao(),
        poleRepository = poleRepository,
        energyRepository = energyRepository,
        syncRepository = syncRepository
    )
    val settingsRepository = SettingsRepository(database.settingsDao(), database.adminPinDao())
    val aiSummaryRepository = AiSummaryRepository()
}
