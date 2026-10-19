package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class SharePlateRepository {

    // Current active simulated session
    private val _currentRole = MutableStateFlow(UserRole.RECIPIENT)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentUser = MutableStateFlow(SeedData.recipients[0])
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _isDemoMode = MutableStateFlow(true)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    // Core entity reactive states
    private val _listings = MutableStateFlow<List<FoodListing>>(emptyList())
    val listings: StateFlow<List<FoodListing>> = _listings.asStateFlow()

    private val _claims = MutableStateFlow<List<RescueClaim>>(emptyList())
    val claims: StateFlow<List<RescueClaim>> = _claims.asStateFlow()

    private val _assignments = MutableStateFlow<List<RescueAssignment>>(emptyList())
    val assignments: StateFlow<List<RescueAssignment>> = _assignments.asStateFlow()

    private val _qrTokens = MutableStateFlow<List<QrToken>>(emptyList())
    val qrTokens: StateFlow<List<QrToken>> = _qrTokens.asStateFlow()

    private val _incidents = MutableStateFlow<List<SafetyIncidentReport>>(emptyList())
    val incidents: StateFlow<List<SafetyIncidentReport>> = _incidents.asStateFlow()

    private val _notifications = MutableStateFlow<List<SharePlateNotification>>(emptyList())
    val notifications: StateFlow<List<SharePlateNotification>> = _notifications.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditEntry>>(emptyList())
    val auditLogs: StateFlow<List<AuditEntry>> = _auditLogs.asStateFlow()

    private val _organisations = MutableStateFlow<List<OrganisationVerificationRecord>>(emptyList())
    val organisations: StateFlow<List<OrganisationVerificationRecord>> = _organisations.asStateFlow()

    private val _offlineDrafts = MutableStateFlow<List<OfflineListingDraft>>(emptyList())
    val offlineDrafts: StateFlow<List<OfflineListingDraft>> = _offlineDrafts.asStateFlow()

    private val _coldChainLogs = MutableStateFlow<List<ColdChainLogEntry>>(emptyList())
    val coldChainLogs: StateFlow<List<ColdChainLogEntry>> = _coldChainLogs.asStateFlow()

    private val lock = Any()

    init {
        resetToDemoData()
    }

    fun switchRole(role: UserRole) {
        _currentRole.value = role
        _currentUser.value = when (role) {
            UserRole.DONOR -> SeedData.donors[0]
            UserRole.RECIPIENT -> SeedData.recipients[0]
            UserRole.VOLUNTEER -> SeedData.volunteers[0]
            UserRole.ADMINISTRATOR -> SeedData.admin
        }
    }

    fun resetToDemoData() {
        synchronized(lock) {
            _listings.value = SeedData.createInitialListings()
            _claims.value = SeedData.createInitialClaims()
            _assignments.value = SeedData.createInitialAssignments()
            _qrTokens.value = SeedData.createInitialQrTokens()
            _incidents.value = SeedData.createInitialIncidents()
            _notifications.value = SeedData.createInitialNotifications()
            _auditLogs.value = SeedData.createInitialAuditLogs()
            _organisations.value = SeedData.createInitialOrganisationRecords()
            _offlineDrafts.value = emptyList()
            _coldChainLogs.value = emptyList()
        }
    }


    // --- CONCURRENCY-PROTECTED CLAIM TRANSACTION ---
    fun claimFoodListing(
        listingId: String,
        requestedQuantity: Double,
        recipient: UserProfile = _currentUser.value
    ): Result<RescueClaim> {
        synchronized(lock) {
            val listing = _listings.value.find { it.id == listingId }
                ?: return Result.failure(IllegalArgumentException("Food listing not found."))

            if (listing.status != ListingStatus.AVAILABLE) {
                return Result.failure(IllegalStateException("Listing is not available for claim (Current status: ${listing.status.label})."))
            }

            // Anti-duplicate check
            val existingActiveClaim = _claims.value.find {
                it.listingId == listingId && it.recipientId == recipient.id &&
                        (it.status == ClaimStatus.APPROVED || it.status == ClaimStatus.PENDING)
            }
            if (existingActiveClaim != null) {
                return Result.failure(IllegalStateException("You already have an active claim for this listing."))
            }

            // Concurrency quantity boundary check
            if (requestedQuantity <= 0.0) {
                return Result.failure(IllegalArgumentException("Quantity must be greater than zero."))
            }

            if (requestedQuantity > listing.remainingQuantity) {
                return Result.failure(
                    IllegalStateException(
                        "Cannot reserve $requestedQuantity ${listing.unit}. Only ${listing.remainingQuantity} ${listing.unit} currently available."
                    )
                )
            }

            val newReserved = listing.totalReservedUnits + requestedQuantity
            val newStatus = if (newReserved >= listing.quantity) ListingStatus.RESERVED else ListingStatus.AVAILABLE

            // Atomic update to listing
            _listings.value = _listings.value.map {
                if (it.id == listingId) {
                    it.copy(totalReservedUnits = newReserved, status = newStatus)
                } else it
            }

            // Create claim
            val claimId = "claim-" + UUID.randomUUID().toString().take(8)
            val newClaim = RescueClaim(
                id = claimId,
                listingId = listingId,
                listingTitle = listing.title,
                recipientId = recipient.id,
                recipientName = recipient.name,
                recipientOrg = recipient.organisationName,
                quantityClaimed = requestedQuantity,
                unit = listing.unit,
                status = ClaimStatus.APPROVED
            )
            _claims.value = listOf(newClaim) + _claims.value

            // Generate Pickup & Delivery QR tokens
            val pickupTokenStr = "QR-PICKUP-${claimId.takeLast(4)}-${System.currentTimeMillis().toString().takeLast(4)}"
            val dropTokenStr = "QR-DROP-${claimId.takeLast(4)}-${System.currentTimeMillis().toString().takeLast(4)}"

            val assignmentId = "asgn-" + UUID.randomUUID().toString().take(8)
            val volunteer = SeedData.volunteers[0] // assign default ready volunteer

            val newAssignment = RescueAssignment(
                id = assignmentId,
                claimId = claimId,
                listingId = listingId,
                volunteerId = volunteer.id,
                volunteerName = volunteer.name,
                status = AssignmentStatus.ASSIGNED,
                listingTitle = listing.title,
                donorOrg = listing.donorOrg,
                pickupAddress = listing.location.address + " (" + listing.location.neighborhood + ")",
                recipientOrg = recipient.organisationName,
                dropoffAddress = recipient.address,
                quantitySummary = "$requestedQuantity ${listing.unit} (~${(requestedQuantity * 2.5).toInt()} servings)",
                pickupQrToken = pickupTokenStr,
                deliveryQrToken = dropTokenStr
            )
            _assignments.value = listOf(newAssignment) + _assignments.value

            _qrTokens.value = _qrTokens.value + listOf(
                QrToken(
                    tokenString = pickupTokenStr,
                    assignmentId = assignmentId,
                    listingId = listingId,
                    type = TokenType.PICKUP,
                    issuedForUser = listing.donorId
                ),
                QrToken(
                    tokenString = dropTokenStr,
                    assignmentId = assignmentId,
                    listingId = listingId,
                    type = TokenType.DELIVERY,
                    issuedForUser = recipient.id
                )
            )

            // Audit & Notifications
            addAuditLog(
                action = "CLAIM_COMMITTED",
                target = "Listing:$listingId",
                details = "Allocated $requestedQuantity ${listing.unit} to ${recipient.organisationName}."
            )
            addNotification(
                title = "Food Claim Confirmed",
                message = "Reserved $requestedQuantity ${listing.unit} of '${listing.title}'. Volunteer assigned.",
                type = NotificationType.NEW_CLAIM,
                role = UserRole.RECIPIENT
            )

            return Result.success(newClaim)
        }
    }

    // --- STRICT FOOD SAFETY VALIDATED LISTING CREATION ---
    fun createFoodListing(
        title: String,
        description: String,
        category: FoodCategory,
        dietaryType: DietaryType,
        quantity: Double,
        unit: String,
        estimatedServings: Int,
        preparationTime: String,
        useByDeadline: String,
        pickupDeadline: String,
        storageCondition: String,
        temperatureReading: String?,
        allergens: List<String>,
        crossContactWarning: String,
        packagingRequirement: String,
        pickupWindow: String,
        address: String,
        neighborhood: String,
        safetyAttested: Boolean,
        donor: UserProfile = _currentUser.value
    ): Result<FoodListing> {
        synchronized(lock) {
            if (title.isBlank() || description.isBlank()) {
                return Result.failure(IllegalArgumentException("Title and description are required."))
            }
            if (quantity <= 0.0) {
                return Result.failure(IllegalArgumentException("Quantity must be greater than zero."))
            }
            if (!safetyAttested) {
                return Result.failure(IllegalStateException("Food safety attestation is mandatory to publish surplus listings."))
            }
            if (pickupDeadline.isBlank() || useByDeadline.isBlank()) {
                return Result.failure(IllegalArgumentException("Both pickup deadline and use-by deadline must be specified."))
            }

            val newId = "list-" + UUID.randomUUID().toString().take(8)
            val newListing = FoodListing(
                id = newId,
                donorId = donor.id,
                donorName = donor.name,
                donorOrg = donor.organisationName,
                title = title.trim(),
                description = description.trim(),
                category = category,
                dietaryType = dietaryType,
                quantity = quantity,
                unit = unit,
                estimatedServings = estimatedServings,
                preparationTime = preparationTime,
                useByDeadline = useByDeadline,
                pickupDeadline = pickupDeadline,
                storageCondition = storageCondition,
                temperatureReading = temperatureReading,
                allergens = allergens,
                crossContactWarning = crossContactWarning,
                packagingRequirement = packagingRequirement,
                location = LocationCoord(address, neighborhood, 37.7749, -122.4194),
                pickupWindow = pickupWindow,
                safetyAttested = true,
                status = ListingStatus.AVAILABLE
            )

            _listings.value = listOf(newListing) + _listings.value

            addAuditLog(
                action = "LISTING_PUBLISHED",
                target = "Listing:$newId",
                details = "Donor ${donor.name} created surplus food offer: $title ($quantity $unit)."
            )
            addNotification(
                title = "New Donation Listed",
                message = "Your surplus offer '$title' is live and matching with nearby recipient organizations.",
                type = NotificationType.NEW_CLAIM,
                role = UserRole.DONOR
            )

            return Result.success(newListing)
        }
    }

    // --- QR HANDOVER VERIFICATION WITH SINGLE-USE & EXPIRY CHECKS ---
    fun verifyAndExecuteQrHandover(
        tokenString: String,
        volunteer: UserProfile = _currentUser.value
    ): Result<String> {
        synchronized(lock) {
            val token = _qrTokens.value.find { it.tokenString.trim().equals(tokenString.trim(), ignoreCase = true) }
                ?: return Result.failure(IllegalArgumentException("Invalid QR verification token. Code does not exist in registry."))

            if (token.isUsed) {
                return Result.failure(IllegalStateException("Token has ALREADY been used. Duplicate handover execution is rejected."))
            }

            if (System.currentTimeMillis() > token.expiresAt) {
                return Result.failure(IllegalStateException("QR token has expired. Please request a fresh authorization token."))
            }

            val assignment = _assignments.value.find { it.id == token.assignmentId }
                ?: return Result.failure(IllegalStateException("Associated rescue assignment record not found."))

            return when (token.type) {
                TokenType.PICKUP -> {
                    // State machine guard: can only pick up if ASSIGNED or ACCEPTED
                    if (assignment.status != AssignmentStatus.ASSIGNED && assignment.status != AssignmentStatus.ACCEPTED) {
                        return Result.failure(IllegalStateException("Invalid transition: Assignment cannot be marked Picked Up from state '${assignment.status.label}'."))
                    }

                    // Mark token used
                    _qrTokens.value = _qrTokens.value.map {
                        if (it.tokenString == token.tokenString) it.copy(isUsed = true, usedAt = System.currentTimeMillis()) else it
                    }

                    // Advance assignment to PICKED_UP
                    _assignments.value = _assignments.value.map {
                        if (it.id == assignment.id) it.copy(status = AssignmentStatus.PICKED_UP, pickedUpAt = System.currentTimeMillis()) else it
                    }

                    // Advance listing to COLLECTED
                    _listings.value = _listings.value.map {
                        if (it.id == assignment.listingId) it.copy(status = ListingStatus.COLLECTED) else it
                    }

                    addAuditLog(
                        action = "PICKUP_HANDOVER_VERIFIED",
                        target = "Assignment:${assignment.id}",
                        details = "Volunteer ${volunteer.name} verified pickup QR from donor ${assignment.donorOrg}."
                    )
                    addNotification(
                        title = "Surplus Picked Up",
                        message = "Rescued ${assignment.quantitySummary} from ${assignment.donorOrg}. En route to ${assignment.recipientOrg}.",
                        type = NotificationType.PICKUP_CONFIRMED,
                        role = UserRole.VOLUNTEER
                    )

                    Result.success("Pickup Handover Verified! Food safely transferred into thermal transit.")
                }
                TokenType.DELIVERY -> {
                    // State machine guard: can only deliver if PICKED_UP
                    if (assignment.status != AssignmentStatus.PICKED_UP) {
                        return Result.failure(IllegalStateException("Invalid transition: Assignment must be in 'PICKED_UP' state prior to delivery confirmation."))
                    }

                    // Mark token used
                    _qrTokens.value = _qrTokens.value.map {
                        if (it.tokenString == token.tokenString) it.copy(isUsed = true, usedAt = System.currentTimeMillis()) else it
                    }

                    // Advance assignment to DELIVERED
                    _assignments.value = _assignments.value.map {
                        if (it.id == assignment.id) it.copy(status = AssignmentStatus.DELIVERED, deliveredAt = System.currentTimeMillis()) else it
                    }

                    // Advance listing to DELIVERED
                    _listings.value = _listings.value.map {
                        if (it.id == assignment.listingId) it.copy(status = ListingStatus.DELIVERED) else it
                    }

                    // Complete claim
                    _claims.value = _claims.value.map {
                        if (it.id == assignment.claimId) it.copy(status = ClaimStatus.COMPLETED) else it
                    }

                    addAuditLog(
                        action = "DELIVERY_HANDOVER_VERIFIED",
                        target = "Assignment:${assignment.id}",
                        details = "Volunteer ${volunteer.name} delivered surplus to recipient ${assignment.recipientOrg}."
                    )
                    addNotification(
                        title = "Rescue Complete!",
                        message = "Delivered ${assignment.quantitySummary} to ${assignment.recipientOrg}. Impact recorded in live ledger.",
                        type = NotificationType.DELIVERY_COMPLETED,
                        role = UserRole.VOLUNTEER
                    )

                    Result.success("Delivery Handover Verified! Food safely received. Impact ledger updated.")
                }
            }
        }
    }

    // --- VOLUNTEER WORKFLOW ACTIONS ---
    fun acceptAssignment(assignmentId: String) {
        synchronized(lock) {
            _assignments.value = _assignments.value.map {
                if (it.id == assignmentId && it.status == AssignmentStatus.ASSIGNED) {
                    it.copy(status = AssignmentStatus.ACCEPTED)
                } else it
            }
            addAuditLog(
                action = "ASSIGNMENT_ACCEPTED",
                target = "Assignment:$assignmentId",
                details = "Volunteer ${_currentUser.value.name} accepted transport duty."
            )
        }
    }

    fun cancelAssignment(assignmentId: String, reason: String): Result<Boolean> {
        synchronized(lock) {
            val asgn = _assignments.value.find { it.id == assignmentId }
                ?: return Result.failure(IllegalArgumentException("Assignment not found."))

            _assignments.value = _assignments.value.map {
                if (it.id == assignmentId) it.copy(status = AssignmentStatus.CANCELLED) else it
            }

            // Restore listing availability
            _listings.value = _listings.value.map {
                if (it.id == asgn.listingId) {
                    val restoredReserved = (it.totalReservedUnits - 10.0).coerceAtLeast(0.0)
                    it.copy(totalReservedUnits = restoredReserved, status = ListingStatus.AVAILABLE)
                } else it
            }

            addAuditLog(
                action = "ASSIGNMENT_CANCELLED",
                target = "Assignment:$assignmentId",
                details = "Volunteer cancelled run. Reason: $reason. Listing restored to network."
            )
            addNotification(
                title = "Rescue Mission Re-routed",
                message = "Volunteer transport was cancelled ($reason). Listing re-queued for dispatch.",
                type = NotificationType.SYSTEM_ALERT,
                role = UserRole.ADMINISTRATOR
            )
            return Result.success(true)
        }
    }

    fun logColdChainTemperature(
        assignmentId: String,
        tempCelsius: Double,
        notes: String
    ): Result<Boolean> {
        synchronized(lock) {
            val asgn = _assignments.value.find { it.id == assignmentId }
                ?: return Result.failure(IllegalArgumentException("Assignment not found."))

            val listing = _listings.value.find { it.id == asgn.listingId }
            val isRefrigerated = listing?.storageCondition?.contains("refrig", ignoreCase = true) == true
            val isHot = listing?.storageCondition?.contains("hot", ignoreCase = true) == true ||
                    listing?.storageCondition?.contains("steam", ignoreCase = true) == true

            // Cold chain safety rules: Refrigerated <= 5.0 C, Hot >= 60.0 C
            val isCompliant = when {
                isRefrigerated -> tempCelsius <= 5.0
                isHot -> tempCelsius >= 60.0
                else -> tempCelsius in 0.0..25.0
            }

            val entry = ColdChainLogEntry(
                id = "ccl-" + UUID.randomUUID().toString().take(8),
                assignmentId = assignmentId,
                recordedBy = _currentUser.value.name,
                temperatureCelsius = tempCelsius,
                isCompliant = isCompliant,
                notes = notes
            )
            _coldChainLogs.value = listOf(entry) + _coldChainLogs.value

            addAuditLog(
                action = if (isCompliant) "COLD_CHAIN_LOGGED" else "COLD_CHAIN_BREACH_ALERT",
                target = "Assignment:$assignmentId",
                details = "Temp reading: ${tempCelsius}°C (${if (isCompliant) "Compliant" else "CRITICAL BREACH"}). Notes: $notes"
            )

            if (!isCompliant) {
                // Auto-escalate safety incident
                submitSafetyIncident(
                    listingId = asgn.listingId,
                    listingTitle = asgn.listingTitle,
                    type = IncidentType.TEMPERATURE_ABUSE,
                    description = "Transit temperature breach recorded by courier (${tempCelsius}°C). Safety quarantine enforced."
                )
            }

            return Result.success(isCompliant)
        }
    }

    // --- ORGANISATION VERIFICATION WORKFLOW ---
    fun verifyOrganisation(orgId: String, approved: Boolean, notes: String) {
        synchronized(lock) {
            val org = _organisations.value.find { it.id == orgId } ?: return
            val newStatus = if (approved) VerificationStatus.VERIFIED else VerificationStatus.REJECTED

            _organisations.value = _organisations.value.map {
                if (it.id == orgId) {
                    it.copy(
                        verificationStatus = newStatus,
                        auditorNotes = notes
                    )
                } else it
            }

            addAuditLog(
                action = if (approved) "ORGANISATION_VERIFIED" else "ORGANISATION_REJECTED",
                target = "Organisation:${org.name}",
                details = "Auditor ${_currentUser.value.name} marked organisation as ${newStatus.label}. Reason: $notes"
            )
            addNotification(
                title = "Organisation Verification Updated",
                message = "${org.name} verification status updated to ${newStatus.label}.",
                type = NotificationType.SYSTEM_ALERT,
                role = UserRole.ADMINISTRATOR
            )
        }
    }

    // --- OFFLINE-FRIENDLY DRAFT LISTINGS & SYNC RETRY ---
    fun saveOfflineDraft(draft: OfflineListingDraft) {
        synchronized(lock) {
            _offlineDrafts.value = listOf(draft) + _offlineDrafts.value
            addNotification(
                title = "Draft Saved Offline",
                message = "Surplus offer '${draft.title}' stored locally on device. Will auto-sync when online.",
                type = NotificationType.SYSTEM_ALERT,
                role = UserRole.DONOR
            )
        }
    }

    fun syncOfflineDrafts(): Result<Int> {
        synchronized(lock) {
            val pendingDrafts = _offlineDrafts.value
            if (pendingDrafts.isEmpty()) return Result.success(0)

            var publishedCount = 0
            for (draft in pendingDrafts) {
                val createResult = createFoodListing(
                    title = draft.title,
                    description = draft.description,
                    category = draft.category,
                    dietaryType = draft.dietaryType,
                    quantity = draft.quantity,
                    unit = draft.unit,
                    estimatedServings = draft.estimatedServings,
                    preparationTime = draft.prepTime,
                    useByDeadline = draft.useByDeadline,
                    pickupDeadline = draft.pickupDeadline,
                    storageCondition = draft.storageCondition,
                    temperatureReading = draft.temperatureReading,
                    allergens = draft.allergens,
                    crossContactWarning = "Offline synchronized listing.",
                    packagingRequirement = draft.packagingRequirement,
                    pickupWindow = draft.pickupWindow,
                    address = draft.address,
                    neighborhood = "Midtown",
                    safetyAttested = draft.safetyAttested
                )
                if (createResult.isSuccess) {
                    publishedCount++
                }
            }

            _offlineDrafts.value = emptyList()
            addAuditLog(
                action = "OFFLINE_DRAFTS_SYNCED",
                target = "BatchSync",
                details = "Successfully synchronized $publishedCount offline draft listings to cloud network."
            )
            return Result.success(publishedCount)
        }
    }


    // --- INCIDENT REPORTING & AUDIT ESCALATION ---
    fun submitSafetyIncident(
        listingId: String,
        listingTitle: String,
        type: IncidentType,
        description: String
    ) {
        synchronized(lock) {
            val incidentId = "inc-" + UUID.randomUUID().toString().take(8)
            val incident = SafetyIncidentReport(
                id = incidentId,
                listingId = listingId,
                listingTitle = listingTitle,
                reporterId = _currentUser.value.id,
                reporterName = _currentUser.value.name,
                reporterRole = _currentRole.value,
                type = type,
                description = description
            )
            _incidents.value = listOf(incident) + _incidents.value

            // Automatically place listing on hold
            _listings.value = _listings.value.map {
                if (it.id == listingId) {
                    it.copy(status = ListingStatus.PENDING_REVIEW, safetyReviewNotes = "Safety incident reported: ${type.label}. On hold.")
                } else it
            }

            addAuditLog(
                action = "SAFETY_INCIDENT_REPORTED",
                target = "Incident:$incidentId",
                details = "${type.label}: $description"
            )
            addNotification(
                title = "Safety Alert Logged",
                message = "Incident reported on '$listingTitle'. Listing placed under administrative review.",
                type = NotificationType.SAFETY_ALERT,
                role = UserRole.ADMINISTRATOR
            )
        }
    }

    fun resolveIncident(incidentId: String, approvedSafe: Boolean) {
        synchronized(lock) {
            val inc = _incidents.value.find { it.id == incidentId } ?: return
            _incidents.value = _incidents.value.map {
                if (it.id == incidentId) it.copy(status = IncidentStatus.RESOLVED) else it
            }
            _listings.value = _listings.value.map {
                if (it.id == inc.listingId) {
                    it.copy(status = if (approvedSafe) ListingStatus.AVAILABLE else ListingStatus.REJECTED)
                } else it
            }
            addAuditLog(
                action = "INCIDENT_RESOLVED",
                target = "Incident:$incidentId",
                details = "Auditor set status to ${if (approvedSafe) "RE-APPROVED" else "REJECTED"}."
            )
        }
    }

    // --- IMPACT METRICS COMPUTATION (Strictly from verified deliveries) ---
    fun computeImpactMetrics(): ImpactMetrics {
        synchronized(lock) {
            val deliveredListings = _listings.value.filter { it.status == ListingStatus.DELIVERED }
            val totalKgRescued = deliveredListings.sumOf { it.quantity }
            val servingsRescued = deliveredListings.sumOf { it.estimatedServings }
            val completedDeliveries = _assignments.value.count { it.status == AssignmentStatus.DELIVERED }
            val activeOpportunities = _listings.value.count { it.isAvailable }
            val uncollectedOrExpired = _listings.value.count { it.status == ListingStatus.EXPIRED || it.status == ListingStatus.CANCELLED }

            // Documented UN FAO Factor: 1 kg food waste prevented = 2.5 kg CO2e emissions avoided
            val co2PreventedKg = totalKgRescued * 2.5

            val totalStarted = (_claims.value.size).coerceAtLeast(1)
            val completionRate = ((completedDeliveries.toDouble() / totalStarted) * 100.0).coerceIn(0.0, 100.0)

            return ImpactMetrics(
                totalFoodWeightRescuedKg = totalKgRescued,
                completedDeliveries = completedDeliveries,
                estimatedServingsDelivered = servingsRescued,
                activeRescueOpportunities = activeOpportunities,
                uncollectedOrExpiredCount = uncollectedOrExpired,
                activeDonorCount = SeedData.donors.size,
                activeRecipientCount = SeedData.recipients.size,
                activeVolunteerCount = SeedData.volunteers.size,
                rescueCompletionRatePercent = completionRate,
                co2EquivalentPreventedKg = co2PreventedKg,
                isDemoData = _isDemoMode.value
            )
        }
    }

    private fun addAuditLog(action: String, target: String, details: String) {
        val entry = AuditEntry(
            id = "aud-" + UUID.randomUUID().toString().take(8),
            action = action,
            actorName = _currentUser.value.name,
            actorRole = _currentRole.value,
            targetEntity = target,
            details = details
        )
        _auditLogs.value = listOf(entry) + _auditLogs.value
    }

    private fun addNotification(title: String, message: String, type: NotificationType, role: UserRole) {
        val notif = SharePlateNotification(
            id = "notif-" + UUID.randomUUID().toString().take(8),
            title = title,
            message = message,
            type = type,
            targetRole = role
        )
        _notifications.value = listOf(notif) + _notifications.value
    }
}
