package com.example.model

enum class IncidentType(val label: String) {
    PACKAGING_DAMAGE("Damaged / Unsealed Packaging"),
    TEMPERATURE_ABUSE("Temperature Out of Safe Range"),
    SUSPECTED_SPOILAGE("Suspected Odor or Spoilage"),
    UNLABELED_ALLERGEN("Undeclared Allergen Warning"),
    LATE_PICKUP("Pickup Window Expired"),
    OTHER("Other Operational Incident")
}

enum class IncidentStatus(val label: String) {
    OPEN("Open Investigation"),
    UNDER_REVIEW("Under Admin Review"),
    RESOLVED("Resolved"),
    ESCALATED("Escalated to Food Safety Authorities")
}

data class SafetyIncidentReport(
    val id: String,
    val listingId: String,
    val listingTitle: String,
    val reporterId: String,
    val reporterName: String,
    val reporterRole: UserRole,
    val type: IncidentType,
    val description: String,
    val reportedAt: Long = System.currentTimeMillis(),
    val status: IncidentStatus = IncidentStatus.OPEN,
    val emergencyEscalationNotice: String = "For life-threatening anaphylaxis or immediate acute contamination, contact local emergency dispatch (911/112) immediately."
)

data class SafetyReviewDecision(
    val id: String,
    val listingId: String,
    val reviewerName: String,
    val approved: Boolean,
    val notes: String,
    val reviewedAt: Long = System.currentTimeMillis()
)
