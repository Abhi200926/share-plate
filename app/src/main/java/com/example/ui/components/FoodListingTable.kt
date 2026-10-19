package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun FoodListingTable(
    listings: List<FoodListing>,
    onListingClicked: (FoodListing) -> Unit,
    onClaimClicked: ((FoodListing) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var sortAscending by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<FoodCategory?>(null) }
    var currentPage by remember { mutableStateOf(0) }
    val pageSize = 6

    val filteredListings = listings.filter { item ->
        val matchesQuery = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.donorOrg.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || item.category == selectedCategory
        matchesQuery && matchesCategory
    }.let { list ->
        if (sortAscending) list.sortedBy { it.remainingQuantity } else list.sortedByDescending { it.remainingQuantity }
    }

    val totalPages = (filteredListings.size + pageSize - 1) / pageSize
    val displayedPage = if (totalPages > 0) currentPage.coerceIn(0, totalPages - 1) else 0
    val pagedItems = filteredListings.drop(displayedPage * pageSize).take(pageSize)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("desktop_food_table"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Table Controls Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        currentPage = 0
                    },
                    placeholder = { Text("Filter table by title or kitchen...", fontSize = 13.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .widthIn(min = 260.dp, max = 400.dp)
                        .height(48.dp)
                        .testTag("table_search_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Filter Dropdown
                    var showCatDropdown by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { showCatDropdown = true },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(selectedCategory?.label ?: "All Categories", fontSize = 12.sp)
                        }

                        DropdownMenu(
                            expanded = showCatDropdown,
                            onDismissRequest = { showCatDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Categories") },
                                onClick = {
                                    selectedCategory = null
                                    showCatDropdown = false
                                }
                            )
                            FoodCategory.values().forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.label) },
                                    onClick = {
                                        selectedCategory = cat
                                        showCatDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Sort Toggle
                    OutlinedButton(
                        onClick = { sortAscending = !sortAscending },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.SwapVert, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (sortAscending) "Qty: Low to High" else "Qty: High to Low", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Table Container with horizontal scrolling for responsive integrity
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .widthIn(min = 900.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Food Offer & Donor", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(240.dp))
                        Text("Category", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(130.dp))
                        Text("Dietary", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(110.dp))
                        Text("Available Surplus", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(140.dp))
                        Text("Pickup Deadline", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(130.dp))
                        Text("Status", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(130.dp))
                        Text("Action", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(120.dp))
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    if (pagedItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(min = 900.dp)
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No food listings match current filter criteria.", color = MaterialTheme.colorScheme.outline, fontSize = 13.sp)
                        }
                    } else {
                        pagedItems.forEachIndexed { index, listing ->
                            Row(
                                modifier = Modifier
                                    .background(if (index % 2 == 0) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
                                    .clickable { onListingClicked(listing) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .widthIn(min = 900.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Title & Donor
                                Column(modifier = Modifier.width(240.dp)) {
                                    Text(
                                        text = listing.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = listing.donorOrg,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Category
                                Text(text = listing.category.label, fontSize = 12.sp, modifier = Modifier.width(130.dp))

                                // Dietary
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.width(110.dp)
                                ) {
                                    Text(
                                        text = listing.dietaryType.label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E6F50),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }

                                // Available / Total
                                Column(modifier = Modifier.width(140.dp)) {
                                    Text(
                                        text = "${listing.remainingQuantity} / ${listing.quantity} ${listing.unit}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = ForestGreenPrimary
                                    )
                                    Text(
                                        text = "~${listing.estimatedServings} servings",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                // Pickup Deadline
                                Column(modifier = Modifier.width(130.dp)) {
                                    Text(
                                        text = listing.pickupDeadline,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CoralAccent
                                    )
                                    Text(
                                        text = listing.location.neighborhood,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                // Status
                                Box(modifier = Modifier.width(130.dp)) {
                                    StatusBadge(status = listing.status)
                                }

                                // Action Button
                                Box(modifier = Modifier.width(120.dp)) {
                                    if (onClaimClicked != null && listing.isAvailable) {
                                        Button(
                                            onClick = { onClaimClicked(listing) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text("Claim", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = { onListingClicked(listing) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text("Details", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pagination Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${if (filteredListings.isEmpty()) 0 else displayedPage * pageSize + 1}–${(displayedPage * pageSize + pagedItems.size).coerceAtMost(filteredListings.size)} of ${filteredListings.size} listings",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (displayedPage > 0) currentPage-- },
                        enabled = displayedPage > 0,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Previous", fontSize = 11.sp)
                    }

                    Text(
                        text = "Page ${if (totalPages == 0) 0 else displayedPage + 1} of ${totalPages.coerceAtLeast(1)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedButton(
                        onClick = { if (displayedPage < totalPages - 1) currentPage++ },
                        enabled = displayedPage < totalPages - 1,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Next", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
