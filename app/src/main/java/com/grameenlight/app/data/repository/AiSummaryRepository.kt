package com.grameenlight.app.data.repository

import com.grameenlight.app.data.local.ComplaintEntity
import com.grameenlight.app.data.model.ReportStatus
import com.grameenlight.app.data.model.TrackerStatus

class AiSummaryRepository {
    fun summarizeOpenComplaints(complaints: List<ComplaintEntity>): String {
        val open = complaints.filter {
            it.trackerStatus == TrackerStatus.Reported || it.trackerStatus == TrackerStatus.Assigned
        }
        if (open.isEmpty()) return "No open complaints today."

        val topStreet = open.groupingBy { it.streetName }.eachCount().maxByOrNull { it.value }
        val fusedCount = open.count { it.selectedStatus == ReportStatus.Fused }
        val burningCount = open.count { it.selectedStatus == ReportStatus.BurningInDay }
        return buildString {
            appendLine("Priority summary")
            appendLine("Top street: ${topStreet?.key ?: "No street"} (${topStreet?.value ?: 0} open)")
            appendLine("Fused lamps: $fusedCount need night-safety attention")
            appendLine("Day-burning lamps: $burningCount may waste energy until fixed")
            append("Review assigned jobs first, then clear the top-street backlog.")
        }
    }
}
