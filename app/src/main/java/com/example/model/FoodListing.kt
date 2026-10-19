package com.example.model

enum class ListingStatus(val label: String, val colorHex: Long) {
    DRAFT("Draft", 0xFF6B7280),
    PENDING_REVIEW("Under Safety Review", 0xFFEA580C),
    AVAILABLE("Available", 0xFF2D6A4F),
    RESERVED("Reserved", 0xFFD97706),
    PICKUP_ASSIGNED("Pickup Assigned", 0xFF2563EB),
    COLLECTED("Collected", 0xFF7C3AED),
    DELIVERED("Delivered & Rescued", 0xFF059669),
    EXPIRED("Expired", 0xFFDC2626),
    CANCELLED("Cancelled", 0xFF9CA3AF),
    REJECTED("Safety Rejected", 0xFFB91C1C)
}

enum class FoodCategory(val label: String, val iconName: String) {
    COOKED_MEALS("Cooked Meals", "restaurant"),
    BAKED_GOODS("Bakery & Bread", "bakery_dining"),
    FRESH_PRODUCE("Fresh Produce", "eco"),
    DAIRY_PANTRY("Dairy & Pantry", "kitchen"),
    CANTEEN_PREP("Canteen Surplus", "soup_kitchen"),
    BEVERAGES("Beverages", "local_cafe"),
    OTHER("Specialty Items", "fastfood")
}

enum class DietaryType(val label: String) {
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    NON_VEGETARIAN("Non-Vegetarian"),
    HALAL("Halal Certified"),
    GLUTEN_FREE("Gluten-Free"),
    EGG_FREE("Egg-Free")
}

data class LocationCoord(
    val address: String,
    val neighborhood: String,
    val latitude: Double,
    val longitude: Double,
    val accessNotes: String = "Staff loading dock around rear."
)

data class FoodListing(
    val id: String,
    val donorId: String,
    val donorName: String,
    val donorOrg: String,
    val title: String,
    val description: String,
    val category: FoodCategory,
    val dietaryType: DietaryType,
    val quantity: Double,
    val unit: String, // "kg", "servings", "boxes", "trays"
    val estimatedServings: Int,
    val preparationTime: String,
    val useByDeadline: String,
    val pickupDeadline: String,
    val storageCondition: String, // e.g. "Refrigerated (2°C - 4°C)", "Hot Hold (>63°C)", "Ambient Dry"
    val temperatureReading: String? = null,
    val allergens: List<String> = emptyList(), // "Dairy", "Gluten", "Nuts", etc.
    val crossContactWarning: String = "Prepared in a commercial kitchen with shared prep counters.",
    val packagingRequirement: String = "Insulated thermal transport box required.",
    val location: LocationCoord,
    val pickupWindow: String, // e.g. "18:00 - 20:30 Today"
    val safetyAttested: Boolean = true,
    val safetyDeclarationNote: String = "Donor confirms adherence to safe holding temps and food hygiene standards.",
    val imageUrl: String? = null,
    val status: ListingStatus = ListingStatus.AVAILABLE,
    val totalReservedUnits: Double = 0.0,
    val safetyReviewNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val remainingQuantity: Double
        get() = (quantity - totalReservedUnits).coerceAtLeast(0.0)

    val isAvailable: Boolean
        get() = status == ListingStatus.AVAILABLE && remainingQuantity > 0.0
}
