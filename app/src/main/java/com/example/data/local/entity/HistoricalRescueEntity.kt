package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.AssignmentStatus
import com.example.model.FoodCategory
import com.example.model.RescueAssignment

/**
 * Room entity for historical and completed food rescue operations.
 * Caches immutable mission delivery logs, temperature readings, CO2 calculations,
 * and handover proof on-device for auditing, reporting, and offline inspection.
 */
@Entity(tableName = "historical_rescues")
data class HistoricalRescueEntity(
    @PrimaryKey
    val id: String,
    val assignmentId: String,
    val listingId: String,
    val listingTitle: String,
    val category: String,
    val donorOrg: String,
    val pickupAddress: String,
    val recipientOrg: String,
    val dropoffAddress: String,
    val volunteerId: String,
    val volunteerName: String,
    val quantityKg: Double,
    val estimatedMeals: Int,
    val co2PreventedKg: Double,
    val status: String, // String representation of AssignmentStatus (e.g., DELIVERED, PICKED_UP, CANCELLED)
    val pickupTimestamp: Long = System.currentTimeMillis(),
    val deliveryTimestamp: Long = System.currentTimeMillis(),
    val transitTemperatureCelsius: Double? = null,
    val handoverToken: String? = null,
    val notes: String? = null,
    val verifiedByAdmin: Boolean = true
) {
    companion object {
        fun fromRescueAssignment(
            assignment: RescueAssignment,
            temperatureCelsius: Double? = null,
            handoverToken: String? = null,
            notes: String? = null
        ): HistoricalRescueEntity {
            val parsedKg = assignment.quantitySummary.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 12.0
            val meals = (parsedKg * 2.5).toInt().coerceAtLeast(1)
            val co2 = parsedKg * 2.5
            return HistoricalRescueEntity(
                id = "hist-${assignment.id}",
                assignmentId = assignment.id,
                listingId = assignment.listingId,
                listingTitle = assignment.listingTitle,
                category = FoodCategory.COOKED_MEALS.name,
                donorOrg = assignment.donorOrg,
                pickupAddress = assignment.pickupAddress,
                recipientOrg = assignment.recipientOrg,
                dropoffAddress = assignment.dropoffAddress,
                volunteerId = assignment.volunteerId,
                volunteerName = assignment.volunteerName,
                quantityKg = parsedKg,
                estimatedMeals = meals,
                co2PreventedKg = co2,
                status = assignment.status.name,
                pickupTimestamp = System.currentTimeMillis() - 3600000L,
                deliveryTimestamp = System.currentTimeMillis(),
                transitTemperatureCelsius = temperatureCelsius,
                handoverToken = handoverToken,
                notes = notes,
                verifiedByAdmin = assignment.status == AssignmentStatus.DELIVERED
            )
        }
    }
}
