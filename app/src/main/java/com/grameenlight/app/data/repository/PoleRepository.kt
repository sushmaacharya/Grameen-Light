package com.grameenlight.app.data.repository

import com.grameenlight.app.data.local.PoleDao
import com.grameenlight.app.data.local.PoleEntity
import com.grameenlight.app.data.local.SeedData
import com.grameenlight.app.data.model.PoleStatus
import kotlinx.coroutines.flow.Flow

class PoleRepository(private val poleDao: PoleDao) {
    val poles: Flow<List<PoleEntity>> = poleDao.observePoles()

    suspend fun ensureSeeded() {
        if (poleDao.countPoles() == 0) {
            poleDao.upsertPoles(SeedData.villagePoles(System.currentTimeMillis()))
        }
    }

    suspend fun getPole(poleId: String): PoleEntity? = poleDao.getPole(poleId)

    suspend fun upsertRemotePole(pole: PoleEntity) {
        val local = poleDao.getPole(pole.poleId)
        if (local == null || pole.lastUpdatedAt >= local.lastUpdatedAt) {
            poleDao.upsertPole(pole)
        }
    }

    suspend fun updatePoleStatus(poleId: String, status: PoleStatus, updatedAt: Long) {
        poleDao.updateStatus(poleId, status, updatedAt)
    }
}
