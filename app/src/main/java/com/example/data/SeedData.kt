package com.example.data

import com.example.model.*

object SeedData {
    val donors = listOf(
        UserProfile(
            id = "donor-1",
            name = "Chef Marcus Bell",
            email = "m.bell@grandhorizon.com",
            role = UserRole.DONOR,
            organisationName = "Grand Horizon Hotel & Banquet",
            phone = "+1 (555) 234-5678",
            isVerified = true,
            address = "742 Evergreen Blvd, Midtown",
            rating = 4.95,
            completedRescues = 48
        ),
        UserProfile(
            id = "donor-2",
            name = "Clara Fontaine",
            email = "clara@artisanhearth.org",
            role = UserRole.DONOR,
            organisationName = "Artisan Hearth Bakery & Café",
            phone = "+1 (555) 345-6789",
            isVerified = true,
            address = "18 Baker Street, Old Quarter",
            rating = 4.88,
            completedRescues = 32
        ),
        UserProfile(
            id = "donor-3",
            name = "Devon Sanders",
            email = "d.sanders@metrocampus.edu",
            role = UserRole.DONOR,
            organisationName = "Metro University Central Canteen",
            phone = "+1 (555) 456-7890",
            isVerified = true,
            address = "500 College Avenue, Westside",
            rating = 4.92,
            completedRescues = 64
        )
    )

    val recipients = listOf(
        UserProfile(
            id = "recip-1",
            name = "Sister Maria Gomez",
            email = "maria@hopehousepantry.org",
            role = UserRole.RECIPIENT,
            organisationName = "Hope House Community Shelter",
            phone = "+1 (555) 890-1234",
            isVerified = true,
            address = "310 Elm Street, Southside",
            rating = 4.97,
            completedRescues = 52
        ),
        UserProfile(
            id = "recip-2",
            name = "Tariq Al-Mansoor",
            email = "tariq@citycentralfoodbank.org",
            role = UserRole.RECIPIENT,
            organisationName = "City Central Food Bank & Kitchen",
            phone = "+1 (555) 901-2345",
            isVerified = true,
            address = "850 Industrial Parkway, Docklands",
            rating = 4.91,
            completedRescues = 94
        ),
        UserProfile(
            id = "recip-3",
            name = "Rachel Kim",
            email = "rachel@stjudeyouth.org",
            role = UserRole.RECIPIENT,
            organisationName = "St. Jude Youth Center Meals",
            phone = "+1 (555) 012-3456",
            isVerified = true,
            address = "142 Oak Avenue, Uptown",
            rating = 4.85,
            completedRescues = 28
        )
    )

    val volunteers = listOf(
        UserProfile(
            id = "vol-1",
            name = "Leo Sterling",
            email = "leo.sterling@rescuevol.net",
            role = UserRole.VOLUNTEER,
            organisationName = "Cargo Courier Green Volunteers",
            phone = "+1 (555) 678-9012",
            isVerified = true,
            address = "Downtown Dispatch Hub",
            rating = 4.98,
            completedRescues = 73
        ),
        UserProfile(
            id = "vol-2",
            name = "Aisha Patel",
            email = "aisha.p@rescuevol.net",
            role = UserRole.VOLUNTEER,
            organisationName = "Independent Electric Bike Courier",
            phone = "+1 (555) 789-0123",
            isVerified = true,
            address = "Midtown Bike Station 4",
            rating = 4.92,
            completedRescues = 39
        ),
        UserProfile(
            id = "vol-3",
            name = "Carlos Ramirez",
            email = "carlos.r@rescuevol.net",
            role = UserRole.VOLUNTEER,
            organisationName = "Van Rescue Collective",
            phone = "+1 (555) 890-9876",
            isVerified = true,
            address = "Westside Distribution Bay",
            rating = 4.89,
            completedRescues = 41
        )
    )

    val admin = UserProfile(
        id = "admin-1",
        name = "Dr. Evelyn Vance",
        email = "evelyn.vance@shareplate.network",
        role = UserRole.ADMINISTRATOR,
        organisationName = "SharePlate Food Safety & Trust Governance",
        phone = "+1 (555) 100-2000",
        isVerified = true,
        address = "SharePlate HQ, Civic Center",
        rating = 5.0,
        completedRescues = 120
    )

