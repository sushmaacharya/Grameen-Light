package com.grameenlight.app

import android.app.Application

class GrameenLightApplication : Application() {
    lateinit var container: AppContainer
        private set
    private var appSyncManager: AppSyncManager? = null

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        appSyncManager = AppSyncManager(
            context = this,
            complaintRepository = container.complaintRepository,
            syncRepository = container.syncRepository
        ).also { it.start() }
    }

    override fun onTerminate() {
        appSyncManager?.stop()
        super.onTerminate()
    }
}
