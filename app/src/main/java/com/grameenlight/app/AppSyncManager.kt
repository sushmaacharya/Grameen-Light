package com.grameenlight.app

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.grameenlight.app.data.repository.ComplaintRepository
import com.grameenlight.app.data.repository.SyncRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AppSyncManager(
    context: Context,
    private val complaintRepository: ComplaintRepository,
    private val syncRepository: SyncRepository
) {
    private val appContext = context.applicationContext
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val connectivityManager =
        appContext.getSystemService(ConnectivityManager::class.java)

    private var started = false
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    fun start() {
        if (started) return
        started = true

        complaintRepository.startRemoteSync(appScope)
        appScope.launch { complaintRepository.retryPendingSync() }
        registerNetworkRetry()
    }

    fun stop() {
        if (!started) return
        networkCallback?.let { callback ->
            runCatching { connectivityManager?.unregisterNetworkCallback(callback) }
        }
        networkCallback = null
        complaintRepository.stopRemoteSync()
        appScope.cancel()
        started = false
    }

    private fun registerNetworkRetry() {
        if (!syncRepository.isFirebaseConfigured || connectivityManager == null) return
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                appScope.launch { complaintRepository.retryPendingSync() }
            }
        }
        networkCallback = callback
        runCatching { connectivityManager.registerDefaultNetworkCallback(callback) }
            .onFailure { networkCallback = null }
    }
}