    fun createInitialListings(): List<FoodListing> = listOf(
        FoodListing(
            id = "list-1",
            donorId = "donor-1",
            donorName = "Chef Marcus Bell",
            donorOrg = "Grand Horizon Hotel & Banquet",
            title = "Surplus Mediterranean Buffet Trays",
            description = "Gourmet roasted herb chicken, wild rice pilaf, and steamed seasonal vegetables from a catered afternoon gala. Held under continuous hot-holding (>65°C) until pack-down.",
            category = FoodCategory.COOKED_MEALS,
            dietaryType = DietaryType.NON_VEGETARIAN,
            quantity = 24.0,
            unit = "kg",
            estimatedServings = 60,
            preparationTime = "Today, 14:00",
            useByDeadline = "Tonight, 22:30",
            pickupDeadline = "Tonight, 20:00",
            storageCondition = "Hot Hold (>63°C) or rapid refrigeration",
            temperatureReading = "68.4°C at pack-down",
            allergens = listOf("Poultry", "Celery"),
            crossContactWarning = "Nut-free kitchen section, but shared main convection ovens.",
            packagingRequirement = "Stainless steam pans with sealed lids. Thermal food transport box required.",
            location = LocationCoord("742 Evergreen Blvd", "Midtown", 37.7749, -122.4194, "Kitchen loading bay 2 via service ramp."),
            pickupWindow = "18:30 - 20:00 Today",
            safetyAttested = true,
            status = ListingStatus.AVAILABLE,
            totalReservedUnits = 0.0
        ),
        FoodListing(
            id = "list-2",
            donorId = "donor-2",
            donorName = "Clara Fontaine",
            donorOrg = "Artisan Hearth Bakery & Café",
            title = "Fresh Sourdough Loaves & Viennoiserie",
            description = "Artisanal crusty sourdough loaves, multigrain batards, and butter croissants baked fresh this morning. Unsold at evening counter close.",
            category = FoodCategory.BAKED_GOODS,
            dietaryType = DietaryType.VEGETARIAN,
            quantity = 18.0,
            unit = "kg",
            estimatedServings = 45,
            preparationTime = "Today, 06:30",
            useByDeadline = "Tomorrow, 20:00",
            pickupDeadline = "Tonight, 21:00",
            storageCondition = "Ambient dry, protected from humidity",
            allergens = listOf("Gluten", "Dairy (croissants only)"),
            crossContactWarning = "Contains sesame and tree nuts in bakery environment.",
            packagingRequirement = "Food-grade breathable kraft bakery bags and stacked bakery crates.",
            location = LocationCoord("18 Baker Street", "Old Quarter", 37.7833, -122.4167, "Ring rear bell marked Bakery Deliveries."),
            pickupWindow = "19:00 - 21:00 Tonight",
            safetyAttested = true,
            status = ListingStatus.AVAILABLE,
            totalReservedUnits = 0.0
        ),
        FoodListing(
            id = "list-3",
            donorId = "donor-3",
            donorName = "Devon Sanders",
            donorOrg = "Metro University Central Canteen",
            title = "Hearty Lentil & Spinach Dahl (Vegan)",
            description = "High-protein red lentil dahl simmered with ginger, cumin, spinach, and coconut milk. Freshly cooked batch in university dining hall.",
            category = FoodCategory.COOKED_MEALS,
            dietaryType = DietaryType.VEGAN,
            quantity = 30.0,
            unit = "kg",
            estimatedServings = 75,
            preparationTime = "Today, 12:00",
            useByDeadline = "Tonight, 23:00",
            pickupDeadline = "Tonight, 19:30",
            storageCondition = "Refrigerated (2°C - 4°C)",
            temperatureReading = "3.2°C logged in walk-in chiller",
            allergens = emptyList(),
            crossContactWarning = "Cooked in certified vegan & allergen-segregated prep station.",
            packagingRequirement = "Heavy-duty food-grade Cambro containers with latch lids.",
            location = LocationCoord("500 College Avenue", "Westside", 37.7690, -122.4467, "Enter Campus Gate 4, Canteen Dispatch Dock."),
            pickupWindow = "18:00 - 19:30 Tonight",
            safetyAttested = true,
            status = ListingStatus.RESERVED,
            totalReservedUnits = 30.0
        ),
        FoodListing(
            id = "list-4",
            donorId = "donor-1",
            donorName = "Chef Marcus Bell",
            donorOrg = "Grand Horizon Hotel & Banquet",
            title = "Chilled Gourmet Sandwich Platters",
            description = "Assorted artisan wraps and sandwiches (Egg & Cress, Roasted Veg & Hummus, Smoked Turkey Breast) individually parchment-wrapped.",
            category = FoodCategory.COOKED_MEALS,
            dietaryType = DietaryType.VEGETARIAN,
            quantity = 15.0,
            unit = "kg",
            estimatedServings = 40,
            preparationTime = "Today, 11:30",
            useByDeadline = "Tomorrow, 12:00",
            pickupDeadline = "Tonight, 20:30",
            storageCondition = "Refrigerated (1°C - 3°C)",
            temperatureReading = "2.8°C",
            allergens = listOf("Gluten", "Eggs", "Dairy"),
            crossContactWarning = "Color-coded trays clearly designate vegetarian vs poultry.",
            packagingRequirement = "Lidded catering platters with cold ice pack inserts.",
            location = LocationCoord("742 Evergreen Blvd", "Midtown", 37.7749, -122.4194, "Service door C."),
            pickupWindow = "19:00 - 20:30 Tonight",
            safetyAttested = true,
            status = ListingStatus.PICKUP_ASSIGNED,
            totalReservedUnits = 15.0
        ),
        FoodListing(
            id = "list-5",
            donorId = "donor-2",
            donorName = "Clara Fontaine",
            donorOrg = "Artisan Hearth Bakery & Café",
            title = "Organic Whole-Wheat Focaccia with Rosemary",
            description = "Heritage grain focaccia baked with organic extra virgin olive oil, sea salt flakes, and fresh garden rosemary.",
            category = FoodCategory.BAKED_GOODS,
            dietaryType = DietaryType.VEGAN,
            quantity = 10.0,
            unit = "kg",
            estimatedServings = 30,
            preparationTime = "Today, 09:00",
            useByDeadline = "Tomorrow, 18:00",
            pickupDeadline = "Tonight, 21:00",
            storageCondition = "Ambient dry, sealed against pests",
            allergens = listOf("Gluten"),
            crossContactWarning = "Vegan recipe, prepared in wheat flour facility.",
            packagingRequirement = "Cardboard bakery flat cartons.",
            location = LocationCoord("18 Baker Street", "Old Quarter", 37.7833, -122.4167, "Rear courtyard."),
            pickupWindow = "19:00 - 21:00 Tonight",
            safetyAttested = true,
            status = ListingStatus.AVAILABLE,
            totalReservedUnits = 0.0
        ),
        FoodListing(
            id = "list-6",
            donorId = "donor-3",
            donorName = "Devon Sanders",
            donorOrg = "Metro University Central Canteen",
            title = "Delivered & Rescued: Steamed Jasmine Rice & Vegetable Curry",
            description = "Rescued earlier today: 35 kg of vegetable Thai green curry delivered safely to Hope House Community Shelter.",
            category = FoodCategory.COOKED_MEALS,
            dietaryType = DietaryType.VEGAN,
            quantity = 35.0,
            unit = "kg",
            estimatedServings = 90,
            preparationTime = "Yesterday, 17:00",
            useByDeadline = "Today, 14:00",
            pickupDeadline = "Today, 11:00",
            storageCondition = "Temperature managed thermal boxes",
            allergens = listOf("Soy"),
            crossContactWarning = "Peanut-free verified batch.",
            packagingRequirement = "Thermal food coolers.",
            location = LocationCoord("500 College Avenue", "Westside", 37.7690, -122.4467, "Canteen dock."),
            pickupWindow = "Completed 11:30 AM",
            safetyAttested = true,
            status = ListingStatus.DELIVERED,
            totalReservedUnits = 35.0
        ),
        FoodListing(
            id = "list-7",
            donorId = "donor-1",
            donorName = "Chef Marcus Bell",
            donorOrg = "Grand Horizon Hotel & Banquet",
            title = "Pending Safety Review: Seafood Paella",
            description = "Seafood paella containing shrimp and calamari. Flagged for strict cold chain verification and temperature log validation.",
            category = FoodCategory.COOKED_MEALS,
            dietaryType = DietaryType.NON_VEGETARIAN,
            quantity = 16.0,
            unit = "kg",
            estimatedServings = 35,
            preparationTime = "Today, 15:30",
            useByDeadline = "Tonight, 21:00",
            pickupDeadline = "Tonight, 19:00",
            storageCondition = "Hot Hold (>63°C)",
            temperatureReading = "61.5°C (Marginal - under auditor review)",
            allergens = listOf("Crustaceans", "Molluscs", "Fish"),
            crossContactWarning = "High-risk allergen item.",
            packagingRequirement = "Sealed stainless food pans.",
            location = LocationCoord("742 Evergreen Blvd", "Midtown", 37.7749, -122.4194, "Kitchen bay."),
            pickupWindow = "On hold pending verification",
            safetyAttested = false,
            status = ListingStatus.PENDING_REVIEW,
            totalReservedUnits = 0.0,
            safetyReviewNotes = "Temperature reading 61.5°C is below the 63°C minimum hot-hold threshold. Requires temperature re-check before public dispatch."
        ),
        FoodListing(
            id = "list-8",
            donorId = "donor-2",
            donorName = "Clara Fontaine",
            donorOrg = "Artisan Hearth Bakery & Café",
            title = "Expired: Custard & Fresh Berry Brioche Tarts",
            description = "Dairy-rich custard tarts past their mandatory 4-hour room temperature ambient window. Automatically decommissioned.",
            category = FoodCategory.BAKED_GOODS,
            dietaryType = DietaryType.VEGETARIAN,
            quantity = 6.0,
            unit = "kg",
            estimatedServings = 20,
            preparationTime = "Yesterday, 10:00",
            useByDeadline = "Yesterday, 16:00",
            pickupDeadline = "Yesterday, 15:00",
            storageCondition = "Ambient",
            allergens = listOf("Dairy", "Eggs", "Gluten"),
            crossContactWarning = "Expired perishable dairy.",
            packagingRequirement = "Decommissioned - Sent for municipal composting.",
            location = LocationCoord("18 Baker Street", "Old Quarter", 37.7833, -122.4167, "Compost bin 2."),
            pickupWindow = "Window expired",
            safetyAttested = false,
            status = ListingStatus.EXPIRED,
            totalReservedUnits = 0.0
        )
    )

