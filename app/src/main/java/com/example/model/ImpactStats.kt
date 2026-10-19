package com.example.model

data class ImpactMetrics(
    val totalFoodWeightRescuedKg: Double,
    val completedDeliveries: Int,
    val estimatedServingsDelivered: Int,
    val activeRescueOpportunities: Int,
    val uncollectedOrExpiredCount: Int,
    val activeDonorCount: Int,
    val activeRecipientCount: Int,
    val activeVolunteerCount: Int,
    val rescueCompletionRatePercent: Double,
    val co2EquivalentPreventedKg: Double, // Formula: 1 kg rescued food ≈ 2.5 kg CO2e prevented (UN FAO Food Wastage Footprint standard)
    val isDemoData: Boolean = true
)

data class EnvironmentalFactor(
    val factorName: String,
    val factorValue: String,
    val unit: String,
    val sourceCitation: String
)
