package com.example.model

enum class UserRole(val displayName: String, val badgeColorHex: Long) {
    DONOR("Food Donor", 0xFF1E6F50),
    RECIPIENT("Verified Recipient", 0xFF2563EB),
    VOLUNTEER("Rescue Volunteer", 0xFFD97706),
    ADMINISTRATOR("Platform Admin", 0xFF7C3AED)
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val organisationName: String,
    val phone: String = "+1 (555) 019-2834",
    val isVerified: Boolean = true,
    val address: String = "124 Market St, Downtown",
    val rating: Double = 4.9,
    val joinedDate: String = "Jan 2025",
    val completedRescues: Int = 0
)