    fun createInitialClaims(): List<RescueClaim> = listOf(
        RescueClaim(
            id = "claim-101",
            listingId = "list-3",
            listingTitle = "Hearty Lentil & Spinach Dahl (Vegan)",
            recipientId = "recip-1",
            recipientName = "Sister Maria Gomez",
            recipientOrg = "Hope House Community Shelter",
            quantityClaimed = 30.0,
            unit = "kg",
            status = ClaimStatus.APPROVED,
            claimedAt = System.currentTimeMillis() - 7200000L,
            pickupInstructions = "Bring Cambro insulation sleeves to Canteen Gate 4. Show claim #101."
        ),
        RescueClaim(
            id = "claim-102",
            listingId = "list-4",
            listingTitle = "Chilled Gourmet Sandwich Platters",
            recipientId = "recip-2",
            recipientName = "Tariq Al-Mansoor",
            recipientOrg = "City Central Food Bank & Kitchen",
            quantityClaimed = 15.0,
            unit = "kg",
            status = ClaimStatus.APPROVED,
            claimedAt = System.currentTimeMillis() - 5400000L,
            pickupInstructions = "Assigned to Volunteer Leo Sterling. Present QR upon arrival."
        ),
        RescueClaim(
            id = "claim-103",
            listingId = "list-6",
            listingTitle = "Delivered & Rescued: Steamed Jasmine Rice & Vegetable Curry",
            recipientId = "recip-1",
            recipientName = "Sister Maria Gomez",
            recipientOrg = "Hope House Community Shelter",
            quantityClaimed = 35.0,
            unit = "kg",
            status = ClaimStatus.COMPLETED,
            claimedAt = System.currentTimeMillis() - 86400000L,
            pickupInstructions = "Delivered and verified via Handover QR."
        )
    )

