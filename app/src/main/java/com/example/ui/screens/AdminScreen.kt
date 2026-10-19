package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.adaptive.calculateWindowSizeClass
import com.example.ui.adaptive.WindowSizeClassType
import com.example.ui.adaptive.LocalWindowSizeClass
import com.example.ui.components.SafetyEmergencyNoticeCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun AdminScreen(
    adminUser: UserProfile,
    listings: List<FoodListing>,
    incidents: List<SafetyIncidentReport>,
    auditLogs: List<AuditEntry>,
    organisations: List<OrganisationVerificationRecord> = emptyList(),
    onResolveIncident: (incidentId: String, approvedSafe: Boolean) -> Unit,
    onVerifyOrganisation: (orgId: String, approved: Boolean, notes: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var adminTab by remember { mutableStateOf(0) }
    val pendingReviewListings = listings.filter { it.status == ListingStatus.PENDING_REVIEW }

    val localSize = LocalWindowSizeClass.current
    BoxWithConstraints(modifier = modifier.fillMaxSize().testTag("admin_dashboard")) {
        val sizeClass = if (localSize != WindowSizeClassType.COMPACT) localSize else calculateWindowSizeClass(maxWidth)
        val isWide = sizeClass != WindowSizeClassType.COMPACT

        Column(modifier = Modifier.fillMaxSize()) {
            // Admin Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Platform Governance & Food Safety",
                                fontSize = if (isWide) 22.sp else 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Auditor: ${adminUser.name} • Certified Safety Officer",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Surface(
                            color = Color(0xFFEDE9FE),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Admin Governance",
                                fontSize = 11.sp,
                                color = Color(0xFF6D28D9),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Tabs
            TabRow(selectedTabIndex = adminTab) {
                Tab(
                    selected = adminTab == 0,
                    onClick = { adminTab = 0 },
                    text = { Text("Safety Queue (${pendingReviewListings.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = adminTab == 1,
                    onClick = { adminTab = 1 },
                    text = { Text("Incidents (${incidents.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = adminTab == 2,
                    onClick = { adminTab = 2 },
                    text = { Text("Audit Trail", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = adminTab == 3,
                    onClick = { adminTab = 3 },
                    text = { Text("Organisations (${organisations.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
            }


            when (adminTab) {
                0 -> {
                    // Safety Review Queue (2-column on desktop/tablet)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            SafetyEmergencyNoticeCard()
                        }

                        if (pendingReviewListings.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Safety Queue Clean", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text("No food offers currently flagged or quarantined.", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }
                        } else {
                            if (isWide) {
                                val chunks = pendingReviewListings.chunked(2)
                                items(chunks) { rowListings ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        rowListings.forEach { listing ->
                                            Box(modifier = Modifier.weight(1f)) {
                                                SafetyReviewCard(listing = listing)
                                            }
                                        }
                                        if (rowListings.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            } else {
                                items(pendingReviewListings) { listing ->
                                    SafetyReviewCard(listing = listing)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Incidents List (2-column on desktop/tablet)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        if (isWide) {
                            val chunks = incidents.chunked(2)
                            items(chunks) { rowIncidents ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    rowIncidents.forEach { inc ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            IncidentItemCard(inc = inc, onResolve = { onResolveIncident(inc.id, true) })
                                        }
                                    }
                                    if (rowIncidents.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        } else {
                            items(incidents) { inc ->
                                IncidentItemCard(inc = inc, onResolve = { onResolveIncident(inc.id, true) })
                            }
                        }
                    }
                }
                2 -> {
                    // Audit Trail (Card or table on wide screens)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(auditLogs) { entry ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = entry.action,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ForestGreenPrimary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = entry.actorName,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = entry.details, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "Target: ${entry.targetEntity}", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Organisations & Verification Reviews
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Organisation Trust & Compliance Verification", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Only verified non-profits and licensed commercial food businesses can dispatch/claim high-risk perishables.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }
                        }

                        if (isWide) {
                            val chunks = organisations.chunked(2)
                            items(chunks) { rowOrgs ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    rowOrgs.forEach { org ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            OrganisationAuditCard(
                                                org = org,
                                                onApprove = { onVerifyOrganisation(org.id, true, "Verified health inspection and active registration.") },
                                                onReject = { onVerifyOrganisation(org.id, false, "Supplemental health inspection documents requested.") }
                                            )
                                        }
                                    }
                                    if (rowOrgs.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        } else {
                            items(organisations) { org ->
                                OrganisationAuditCard(
                                    org = org,
                                    onApprove = { onVerifyOrganisation(org.id, true, "Verified health inspection and active registration.") },
                                    onReject = { onVerifyOrganisation(org.id, false, "Supplemental health inspection documents requested.") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrganisationAuditCard(
    org: OrganisationVerificationRecord,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = org.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                val (badgeBg, badgeFg) = when (org.verificationStatus) {
                    VerificationStatus.VERIFIED -> Color(0xFFD1FAE5) to Color(0xFF065F46)
                    VerificationStatus.PENDING_AUDIT -> Color(0xFFFEF3C7) to Color(0xFF92400E)
                    VerificationStatus.REJECTED -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
                }
                Surface(color = badgeBg, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = org.verificationStatus.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeFg,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Role: ${org.role.displayName} • Reg: ${org.registrationNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
            Text(text = "Hygiene: ${org.foodHygieneRating}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "Cold Chain: ${if (org.coldChainCertified) "Certified Refrigerated Transit" else "Ambient / Dry Only"}", fontSize = 11.sp, color = if (org.coldChainCertified) ForestGreenPrimary else CoralAccent)
            Text(text = "Contact: ${org.contactPerson} (${org.contactEmail})", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

            if (org.auditorNotes != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Notes: ${org.auditorNotes}", fontSize = 11.sp, modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (org.verificationStatus == VerificationStatus.PENDING_AUDIT) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Grant Verified Badge", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Request Docs", fontSize = 11.sp, color = Color.Red)
                    }
                }
            }
        }
    }
}


@Composable
private fun SafetyReviewCard(listing: FoodListing) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = listing.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                StatusBadge(status = listing.status)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Donor: ${listing.donorOrg}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
            Text(text = "Temperature reading: ${listing.temperatureReading ?: "None recorded"}", fontSize = 12.sp, color = CoralAccent, fontWeight = FontWeight.SemiBold)

            if (listing.safetyReviewNotes != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Auditor Flag: ${listing.safetyReviewNotes}",
                        fontSize = 11.sp,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* In demo, re-approves */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Approve Batch", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { /* Rejects */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Decommission", fontSize = 12.sp, color = Color.Red)
                }
            }
        }
    }
}

@Composable
private fun IncidentItemCard(
    inc: SafetyIncidentReport,
    onResolve: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = inc.type.label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFDC2626))
                Surface(
                    color = if (inc.status == IncidentStatus.RESOLVED) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = inc.status.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (inc.status == IncidentStatus.RESOLVED) Color(0xFF065F46) else Color(0xFF991B1B),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Subject: ${inc.listingTitle}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "Reporter: ${inc.reporterName} (${inc.reporterRole.displayName})", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = inc.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (inc.status != IncidentStatus.RESOLVED) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onResolve,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Complete Investigation & Mark Resolved", fontSize = 12.sp)
                }
            }
        }
    }
}
