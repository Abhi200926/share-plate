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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.adaptive.calculateWindowSizeClass
import com.example.ui.adaptive.WindowSizeClassType
import com.example.ui.adaptive.LocalWindowSizeClass
import com.example.ui.components.FoodListingCard
import com.example.ui.components.FoodListingTable
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun DonorScreen(
    donorUser: UserProfile,
    listings: List<FoodListing>,
    claims: List<RescueClaim>,
    assignments: List<RescueAssignment>,
    offlineDrafts: List<OfflineListingDraft> = emptyList(),
    onCreateListingClicked: () -> Unit,
    onListingClicked: (FoodListing) -> Unit,
    onShowPickupPass: (RescueAssignment) -> Unit,
    onSyncOfflineDrafts: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    var preferTableView by remember { mutableStateOf(true) }
    val donorListings = listings.filter { it.donorId == donorUser.id }

    val localSize = LocalWindowSizeClass.current
    BoxWithConstraints(modifier = modifier.fillMaxSize().testTag("donor_dashboard")) {
        val sizeClass = if (localSize != WindowSizeClassType.COMPACT) localSize else calculateWindowSizeClass(maxWidth)
        val isWide = sizeClass != WindowSizeClassType.COMPACT


        Column(modifier = Modifier.fillMaxSize()) {
            // Donor Header Bar
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
                                text = donorUser.organisationName,
                                fontSize = if (isWide) 22.sp else 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Head Chef / Kitchen Manager: ${donorUser.name}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFFD1FAE5),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Certified Kitchen", fontSize = 11.sp, color = Color(0xFF065F46), fontWeight = FontWeight.Bold)
                                }
                            }

                            if (isWide) {
                                Button(
                                    onClick = onCreateListingClicked,
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("btn_create_donation_wide")
                                ) {
                                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Donate Surplus Food", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats Row: 4 in a horizontal row on desktop, 2x2 on tablet/mobile
                    if (sizeClass == WindowSizeClassType.EXPANDED) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Active Offers", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    Text("${donorListings.count { it.isAvailable }}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Rescues Completed", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    Text("${donorUser.completedRescues}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Food Saved", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    Text("${donorListings.filter { it.status == ListingStatus.DELIVERED }.sumOf { it.quantity }.toInt()} kg", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CoralAccent)
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Meals Shared", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    Text("${donorListings.filter { it.status == ListingStatus.DELIVERED }.sumOf { it.estimatedServings }}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Active Offers", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        Text("${donorListings.count { it.isAvailable }}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Rescues Completed", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        Text("${donorUser.completedRescues}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Food Saved", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        Text("${donorListings.filter { it.status == ListingStatus.DELIVERED }.sumOf { it.quantity }.toInt()} kg", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CoralAccent)
                                    }
                                }

                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Meals Shared", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        Text("${donorListings.filter { it.status == ListingStatus.DELIVERED }.sumOf { it.estimatedServings }}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                                    }
                                }
                            }
                        }
                    }

                    if (!isWide) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onCreateListingClicked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_create_donation"),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AddCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Donate Surplus Food (New Listing)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Tabs Bar with Desktop View Mode Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.weight(1f)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("My Offers (${donorListings.size})", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Pickup Dispatches (${assignments.count { asgn -> donorListings.any { it.id == asgn.listingId } }})", fontWeight = FontWeight.SemiBold) }
                    )
                }

                if (isWide && selectedTab == 0) {
                    Row(
                        modifier = Modifier.padding(end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(onClick = { preferTableView = true }) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = "Table View",
                                tint = if (preferTableView) ForestGreenPrimary else MaterialTheme.colorScheme.outline
                            )
                        }
                        IconButton(onClick = { preferTableView = false }) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Grid View",
                                tint = if (!preferTableView) ForestGreenPrimary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                if (isWide && preferTableView) {
                    // Desktop Table View
                    Box(modifier = Modifier.padding(16.dp)) {
                        FoodListingTable(
                            listings = donorListings,
                            onListingClicked = onListingClicked,
                            onClaimClicked = null
                        )
                    }
                } else {
                    // Card View (Responsive: 2 or 3 columns on wide screens, 1 on mobile)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        if (offlineDrafts.isNotEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.CloudOff, contentDescription = null, tint = Color(0xFF92400E))
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text("${offlineDrafts.size} Offline Drafts Pending", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF92400E))
                                                Text("Saved locally while offline. Sync to publish live to shelters.", fontSize = 11.sp, color = Color(0xFF78350F))
                                            }
                                        }
                                        Button(
                                            onClick = onSyncOfflineDrafts,
                                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                                        ) {
                                            Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Sync Now", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }

                        if (donorListings.isEmpty()) {

                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(32.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(imageVector = Icons.Default.Kitchen, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("No Active Donations", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text("Post your end-of-service surplus to prevent food waste.", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }
                        } else {
                            if (isWide) {
                                val columnCount = if (sizeClass == WindowSizeClassType.EXPANDED) 3 else 2
                                val chunks = donorListings.chunked(columnCount)
                                items(chunks) { rowItems ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        rowItems.forEach { item ->
                                            Box(modifier = Modifier.weight(1f)) {
                                                FoodListingCard(
                                                    listing = item,
                                                    onCardClick = { onListingClicked(item) },
                                                    onClaimClick = null
                                                )
                                            }
                                        }
                                        for (i in 0 until (columnCount - rowItems.size)) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            } else {
                                items(donorListings) { listing ->
                                    FoodListingCard(
                                        listing = listing,
                                        onCardClick = { onListingClicked(listing) },
                                        onClaimClick = null
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Pickup Dispatches
                val relevantAssignments = assignments.filter { asgn ->
                    donorListings.any { it.id == asgn.listingId }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    if (relevantAssignments.isEmpty()) {
                        item {
                            Text(
                                text = "No pending dispatches right now.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        if (isWide) {
                            val chunks = relevantAssignments.chunked(2)
                            items(chunks) { rowAssignments ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    rowAssignments.forEach { asgn ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            DispatchCard(asgn = asgn, onShowPickupPass = onShowPickupPass)
                                        }
                                    }
                                    if (rowAssignments.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        } else {
                            items(relevantAssignments) { asgn ->
                                DispatchCard(asgn = asgn, onShowPickupPass = onShowPickupPass)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DispatchCard(
    asgn: RescueAssignment,
    onShowPickupPass: (RescueAssignment) -> Unit
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
                Text(text = asgn.listingTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Surface(
                    color = if (asgn.status == AssignmentStatus.DELIVERED) Color(0xFFD1FAE5) else Color(0xFFDBEAFE),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = asgn.status.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (asgn.status == AssignmentStatus.DELIVERED) Color(0xFF065F46) else Color(0xFF1E40AF),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Recipient: ${asgn.recipientOrg}", fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
            Text(text = "Assigned Volunteer: ${asgn.volunteerName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            Text(text = "Quantity: ${asgn.quantitySummary}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { onShowPickupPass(asgn) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.QrCode, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("View Handover Pass (${asgn.pickupQrToken})", fontSize = 12.sp)
            }
        }
    }
}
