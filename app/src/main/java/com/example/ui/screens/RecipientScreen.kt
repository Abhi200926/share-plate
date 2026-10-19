package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun RecipientScreen(
    recipientUser: UserProfile,
    listings: List<FoodListing>,
    claims: List<RescueClaim>,
    assignments: List<RescueAssignment>,
    onClaimClicked: (FoodListing) -> Unit,
    onListingClicked: (FoodListing) -> Unit,
    onShowReceiveQr: (RescueAssignment) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedScreenTab by remember { mutableStateOf(0) } // 0: Available Feed, 1: My Claims
    var preferTableView by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDietary by remember { mutableStateOf<DietaryType?>(null) }
    var selectedCategory by remember { mutableStateOf<FoodCategory?>(null) }

    val myClaims = claims.filter { it.recipientId == recipientUser.id }

    val filteredListings = listings.filter { listing ->
        val matchesQuery = searchQuery.isBlank() ||
                listing.title.contains(searchQuery, ignoreCase = true) ||
                listing.description.contains(searchQuery, ignoreCase = true) ||
                listing.donorOrg.contains(searchQuery, ignoreCase = true)
        val matchesDietary = selectedDietary == null || listing.dietaryType == selectedDietary
        val matchesCategory = selectedCategory == null || listing.category == selectedCategory
        matchesQuery && matchesDietary && matchesCategory
    }

    val localSize = LocalWindowSizeClass.current
    BoxWithConstraints(modifier = modifier.fillMaxSize().testTag("recipient_dashboard")) {
        val sizeClass = if (localSize != WindowSizeClassType.COMPACT) localSize else calculateWindowSizeClass(maxWidth)
        val isWide = sizeClass != WindowSizeClassType.COMPACT

        Column(modifier = Modifier.fillMaxSize()) {
            // Recipient Org Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = recipientUser.organisationName,
                                fontSize = if (isWide) 20.sp else 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Verified Community Partner • ${recipientUser.name}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Surface(
                            color = Color(0xFFDBEAFE),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Verified Recipient NGO", fontSize = 11.sp, color = Color(0xFF1E40AF), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Impact Statistics: 4 in one horizontal row on desktop, 2x2 on tablet/mobile
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
                                    Text("Available Offers", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    Text("${listings.count { it.isAvailable }}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("My Active Claims", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    Text("${myClaims.count { it.status == ClaimStatus.APPROVED || it.status == ClaimStatus.PENDING }}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Meals Distributed", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    Text("${myClaims.filter { it.status == ClaimStatus.COMPLETED }.sumOf { (it.quantityClaimed * 2).toInt() }}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CoralAccent)
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Rescued Food", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    Text("${myClaims.filter { it.status == ClaimStatus.COMPLETED }.sumOf { it.quantityClaimed.toInt() }} kg", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
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
                                        Text("Available Offers", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        Text("${listings.count { it.isAvailable }}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Active Claims", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        Text("${myClaims.count { it.status == ClaimStatus.APPROVED || it.status == ClaimStatus.PENDING }}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
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
                                        Text("Meals Distributed", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        Text("${myClaims.filter { it.status == ClaimStatus.COMPLETED }.sumOf { (it.quantityClaimed * 2).toInt() }}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CoralAccent)
                                    }
                                }
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Rescued Food", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        Text("${myClaims.filter { it.status == ClaimStatus.COMPLETED }.sumOf { it.quantityClaimed.toInt() }} kg", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tabs Row with view toggle on desktop
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabRow(
                    selectedTabIndex = selectedScreenTab,
                    modifier = Modifier.weight(1f)
                ) {
                    Tab(
                        selected = selectedScreenTab == 0,
                        onClick = { selectedScreenTab = 0 },
                        text = { Text("Available Food (${listings.count { it.isAvailable }})", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedScreenTab == 1,
                        onClick = { selectedScreenTab = 1 },
                        text = { Text("My Reservations (${myClaims.size})", fontWeight = FontWeight.SemiBold) }
                    )
                }

                if (isWide && selectedScreenTab == 0) {
                    Row(
                        modifier = Modifier.padding(end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(onClick = { preferTableView = false }) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Grid View",
                                tint = if (!preferTableView) ForestGreenPrimary else MaterialTheme.colorScheme.outline
                            )
                        }
                        IconButton(onClick = { preferTableView = true }) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = "Table View",
                                tint = if (preferTableView) ForestGreenPrimary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            if (selectedScreenTab == 0) {
                if (isWide && preferTableView) {
                    // Desktop Table View
                    Box(modifier = Modifier.padding(16.dp)) {
                        FoodListingTable(
                            listings = listings.filter { it.isAvailable },
                            onListingClicked = onListingClicked,
                            onClaimClicked = onClaimClicked
                        )
                    }
                } else {
                    // Search and Filter Bar
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search surplus food, dietary type, or donor kitchen...") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("recipient_search_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Dietary Filters
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = selectedDietary == null,
                                onClick = { selectedDietary = null },
                                label = { Text("All Diets", fontSize = 11.sp) }
                            )
                            DietaryType.values().forEach { d ->
                                FilterChip(
                                    selected = selectedDietary == d,
                                    onClick = { selectedDietary = if (selectedDietary == d) null else d },
                                    label = { Text(d.label, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Category Filters
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = selectedCategory == null,
                                onClick = { selectedCategory = null },
                                label = { Text("All Categories", fontSize = 11.sp) }
                            )
                            FoodCategory.values().take(4).forEach { c ->
                                FilterChip(
                                    selected = selectedCategory == c,
                                    onClick = { selectedCategory = if (selectedCategory == c) null else c },
                                    label = { Text(c.label, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    // Food Cards (Responsive Multi-column Grid on Wide Screens)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        if (filteredListings.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(32.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(imageVector = Icons.Default.SearchOff, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("No Matching Food Offers", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text("Try adjusting dietary filters or search terms.", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }
                        } else {
                            if (isWide) {
                                // 2-column or 3-column grid depending on width
                                val columnCount = if (sizeClass == WindowSizeClassType.EXPANDED) 3 else 2
                                val chunks = filteredListings.chunked(columnCount)
                                items(chunks) { rowListings ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        rowListings.forEach { listing ->
                                            Box(modifier = Modifier.weight(1f)) {
                                                FoodListingCard(
                                                    listing = listing,
                                                    onCardClick = { onListingClicked(listing) },
                                                    onClaimClick = { onClaimClicked(listing) }
                                                )
                                            }
                                        }
                                        // Fill empty slots if last row has fewer items
                                        val remainder = columnCount - rowListings.size
                                        for (i in 0 until remainder) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            } else {
                                items(filteredListings) { listing ->
                                    FoodListingCard(
                                        listing = listing,
                                        onCardClick = { onListingClicked(listing) },
                                        onClaimClick = { onClaimClicked(listing) }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // My Reservations & Claims (Responsive Grid on Wide Screens)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    if (myClaims.isEmpty()) {
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
                                    Icon(imageVector = Icons.Default.Inbox, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No Active Claims", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Browse available food offers above to reserve portions for your shelter or kitchen.", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                }
                            }
                        }
                    } else {
                        if (isWide) {
                            val chunks = myClaims.chunked(2)
                            items(chunks) { rowClaims ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    rowClaims.forEach { claim ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            RecipientClaimCard(
                                                claim = claim,
                                                assignments = assignments,
                                                onShowReceiveQr = onShowReceiveQr
                                            )
                                        }
                                    }
                                    if (rowClaims.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        } else {
                            items(myClaims) { claim ->
                                RecipientClaimCard(
                                    claim = claim,
                                    assignments = assignments,
                                    onShowReceiveQr = onShowReceiveQr
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
private fun RecipientClaimCard(
    claim: RescueClaim,
    assignments: List<RescueAssignment>,
    onShowReceiveQr: (RescueAssignment) -> Unit
) {
    val relatedAssignment = assignments.find { it.claimId == claim.id }
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
                Text(text = claim.listingTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Surface(
                    color = if (claim.status == ClaimStatus.COMPLETED) Color(0xFFD1FAE5) else Color(0xFFDBEAFE),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = claim.status.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (claim.status == ClaimStatus.COMPLETED) Color(0xFF065F46) else Color(0xFF1E40AF),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Reserved Amount: ${claim.quantityClaimed} ${claim.unit}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ForestGreenPrimary
            )
            Text(
                text = "Instructions: ${claim.pickupInstructions}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )

            if (relatedAssignment != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Courier: ${relatedAssignment.volunteerName} (${relatedAssignment.status.label})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Drop-off Destination: ${relatedAssignment.dropoffAddress}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { onShowReceiveQr(relatedAssignment) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenLight),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.QrCode, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Show Delivery Receipt Pass (${relatedAssignment.deliveryQrToken})", fontSize = 12.sp)
                }
            }
        }
    }
}