    fun createInitialAssignments(): List<RescueAssignment> = listOf(
        RescueAssignment(
            id = "asgn-201",
            claimId = "claim-102",
            listingId = "list-4",
            volunteerId = "vol-1",
            volunteerName = "Leo Sterling",
            status = AssignmentStatus.ACCEPTED,
            listingTitle = "Chilled Gourmet Sandwich Platters",
            donorOrg = "Grand Horizon Hotel & Banquet",
            pickupAddress = "742 Evergreen Blvd, Midtown",
            recipientOrg = "City Central Food Bank & Kitchen",
            dropoffAddress = "850 Industrial Parkway, Docklands",
            quantitySummary = "15 kg (approx 40 servings)",
            pickupQrToken = "QR-PICKUP-SP-201-9941",
            deliveryQrToken = "QR-DROP-SP-201-3329",
            assignedAt = System.currentTimeMillis() - 3600000L,
            estimatedTravelMinutes = 14
        ),
        RescueAssignment(
            id = "asgn-202",
            claimId = "claim-103",
            listingId = "list-6",
            volunteerId = "vol-2",
            volunteerName = "Aisha Patel",
            status = AssignmentStatus.DELIVERED,
            listingTitle = "Delivered: Jasmine Rice & Veg Curry",
            donorOrg = "Metro University Central Canteen",
            pickupAddress = "500 College Avenue, Westside",
            recipientOrg = "Hope House Community Shelter",
            dropoffAddress = "310 Elm Street, Southside",
            quantitySummary = "35 kg (approx 90 servings)",
            pickupQrToken = "QR-PICKUP-SP-202-USED",
            deliveryQrToken = "QR-DROP-SP-202-USED",
            assignedAt = System.currentTimeMillis() - 86400000L,
            pickedUpAt = System.currentTimeMillis() - 82000000L,
            deliveredAt = System.currentTimeMillis() - 79000000L,
            estimatedTravelMinutes = 22
        )
    )

