package com.example.model

enum class NotificationType {
    NEW_CLAIM,
    CLAIM_APPROVED,
    CLAIM_REJECTED,
    VOLUNTEER_ASSIGNED,
    PICKUP_CONFIRMED,
    DELIVERY_COMPLETED,
    SAFETY_ALERT,
    SYSTEM_ALERT,
    VERIFICATION_UPDATE
}

data class SharePlateNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val targetRole: UserRole? = null
)
