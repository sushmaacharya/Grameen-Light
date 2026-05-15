package com.grameenlight.app.data.repository

import com.grameenlight.app.data.local.AdminPinDao
import com.grameenlight.app.data.local.AdminPinEntity
import com.grameenlight.app.data.local.SettingsDao
import com.grameenlight.app.data.local.SettingsEntity
import com.grameenlight.app.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val settingsDao: SettingsDao,
    private val adminPinDao: AdminPinDao
) {
    val themeMode: Flow<ThemeMode> = settingsDao.observeSetting(THEME_MODE_KEY).map {
        it?.settingValue ?: ThemeMode.DAY
    }

    val panchayatPin: Flow<String> = adminPinDao.observePin(PANCHAYAT_PIN_KEY).map {
        it?.pinValue?.takeIf(::isValidPin) ?: DEFAULT_PIN
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        settingsDao.upsertSetting(
            SettingsEntity(
                settingKey = THEME_MODE_KEY,
                settingValue = mode,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun setPanchayatPin(pin: String) {
        require(isValidPin(pin)) { "Panchayat PIN must be exactly 4 digits" }
        adminPinDao.upsertPin(
            AdminPinEntity(
                pinKey = PANCHAYAT_PIN_KEY,
                pinValue = pin,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    companion object {
        const val DEFAULT_PIN = "1234"
        private const val THEME_MODE_KEY = "theme_mode"
        private const val PANCHAYAT_PIN_KEY = "panchayat_pin"

        private fun isValidPin(pin: String): Boolean = pin.matches(Regex("\\d{4}"))
    }
}