    fun createInitialQrTokens(): List<QrToken> = listOf(
        QrToken(
            tokenString = "QR-PICKUP-SP-201-9941",
            assignmentId = "asgn-201",
            listingId = "list-4",
            type = TokenType.PICKUP,
            issuedForUser = "donor-1",
            isUsed = false
        ),
        QrToken(
            tokenString = "QR-DROP-SP-201-3329",
            assignmentId = "asgn-201",
            listingId = "list-4",
            type = TokenType.DELIVERY,
            issuedForUser = "recip-2",
            isUsed = false
        ),
        QrToken(
            tokenString = "QR-PICKUP-SP-202-USED",
            assignmentId = "asgn-202",
            listingId = "list-6",
            type = TokenType.PICKUP,
            issuedForUser = "donor-3",
            isUsed = true,
            usedAt = System.currentTimeMillis() - 82000000L
        ),
        QrToken(
            tokenString = "QR-DROP-SP-202-USED",
            assignmentId = "asgn-202",
            listingId = "list-6",
            type = TokenType.DELIVERY,
            issuedForUser = "recip-1",
            isUsed = true,
            usedAt = System.currentTimeMillis() - 79000000L
        )
    )

    fun createInitialIncidents(): List<SafetyIncidentReport> = listOf(
        SafetyIncidentReport(
            id = "inc-301",
            listingId = "list-7",
            listingTitle = "Pending Safety Review: Seafood Paella",
            reporterId = "admin-1",
            reporterName = "Dr. Evelyn Vance (Lead Auditor)",
            reporterRole = UserRole.ADMINISTRATOR,
            type = IncidentType.TEMPERATURE_ABUSE,
            description = "Hot-holding reading of 61.5°C falls below safety threshold of 63.0°C. Batch has been locked from claiming until donor kitchen presents recalibration report.",
            reportedAt = System.currentTimeMillis() - 10800000L,
            status = IncidentStatus.UNDER_REVIEW
        )
    )

