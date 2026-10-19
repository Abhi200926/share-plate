package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.DietaryType
import com.example.model.FoodCategory
import com.example.model.OfflineListingDraft

/**
 * Room entity representing an offline-friendly food donation draft.
 * Stores donor-prepared listings on-device when network is unavailable or
 * pending review, supporting safe synchronization retries.
 */
@Entity(tableName = "offline_drafts")
data class OfflineDraftEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val category: String, // String representation of FoodCategory
    val dietaryType: String, // String representation of DietaryType
    val quantity: Double,
    val unit: String = "kg",
    val estimatedServings: Int,
    val prepTime: String,
    val useByDeadline: String,
    val pickupDeadline: String,
    val storageCondition: String,
    val temperatureReading: String? = null,
    val allergens: List<String> = emptyList(),
    val packagingRequirement: String,
    val pickupWindow: String,
    val address: String,
    val safetyAttested: Boolean,
    val savedAtTimestamp: Long = System.currentTimeMillis(),
    val syncAttempts: Int = 0,
    val lastSyncError: String? = null,
    val isSynced: Boolean = false
) {
    fun toDomainModel(): OfflineListingDraft {
        val cat = try {
            FoodCategory.valueOf(category)
        } catch (_: Exception) {
            FoodCategory.COOKED_MEALS
        }
        val diet = try {
            DietaryType.valueOf(dietaryType)
        } catch (_: Exception) {
            DietaryType.VEGETARIAN
        }

        return OfflineListingDraft(
            id = id,
            title = title,
            description = description,
            category = cat,
            dietaryType = diet,
            quantity = quantity,
            unit = unit,
            estimatedServings = estimatedServings,
            prepTime = prepTime,
            useByDeadline = useByDeadline,
            pickupDeadline = pickupDeadline,
            storageCondition = storageCondition,
            temperatureReading = temperatureReading,
            allergens = allergens,
            packagingRequirement = packagingRequirement,
            pickupWindow = pickupWindow,
            address = address,
            safetyAttested = safetyAttested,
            savedAtTimestamp = savedAtTimestamp,
            syncAttempts = syncAttempts,
            lastSyncError = lastSyncError
        )
    }

    companion object {
        fun fromDomainModel(draft: OfflineListingDraft, isSynced: Boolean = false): OfflineDraftEntity {
            return OfflineDraftEntity(
                id = draft.id,
                title = draft.title,
                description = draft.description,
                category = draft.category.name,
                dietaryType = draft.dietaryType.name,
                quantity = draft.quantity,
                unit = draft.unit,
                estimatedServings = draft.estimatedServings,
                prepTime = draft.prepTime,
                useByDeadline = draft.useByDeadline,
                pickupDeadline = draft.pickupDeadline,
                storageCondition = draft.storageCondition,
                temperatureReading = draft.temperatureReading,
                allergens = draft.allergens,
                packagingRequirement = draft.packagingRequirement,
                pickupWindow = draft.pickupWindow,
                address = draft.address,
                safetyAttested = draft.safetyAttested,
                savedAtTimestamp = draft.savedAtTimestamp,
                syncAttempts = draft.syncAttempts,
                lastSyncError = draft.lastSyncError,
                isSynced = isSynced
            )
        }
    }
}
