package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FoodListing
import com.example.ui.adaptive.calculateWindowSizeClass
import com.example.ui.adaptive.LocalWindowSizeClass
import com.example.ui.adaptive.WindowSizeClassType
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun MapScreen(
    listings: List<FoodListing>,
    onListingClicked: (FoodListing) -> Unit,
    modifier: Modifier = Modifier
) {
    var isListView by remember { mutableStateOf(false) }
    var selectedListingId by remember { mutableStateOf<String?>(listings.firstOrNull()?.id) }

    val localSize = LocalWindowSizeClass.current
    BoxWithConstraints(modifier = modifier.fillMaxSize().testTag("map_screen")) {
        val sizeClass = if (localSize != WindowSizeClassType.COMPACT) localSize else calculateWindowSizeClass(maxWidth)
        val isWide = sizeClass != WindowSizeClassType.COMPACT

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rescue Radar & Logistics Hubs",
                            fontSize = if (isWide) 20.sp else 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isWide) "Interactive split-pane dispatch radar and accessible hub registry" else "Real-time dispatch locations & pickup docks",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    if (!isWide) {
                        // Phone View Mode Toggle
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
                                .padding(4.dp)
                        ) {
                            IconButton(
                                onClick = { isListView = false },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        if (!isListView) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = "Map Radar View",
                                    tint = if (!isListView) Color.White else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = { isListView = true },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        if (isListView) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                                    contentDescription = "Accessible List View",
                                    tint = if (isListView) Color.White else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (isWide) {
                // Desktop Split-Pane (60% Radar Map / 40% Accessible Hubs List)
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1.3f).fillMaxHeight()) {
                        RadarMapCanvas(
                            listings = listings,
                            selectedListingId = selectedListingId,
                            onSelectListing = { selectedListingId = it },
                            onListingClicked = onListingClicked
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        AccessibleHubsList(
                            listings = listings,
                            selectedListingId = selectedListingId,
                            onSelectListing = { selectedListingId = it },
                            onListingClicked = onListingClicked
                        )
                    }
                }
            } else {
                // Mobile View (Toggle between Map and List)
                if (!isListView) {
                    RadarMapCanvas(
                        listings = listings,
                        selectedListingId = selectedListingId,
                        onSelectListing = { selectedListingId = it },
                        onListingClicked = onListingClicked
                    )
                } else {
                    AccessibleHubsList(
                        listings = listings,
                        selectedListingId = selectedListingId,
                        onSelectListing = { selectedListingId = it },
                        onListingClicked = onListingClicked
                    )
                }
            }
        }
    }
}

@Composable
private fun RadarMapCanvas(
    listings: List<FoodListing>,
    selectedListingId: String?,
    onSelectListing: (String) -> Unit,
    onListingClicked: (FoodListing) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8ECE9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            listings.take(5).forEachIndexed { index, listing ->
                val isSelected = selectedListingId == listing.id
                val pinColor = when {
                    listing.isAvailable -> ForestGreenPrimary
                    else -> CoralAccent
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ((index * 24) % 60).dp),
                    horizontalArrangement = if (index % 2 == 0) Arrangement.Start else Arrangement.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) pinColor else Color.White,
                        shadowElevation = if (isSelected) 6.dp else 2.dp,
                        modifier = Modifier
                            .clickable { onSelectListing(listing.id) }
                            .border(
                                if (isSelected) 2.dp else 1.dp,
                                pinColor,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else pinColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${listing.donorOrg.take(16)}... (${listing.remainingQuantity} ${listing.unit})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }

        // Bottom Selected Preview Card
        val selected = listings.find { it.id == selectedListingId } ?: listings.firstOrNull()
        if (selected != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selected.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        StatusBadge(status = selected.status)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Kitchen: ${selected.donorOrg}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    Text(text = "Location: ${selected.location.address} (${selected.location.neighborhood})", fontSize = 12.sp)
                    Text(text = "Access Notes: ${selected.location.accessNotes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onListingClicked(selected) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("View Full Food & Safety Specs", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AccessibleHubsList(
    listings: List<FoodListing>,
    selectedListingId: String?,
    onSelectListing: (String) -> Unit,
    onListingClicked: (FoodListing) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(listings) { listing ->
            val isSelected = selectedListingId == listing.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelectListing(listing.id)
                        onListingClicked(listing)
                    }
                    .border(
                        if (isSelected) 2.dp else 0.dp,
                        if (isSelected) ForestGreenPrimary else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = listing.donorOrg, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        StatusBadge(status = listing.status)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Offer: ${listing.title} (${listing.quantity} ${listing.unit})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Address: ${listing.location.address}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    Text(text = "Access: ${listing.location.accessNotes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    Text(text = "Window: ${listing.pickupWindow}", fontSize = 11.sp, color = CoralAccent, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
