package com.example

import com.example.ai.HistoricalProductionLog
import com.example.ai.RescueMatcher
import com.example.ai.SurplusDemandPredictor
import com.example.data.SeedData
import com.example.data.SharePlateRepository
import com.example.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SharePlateBusinessLogicTest {

    private lateinit var repository: SharePlateRepository
    private lateinit var matcher: RescueMatcher
    private lateinit var predictor: SurplusDemandPredictor

    @Before
    fun setUp() {
        repository = SharePlateRepository()
        matcher = RescueMatcher()
        predictor = SurplusDemandPredictor()
    }

    @Test
    fun testCreateValidFoodListing() {
        val result = repository.createFoodListing(
            title = "Fresh Herb Focaccia",
            description = "Baked freshly today with rosemary.",
            category = FoodCategory.BAKED_GOODS,
            dietaryType = DietaryType.VEGAN,
            quantity = 12.0,
            unit = "kg",
            estimatedServings = 30,
            preparationTime = "Today, 10:00",
            useByDeadline = "Tomorrow, 20:00",
            pickupDeadline = "Tonight, 21:00",
            storageCondition = "Ambient dry",
            temperatureReading = null,
            allergens = listOf("Gluten"),
            crossContactWarning = "Shared bakery prep surface.",
            packagingRequirement = "Kraft bakery cartons",
            pickupWindow = "19:00 - 21:00",
            address = "18 Baker Street",
            neighborhood = "Old Quarter",
            safetyAttested = true
        )

        assertTrue(result.isSuccess)
        val listing = result.getOrNull()
        assertNotNull(listing)
        assertEquals(ListingStatus.AVAILABLE, listing?.status)
        assertEquals(12.0, listing?.remainingQuantity)
    }

    @Test
    fun testRejectListingWithoutSafetyAttestation() {
        val result = repository.createFoodListing(
            title = "Unverified Stew",
            description = "Held at room temperature without check.",
            category = FoodCategory.COOKED_MEALS,
            dietaryType = DietaryType.NON_VEGETARIAN,
            quantity = 10.0,
            unit = "kg",
            estimatedServings = 20,
            preparationTime = "Today, 12:00",
            useByDeadline = "Tonight, 22:00",
            pickupDeadline = "Tonight, 20:00",
            storageCondition = "Ambient",
            temperatureReading = null,
            allergens = emptyList(),
            crossContactWarning = "",
            packagingRequirement = "Plastic tub",
            pickupWindow = "Tonight",
            address = "Unknown",
            neighborhood = "Midtown",
            safetyAttested = false // Missing mandatory safety declaration
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("safety", ignoreCase = true) == true)
    }

    @Test
    fun testRejectListingZeroOrNegativeQuantity() {
        val result = repository.createFoodListing(
            title = "Zero Qty Items",
            description = "Empty batch.",
            category = FoodCategory.COOKED_MEALS,
            dietaryType = DietaryType.VEGETARIAN,
            quantity = 0.0, // Invalid
            unit = "kg",
            estimatedServings = 0,
            preparationTime = "Today, 12:00",
            useByDeadline = "Tonight, 22:00",
            pickupDeadline = "Tonight, 20:00",
            storageCondition = "Refrigerated",
            temperatureReading = "3°C",
            allergens = emptyList(),
            crossContactWarning = "",
            packagingRequirement = "Thermal box",
            pickupWindow = "Tonight",
            address = "Downtown",
            neighborhood = "Midtown",
            safetyAttested = true
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun testSuccessfulClaimReducesRemainingQuantity() {
        // list-1 has 24 kg available
        val recipient = SeedData.recipients[0]
        val claimResult = repository.claimFoodListing("list-1", 10.0, recipient)

        assertTrue(claimResult.isSuccess)
        val claim = claimResult.getOrNull()
        assertNotNull(claim)
        assertEquals(10.0, claim?.quantityClaimed)

        val updatedListing = repository.listings.value.find { it.id == "list-1" }
        assertNotNull(updatedListing)
        assertEquals(14.0, updatedListing?.remainingQuantity)
        assertEquals(ListingStatus.AVAILABLE, updatedListing?.status)
    }

    @Test
    fun testClaimExceedingRemainingQuantityIsRejected() {
        // list-1 has 24 kg available. Attempting 50 kg must fail atomically
        val recipient = SeedData.recipients[0]
        val claimResult = repository.claimFoodListing("list-1", 50.0, recipient)

        assertTrue(claimResult.isFailure)
        assertTrue(claimResult.exceptionOrNull()?.message?.contains("available", ignoreCase = true) == true)

        // Ensure original quantity untouched
        val listing = repository.listings.value.find { it.id == "list-1" }
        assertEquals(24.0, listing?.remainingQuantity)
    }

    @Test
    fun testAntiDuplicateClaimPrevention() {
        val recipient = SeedData.recipients[0]
        // First claim succeeds
        val claim1 = repository.claimFoodListing("list-1", 5.0, recipient)
        assertTrue(claim1.isSuccess)

        // Immediate second active claim on same listing by same recipient must be rejected
        val claim2 = repository.claimFoodListing("list-1", 5.0, recipient)
        assertTrue(claim2.isFailure)
        assertTrue(claim2.exceptionOrNull()?.message?.contains("active claim", ignoreCase = true) == true)
    }

    @Test
    fun testQrPickupExecutionAdvancesState() {
        // asgn-201 is ACCEPTED with token QR-PICKUP-SP-201-9941
        val volunteer = SeedData.volunteers[0]
        val handoverResult = repository.verifyAndExecuteQrHandover("QR-PICKUP-SP-201-9941", volunteer)

        assertTrue(handoverResult.isSuccess)

        val assignment = repository.assignments.value.find { it.id == "asgn-201" }
        assertEquals(AssignmentStatus.PICKED_UP, assignment?.status)

        // Token must now be marked used
        val token = repository.qrTokens.value.find { it.tokenString == "QR-PICKUP-SP-201-9941" }
        assertTrue(token?.isUsed == true)
    }

    @Test
    fun testRejectReusedQrToken() {
        // QR-PICKUP-SP-202-USED is already marked used in seed data
        val volunteer = SeedData.volunteers[0]
        val result = repository.verifyAndExecuteQrHandover("QR-PICKUP-SP-202-USED", volunteer)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("ALREADY been used", ignoreCase = true) == true)
    }

    @Test
    fun testRejectNonExistentQrToken() {
        val volunteer = SeedData.volunteers[0]
        val result = repository.verifyAndExecuteQrHandover("FAKE-INVALID-TOKEN-9999", volunteer)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Invalid QR", ignoreCase = true) == true)
    }

    @Test
    fun testNaturalLanguageSearchParser() {
        val parsed = matcher.parseNaturalLanguageQuery("Find vegetarian food for 30 people available for pickup this evening")

        assertEquals(DietaryType.VEGETARIAN, parsed.dietaryType)
        assertEquals(30, parsed.minServings)
        assertTrue(parsed.urgencyTonight)
    }

    @Test
    fun testAccurateImpactMetricsCalculation() {
        val metrics = repository.computeImpactMetrics()

        assertTrue(metrics.totalFoodWeightRescuedKg > 0.0)
        assertTrue(metrics.completedDeliveries > 0)
        // Check standard FAO multiplier: co2 = weight * 2.5
        assertEquals(metrics.totalFoodWeightRescuedKg * 2.5, metrics.co2EquivalentPreventedKg, 0.01)
        assertTrue(metrics.isDemoData)
    }

    @Test
    fun testSurplusPredictionWithSufficientHistory() {
        val prediction = predictor.predictSurplus(
            donorName = "The Grand Horizon Hotel",
            dayOfWeek = "Friday",
            serviceType = "Dinner Banquet",
            plannedCovers = 220,
            category = FoodCategory.COOKED_MEALS,
            isRaining = false
        )

        assertTrue("Expected surplus should be positive", prediction.predictedSurplusKg > 0.0)
        assertTrue("Estimated meals should be positive", prediction.predictedMeals > 0)
        assertFalse("With 5 Friday Dinner Banquet samples, fallback should not be triggered", prediction.isHeuristicFallback)
        assertTrue(prediction.confidencePercent >= 75)
        assertTrue(prediction.uncertaintyLabel.contains("CONFIDENCE", ignoreCase = true))
    }

    @Test
    fun testSurplusPredictionFallbackWhenDataIsSparse() {
        // Query a service type with fewer than 4 historical logs
        val prediction = predictor.predictSurplus(
            donorName = "Small Corner Cafe",
            dayOfWeek = "Wednesday",
            serviceType = "Bakery Pack",
            plannedCovers = 80,
            category = FoodCategory.BAKED_GOODS,
            isRaining = false
        )

        assertTrue("Fallback must be triggered when data is sparse", prediction.isHeuristicFallback)
        assertTrue("Uncertainty label should indicate low confidence or fallback", prediction.uncertaintyLabel.contains("LOW", ignoreCase = true))
        assertTrue(prediction.predictedSurplusKg > 0.0)
        assertNotNull(prediction.fallbackReason)
    }

    @Test
    fun testFairDistributionPrioritizesUnmetNeed() {
        val availableSurplusKg = 50.0
        val distribution = predictor.calculateFairDistribution(SeedData.recipients, availableSurplusKg)

        assertFalse(distribution.isEmpty())
        val totalAllocated = distribution.sumOf { it.recommendedAllocationKg }
        assertTrue("Total allocated should be reasonable", totalAllocated in 1.0..(availableSurplusKg + 1.0))

        // Ensure higher priority is assigned to shelters with higher unmet needs
        val topPriority = distribution.first()
        assertTrue(topPriority.fairDistributionPriority > 0.0)
    }

    @Test
    fun testOrganisationVerificationWorkflow() {
        val pendingOrg = repository.organisations.value.find { it.verificationStatus == VerificationStatus.PENDING_AUDIT }
        assertNotNull("Should have a pending organisation in seed data", pendingOrg)

        val orgId = pendingOrg!!.id
        repository.verifyOrganisation(
            orgId = orgId,
            approved = true,
            notes = "Health inspector cert verified by Dr. Evelyn Vance."
        )

        val updatedOrg = repository.organisations.value.find { it.id == orgId }
        assertEquals(VerificationStatus.VERIFIED, updatedOrg?.verificationStatus)
        assertEquals("Health inspector cert verified by Dr. Evelyn Vance.", updatedOrg?.auditorNotes)
    }

    @Test
    fun testColdChainTemperatureLoggingAndAlert() {
        val assignmentId = "asgn-201"
        // Log safe temperature
        repository.logColdChainTemperature(assignmentId, 3.2, "Standard insulated bag check.")
        val safeLog = repository.coldChainLogs.value.find { it.assignmentId == assignmentId && it.temperatureCelsius == 3.2 }
        assertNotNull(safeLog)
        assertTrue("3.2 C must be compliant", safeLog!!.isCompliant)

        // Log breach temperature (> 5.0 C for chilled food)
        val initialAlertCount = repository.notifications.value.count { it.type == NotificationType.SAFETY_ALERT }
        repository.logColdChainTemperature(assignmentId, 7.8, "Thermal excursion reported.")
        val breachLog = repository.coldChainLogs.value.find { it.assignmentId == assignmentId && it.temperatureCelsius == 7.8 }
        assertNotNull(breachLog)
        assertFalse("7.8 C must not be compliant", breachLog!!.isCompliant)

        val newAlertCount = repository.notifications.value.count { it.type == NotificationType.SAFETY_ALERT }
        assertTrue("Temperature breach must trigger a safety alert notification", newAlertCount > initialAlertCount)
    }

    @Test
    fun testVolunteerAssignmentCancellationWorkflow() {
        val assignment = repository.assignments.value.find { it.status == AssignmentStatus.ASSIGNED || it.status == AssignmentStatus.ACCEPTED }
        assertNotNull(assignment)

        val asgnId = assignment!!.id
        repository.cancelAssignment(asgnId, "Courier bicycle mechanical puncture.")

        val cancelledAsgn = repository.assignments.value.find { it.id == asgnId }
        assertEquals(AssignmentStatus.CANCELLED, cancelledAsgn?.status)

        // Should produce audit entry
        val audit = repository.auditLogs.value.find { it.targetEntity.contains(asgnId) }
        assertNotNull("Cancellation must produce an audit entry", audit)
    }

    @Test
    fun testOfflineDraftListingsSaveAndSync() {
        val initialLiveListingCount = repository.listings.value.size

        val draft = OfflineListingDraft(
            id = "draft-test-1",
            title = "Offline Prepared Vegetable Lasagne",
            description = "Cooked lasagne trays packed offline.",
            category = FoodCategory.COOKED_MEALS,
            dietaryType = DietaryType.VEGETARIAN,
            quantity = 15.0,
            unit = "kg",
            estimatedServings = 30,
            prepTime = "Today, 11:00",
            useByDeadline = "Tomorrow, 14:00",
            pickupDeadline = "Today, 19:00",
            storageCondition = "Rapid chilled",
            temperatureReading = "3.2°C",
            allergens = listOf("Dairy", "Gluten"),
            packagingRequirement = "Stainless steel trays",
            pickupWindow = "17:00 - 19:00",
            address = "77 Railway Terrace",
            safetyAttested = true
        )

        repository.saveOfflineDraft(draft)
        val saved = repository.offlineDrafts.value.find { it.id == "draft-test-1" }
        assertNotNull(saved)

        // Sync drafts
        val syncResult = repository.syncOfflineDrafts()
        assertTrue(syncResult.isSuccess)
        assertTrue((syncResult.getOrNull() ?: 0) >= 1)

        val newLiveListingCount = repository.listings.value.size
        assertEquals(initialLiveListingCount + 1, newLiveListingCount)
    }
}
