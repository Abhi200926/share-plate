package com.example.ai

import com.example.model.*
import kotlin.math.abs

data class MatchWeights(
    val urgencyWeight: Double = 0.35,
    val distanceWeight: Double = 0.25,
    val quantityWeight: Double = 0.25,
    val dietaryWeight: Double = 0.15
)

data class MatchCandidate(
    val listing: FoodListing,
    val overallScore: Double,
    val urgencyScore: Double,
    val distanceScore: Double,
    val quantityFitScore: Double,
    val dietaryFitScore: Double,
    val explanation: String,
    val aiInsight: String? = null,
    val isEligible: Boolean = true,
    val ineligibilityReason: String? = null
)

data class ParsedSearchFilter(
    val originalQuery: String,
    val dietaryType: DietaryType? = null,
    val minServings: Int? = null,
    val category: FoodCategory? = null,
    val urgencyTonight: Boolean = false,
    val rawKeyword: String = ""
)

class RescueMatcher(private val geminiClient: GeminiClient = GeminiClient()) {

    fun matchAndRank(
        listings: List<FoodListing>,
        recipientOrg: UserProfile,
        requestedServings: Int = 40,
        preferredDietary: DietaryType? = null,
        recipientLat: Double = 37.7749,
        recipientLng: Double = -122.4194,
        weights: MatchWeights = MatchWeights()
    ): List<MatchCandidate> {
        val candidates = mutableListOf<MatchCandidate>()

        for (listing in listings) {
            // --- MANDATORY ELIGIBILITY GATES ---
            if (!listing.isAvailable) {
                candidates.add(
                    MatchCandidate(
                        listing = listing,
                        overallScore = 0.0,
                        urgencyScore = 0.0,
                        distanceScore = 0.0,
                        quantityFitScore = 0.0,
                        dietaryFitScore = 0.0,
                        explanation = "Ineligible: Listing status is '${listing.status.label}'.",
                        isEligible = false,
                        ineligibilityReason = "Status not available."
                    )
                )
                continue
            }

            if (!listing.safetyAttested) {
                candidates.add(
                    MatchCandidate(
                        listing = listing,
                        overallScore = 0.0,
                        urgencyScore = 0.0,
                        distanceScore = 0.0,
                        quantityFitScore = 0.0,
                        dietaryFitScore = 0.0,
                        explanation = "Ineligible: Safety attestation missing.",
                        isEligible = false,
                        ineligibilityReason = "Missing safety attestation."
                    )
                )
                continue
            }

            // --- MULTI-FACTOR DETERMINISTIC SCORING ---
            // 1. Urgency score (listings with pickup deadline tonight score high)
            val urgencyScore = if (listing.pickupDeadline.contains("Tonight", ignoreCase = true) ||
                listing.pickupDeadline.contains("Today", ignoreCase = true)
            ) 95.0 else 60.0

            // 2. Quantity compatibility (ratio of estimated servings)
            val servingsDelta = abs(listing.estimatedServings - requestedServings)
            val quantityFitScore = (100.0 - (servingsDelta * 1.2)).coerceIn(20.0, 100.0)

            // 3. Distance approximation
            val latDelta = abs(listing.location.latitude - recipientLat)
            val lngDelta = abs(listing.location.longitude - recipientLng)
            val approxDistanceKm = (latDelta + lngDelta) * 111.0
            val distanceScore = (100.0 - (approxDistanceKm * 8.0)).coerceIn(10.0, 100.0)

            // 4. Dietary fit
            val dietaryFitScore = when {
                preferredDietary == null -> 90.0
                listing.dietaryType == preferredDietary -> 100.0
                preferredDietary == DietaryType.VEGETARIAN && listing.dietaryType == DietaryType.VEGAN -> 100.0
                else -> 30.0
            }

            // Weighted composite formula
            val compositeScore = (urgencyScore * weights.urgencyWeight) +
                    (distanceScore * weights.distanceWeight) +
                    (quantityFitScore * weights.quantityWeight) +
                    (dietaryFitScore * weights.dietaryWeight)

            val explanation = buildString {
                append("Score: ${compositeScore.toInt()}%. ")
                if (urgencyScore >= 90) append("Immediate rescue priority (${listing.pickupDeadline}). ")
                if (distanceScore >= 80) append("Nearby kitchen (${listing.location.neighborhood}). ")
                append("Servings: ${listing.estimatedServings} (${listing.quantity} ${listing.unit}). ")
                if (dietaryFitScore == 100.0) append("Dietary match: ${listing.dietaryType.label}.")
            }

            candidates.add(
                MatchCandidate(
                    listing = listing,
                    overallScore = compositeScore,
                    urgencyScore = urgencyScore,
                    distanceScore = distanceScore,
                    quantityFitScore = quantityFitScore,
                    dietaryFitScore = dietaryFitScore,
                    explanation = explanation,
                    isEligible = true
                )
            )
        }

        return candidates.sortedByDescending { it.overallScore }
    }

    fun parseNaturalLanguageQuery(query: String): ParsedSearchFilter {
        val q = query.lowercase()
        var dietary: DietaryType? = null
        if (q.contains("vegan")) dietary = DietaryType.VEGAN
        else if (q.contains("vegetarian") || q.contains("veg")) dietary = DietaryType.VEGETARIAN
        else if (q.contains("halal")) dietary = DietaryType.HALAL
        else if (q.contains("gluten-free") || q.contains("gluten free")) dietary = DietaryType.GLUTEN_FREE

        var category: FoodCategory? = null
        if (q.contains("bakery") || q.contains("bread") || q.contains("pastry")) category = FoodCategory.BAKED_GOODS
        else if (q.contains("cooked") || q.contains("buffet") || q.contains("meal") || q.contains("dahl") || q.contains("rice")) category = FoodCategory.COOKED_MEALS
        else if (q.contains("produce") || q.contains("fruit") || q.contains("veg")) category = FoodCategory.FRESH_PRODUCE

        val tonight = q.contains("tonight") || q.contains("evening") || q.contains("today") || q.contains("urgent")

        // Parse number of people/servings
        val regex = Regex("(\\d+)\\s*(people|servings|portions|persons|pax)")
        val match = regex.find(q)
        val servings = match?.groupValues?.get(1)?.toIntOrNull()

        return ParsedSearchFilter(
            originalQuery = query,
            dietaryType = dietary,
            minServings = servings,
            category = category,
            urgencyTonight = tonight,
            rawKeyword = query.trim()
        )
    }

    suspend fun generateAiDispatchExplanation(listing: FoodListing, recipientName: String): String {
        val prompt = """
            Write a concise 2-sentence logistical dispatch recommendation for a food rescue match.
            Donor: ${listing.donorOrg} (${listing.location.address})
            Food: ${listing.title} (${listing.quantity} ${listing.unit}, ${listing.estimatedServings} servings)
            Recipient: $recipientName
            Storage: ${listing.storageCondition}
            Pickup Window: ${listing.pickupWindow}
            Highlight pickup urgency and thermal transport requirement.
        """.trimIndent()

        val result = geminiClient.generateText(prompt)
        return result.getOrElse {
            "Match Recommended: Rescuing ${listing.quantity} ${listing.unit} of ${listing.title} from ${listing.donorOrg} fulfills current capacity. Immediate pickup via thermal transport box advised before ${listing.pickupDeadline}."
        }
    }
}
