package com.grameenlight.app.data.local

import com.grameenlight.app.data.model.PoleStatus

object SeedData {
    fun villagePoles(now: Long): List<PoleEntity> {
        val streets = listOf(
            "Temple Road" to 1,
            "School Street" to 1,
            "Market Lane" to 2,
            "Lake View Road" to 2,
            "Panchayat Cross" to 3
        )
        return streets.flatMapIndexed { streetIndex, (street, ward) ->
            (1..5).map { number ->
                val poleNumber = streetIndex * 5 + number
                PoleEntity(
                    poleId = "GL-P-${poleNumber.toString().padStart(3, '0')}",
                    streetName = street,
                    wardNo = ward,
                    currentStatus = when (poleNumber) {
                        3, 9 -> PoleStatus.Fused
                        6, 14 -> PoleStatus.BurningInDay
                        18 -> PoleStatus.Assigned
                        else -> PoleStatus.Working
                    },
                    lastUpdatedAt = now
                )
            }
        }
    }
}
