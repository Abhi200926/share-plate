package com.example.model

enum class ClaimStatus(val label: String) {
    PENDING("Pending Approval"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed")
}

data class RescueClaim(
    val id: String,
    val listingId: String,
    val listingTitle: String,
    val recipientId: String,
    val recipientName: String,
    val recipientOrg: String,
    val quantityClaimed: Double,
    val unit: String,
    val status: ClaimStatus = ClaimStatus.APPROVED,
    val claimedAt: Long = System.currentTimeMillis(),
    val pickupInstructions: String = "Present claim ID upon arrival at the kitchen dispatch desk."
)

enum class AssignmentStatus(val label: String, val stepIndex: Int) {
    ASSIGNED("Assigned", 0),
    ACCEPTED("Accepted by Volunteer", 1),
    PICKED_UP("Picked Up & En Route", 2),
    DELIVERED("Delivered & Rescued", 3),
    CANCELLED("Cancelled", -1)
}

data class RescueAssignment(
    val id: String,
    val claimId: String,
    val listingId: String,
    val volunteerId: String,
    val volunteerName: String,
    val status: AssignmentStatus = AssignmentStatus.ASSIGNED,
    val listingTitle: String,
    val donorOrg: String,
    val pickupAddress: String,
    val recipientOrg: String,
    val dropoffAddress: String,
    val quantitySummary: String,
    val pickupQrToken: String,
    val deliveryQrToken: String,
    val assignedAt: Long = System.currentTimeMillis(),
    val pickedUpAt: Long? = null,
    val deliveredAt: Long? = null,
    val estimatedTravelMinutes: Int = 18
)

enum class TokenType {
    PICKUP,
    DELIVERY
}

data class QrToken(
    val tokenString: String,
    val assignmentId: String,
    val listingId: String,
    val type: TokenType,
    val issuedForUser: String,
    val isUsed: Boolean = false,
    val usedAt: Long? = null,
    val expiresAt: Long = System.currentTimeMillis() + 86400000L // 24 hours
)

data class HandoverEvent(
    val id: String,
    val assignmentId: String,
    val type: TokenType,
    val confirmedByActor: String,
    val confirmedAt: Long = System.currentTimeMillis(),
    val tokenUsed: String,
    val note: String
)
