package com.grameenlight.app.data.model

enum class PoleStatus(val label: String) {
    Working("Working"),
    Fused("Fused"),
    BurningInDay("Burning in Day"),
    Assigned("Repair Assigned"),
    Unknown("Unknown")
}

enum class ReportStatus(val label: String) {
    Working("Working"),
    Fused("Fused"),
    BurningInDay("Burning in Day")
}

enum class TrackerStatus(val label: String) {
    Reported("Reported"),
    Assigned("Assigned"),
    Fixed("Fixed"),
    Closed("Closed")
}

enum class SyncStatus(val label: String) {
    PENDING("Pending"),
    SYNCED("Synced"),
    FAILED("Failed")
}

enum class ThemeMode(val label: String) {
    DAY("Day Audit"),
    NIGHT("Night Audit")
}

enum class TrackerFilter(val label: String) {
    OPEN("Open"),
    ASSIGNED("Assigned"),
    FIXED("Fixed"),
    POLE_ID("Pole ID")
}

data class EnergySummary(
    val kwhSaved: Double = 0.0,
    val inrSaved: Double = 0.0,
    val fixedDayBurningCount: Int = 0
)
