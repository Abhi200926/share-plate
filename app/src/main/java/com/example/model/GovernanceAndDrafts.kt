package com.example.model

enum class VerificationStatus(val label: String) {
    PENDING_AUDIT("Pending Audit"),
    VERIFIED("Verified & Certified"),
    REJECTED("Rejected / Docs Required")
}

data class OrganisationVerificationRecord(
    val id: String,
    val name: String,
    val role: UserRole,
    val registrationNumber: String,
    val foodHygieneRating: String, // e.g. "Grade A / 5-Star"
    val coldChainCertified: Boolean,
    val contactPerson: String,
    val contactEmail: String,
    val address: String,
    val verificationStatus: VerificationStatus,
    val auditorNotes: String? = null,
    val submittedAt: String = "Today, 09:30"
)

data class OfflineListingDraft(
    val id: String,
    val title: String,
    val description: String,
    val category: FoodCategory,
    val dietaryType: DietaryType,
    val quantity: Double,
    val unit: String,
    val estimatedServings: Int,
    val prepTime: String,
    val useByDeadline: String,
    val pickupDeadline: String,
    val storageCondition: String,
    val temperatureReading: String?,
    val allergens: List<String>,
    val packagingRequirement: String,
    val pickupWindow: String,
    val address: String,
    val safetyAttested: Boolean,
    val savedAtTimestamp: Long = System.currentTimeMillis(),
    val syncAttempts: Int = 0,
    val lastSyncError: String? = null
)

data class ColdChainLogEntry(
    val id: String,
    val assignmentId: String,
    val recordedBy: String,
    val temperatureCelsius: Double,
    val isCompliant: Boolean, // < 5.0 C for chilled or > 60.0 C for hot
    val recordedAtTimestamp: Long = System.currentTimeMillis(),
    val notes: String
)