    fun createInitialNotifications(): List<SharePlateNotification> = listOf(
        SharePlateNotification(
            id = "notif-1",
            title = "New Surplus Listing Available",
            message = "Grand Horizon Hotel posted 24 kg Mediterranean Buffet Trays near Midtown.",
            type = NotificationType.NEW_CLAIM,
            targetRole = UserRole.RECIPIENT
        ),
        SharePlateNotification(
            id = "notif-2",
            title = "Rescue Assignment Assigned",
            message = "You have been assigned to pick up 15 kg sandwiches from Grand Horizon Hotel.",
            type = NotificationType.VOLUNTEER_ASSIGNED,
            targetRole = UserRole.VOLUNTEER
        ),
        SharePlateNotification(
            id = "notif-3",
            title = "Safety Alert: Temperature Hold Flag",
            message = "Listing #list-7 Seafood Paella placed on hold pending temperature recalibration.",
            type = NotificationType.SAFETY_ALERT,
            targetRole = UserRole.ADMINISTRATOR
        )
    )

    fun createInitialAuditLogs(): List<AuditEntry> = listOf(
        AuditEntry(
            id = "aud-1",
            action = "LISTING_CREATED",
            actorName = "Chef Marcus Bell",
            actorRole = UserRole.DONOR,
            targetEntity = "FoodListing:list-1",
            details = "Created 24 kg Mediterranean Buffet Trays with safety attestation."
        ),
        AuditEntry(
            id = "aud-2",
            action = "CLAIM_APPROVED",
            actorName = "System Transaction Engine",
            actorRole = UserRole.ADMINISTRATOR,
            targetEntity = "RescueClaim:claim-101",
            details = "Allocated 30 kg Lentil Dahl to Hope House Community Shelter. Units locked."
        ),
        AuditEntry(
            id = "aud-3",
            action = "SAFETY_HOLD_ENFORCED",
            actorName = "Dr. Evelyn Vance",
            actorRole = UserRole.ADMINISTRATOR,
            targetEntity = "FoodListing:list-7",
            details = "Temperature 61.5°C below critical limit. Locked listing from recipient discovery."
        )
    )

    fun createInitialOrganisationRecords(): List<OrganisationVerificationRecord> = listOf(
        OrganisationVerificationRecord(
            id = "org-1",
            name = "Grand Horizon Hotel & Banquet",
            role = UserRole.DONOR,
            registrationNumber = "EIN-88412903",
            foodHygieneRating = "Grade A / 5-Star (City Dept Health)",
            coldChainCertified = true,
            contactPerson = "Chef Marcus Bell",
            contactEmail = "m.bell@grandhorizon.com",
            address = "742 Evergreen Blvd, Midtown",
            verificationStatus = VerificationStatus.VERIFIED,
            auditorNotes = "Inspected commercial blast chiller and food handler licenses on Feb 2025."
        ),
        OrganisationVerificationRecord(
            id = "org-2",
            name = "Hope House Community Shelter",
            role = UserRole.RECIPIENT,
            registrationNumber = "501(c)(3) #94-281094",
            foodHygieneRating = "Safe Food Certified Kitchen",
            coldChainCertified = true,
            contactPerson = "Sister Maria Gomez",
            contactEmail = "maria@hopehousepantry.org",
            address = "310 Elm Street, Southside",
            verificationStatus = VerificationStatus.VERIFIED,
            auditorNotes = "Walk-in cooler inspected, 120-meal daily serving capacity confirmed."
        ),
        OrganisationVerificationRecord(
            id = "org-3",
            name = "Sunrise Community Dining Hall",
            role = UserRole.RECIPIENT,
            registrationNumber = "PEND-CHARITY-5521",
            foodHygieneRating = "County Public Health Permit Pending",
            coldChainCertified = false,
            contactPerson = "David O'Connor",
            contactEmail = "david@sunrisedining.org",
            address = "420 Mission St, Eastside",
            verificationStatus = VerificationStatus.PENDING_AUDIT,
            auditorNotes = "Awaiting upload of current temperature log calibration certificate."
        ),
        OrganisationVerificationRecord(
            id = "org-4",
            name = "BakeCraft Artisan Patisserie",
            role = UserRole.DONOR,
            registrationNumber = "EIN-91028345",
            foodHygieneRating = "Commercial Bakery License #B-491",
            coldChainCertified = true,
            contactPerson = "Helena Vance",
            contactEmail = "helena@bakecraft.co",
            address = "88 Harbor Walk, Pier District",
            verificationStatus = VerificationStatus.PENDING_AUDIT,
            auditorNotes = "Application submitted for surplus sourdough & morning pastry donations."
        )
    )
}

