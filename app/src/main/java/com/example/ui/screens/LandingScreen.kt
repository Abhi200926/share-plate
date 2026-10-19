package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FoodListing
import com.example.model.ImpactMetrics
import com.example.model.UserRole
import com.example.ui.adaptive.calculateWindowSizeClass
import com.example.ui.adaptive.WindowSizeClassType
import com.example.ui.adaptive.LocalWindowSizeClass
import com.example.ui.components.FoodListingCard
import com.example.ui.components.SafetyEmergencyNoticeCard
import com.example.ui.components.StatCard
import com.example.ui.theme.*

@Composable
fun LandingScreen(
    impactMetrics: ImpactMetrics,
    recentListings: List<FoodListing>,
    onRoleSelected: (UserRole) -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToAiMatcher: () -> Unit,
    onListingClicked: (FoodListing) -> Unit,
    modifier: Modifier = Modifier
) {
    val localSize = LocalWindowSizeClass.current
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val sizeClass = if (localSize != WindowSizeClassType.COMPACT) localSize else calculateWindowSizeClass(maxWidth)
        val isWide = sizeClass != WindowSizeClassType.COMPACT

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("landing_screen"),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Hero Section (Full width banner on desktop, responsive layout)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(ForestGreenPrimary, ForestGreenLight)
                            )
                        )
                        .padding(if (isWide) 32.dp else 20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (sizeClass == WindowSizeClassType.EXPANDED) Alignment.Start else Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = MintLight.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Eco, contentDescription = null, tint = MintLight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Good Food. Shared Futures.", color = MintLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "SharePlate Food Rescue Network",
                            fontSize = if (isWide) 34.sp else 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = if (isWide) 40.sp else 34.sp,
                            textAlign = if (sizeClass == WindowSizeClassType.EXPANDED) TextAlign.Start else TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Connecting restaurants, bakeries, university canteens, community shelters, and volunteer couriers to redirect surplus food into nutritious meals with verified food safety.",
                            fontSize = 15.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 22.sp,
                            textAlign = if (sizeClass == WindowSizeClassType.EXPANDED) TextAlign.Start else TextAlign.Center,
                            modifier = Modifier.widthIn(max = 900.dp)
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = if (sizeClass == WindowSizeClassType.EXPANDED) Modifier else Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onRoleSelected(UserRole.DONOR) },
                                colors = ButtonDefaults.buttonColors(containerColor = CoralAccent),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
                                modifier = if (sizeClass == WindowSizeClassType.COMPACT) Modifier.weight(1f).testTag("cta_donate_food") else Modifier.testTag("cta_donate_food")
                            ) {
                                Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Donate Surplus", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Button(
                                onClick = { onRoleSelected(UserRole.RECIPIENT) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
                                modifier = if (sizeClass == WindowSizeClassType.COMPACT) Modifier.weight(1f).testTag("cta_find_food") else Modifier.testTag("cta_find_food")
                            ) {
                                Icon(imageVector = Icons.Default.VolunteerActivism, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Find Food", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                            }

                            OutlinedButton(
                                onClick = { onRoleSelected(UserRole.VOLUNTEER) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
                                modifier = if (sizeClass == WindowSizeClassType.COMPACT) Modifier.weight(1f).testTag("cta_volunteer") else Modifier.testTag("cta_volunteer")
                            ) {
                                Text("Courier Run", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // Impact Statistics: 4 in a horizontal row on Desktop, 2x2 on Tablet, 2x2 on Mobile
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isWide) 24.dp else 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Live Network Impact",
                        fontSize = if (isWide) 22.sp else 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time verified rescues across participating commercial kitchens and recipient shelters",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (sizeClass == WindowSizeClassType.EXPANDED) {
                        // DESKTOP: Exactly 4 impact statistics in one horizontal row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCard(
                                title = "Rescued Food",
                                value = "${impactMetrics.totalFoodWeightRescuedKg.toInt()} kg",
                                subtitle = "Verified edible surplus",
                                icon = Icons.Default.Scale,
                                iconTint = ForestGreenLight,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Meals Served",
                                value = "${impactMetrics.estimatedServingsDelivered}",
                                subtitle = "Nutritious portions",
                                icon = Icons.Default.Restaurant,
                                iconTint = CoralAccent,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Completed Trips",
                                value = "${impactMetrics.completedDeliveries}",
                                subtitle = "QR verified deliveries",
                                icon = Icons.Default.CheckCircle,
                                iconTint = Color(0xFF2563EB),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "CO₂e Avoided",
                                value = "${impactMetrics.co2EquivalentPreventedKg.toInt()} kg",
                                subtitle = "Emissions prevented",
                                icon = Icons.Default.CloudQueue,
                                iconTint = EmeraldSecondary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        // TABLET & MOBILE: 2x2 grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatCard(
                                title = "Rescued Food",
                                value = "${impactMetrics.totalFoodWeightRescuedKg.toInt()} kg",
                                subtitle = "Verified edible surplus",
                                icon = Icons.Default.Scale,
                                iconTint = ForestGreenLight,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Meals Served",
                                value = "${impactMetrics.estimatedServingsDelivered}",
                                subtitle = "Nutritious portions",
                                icon = Icons.Default.Restaurant,
                                iconTint = CoralAccent,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatCard(
                                title = "Completed Trips",
                                value = "${impactMetrics.completedDeliveries}",
                                subtitle = "QR verified deliveries",
                                icon = Icons.Default.CheckCircle,
                                iconTint = Color(0xFF2563EB),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "CO₂e Avoided",
                                value = "${impactMetrics.co2EquivalentPreventedKg.toInt()} kg",
                                subtitle = "Emissions prevented",
                                icon = Icons.Default.CloudQueue,
                                iconTint = EmeraldSecondary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Safety Notice Card
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    SafetyEmergencyNoticeCard()
                }
            }

            // How SharePlate Works (Responsive: 4 columns on wide screens, 2x2 on medium, 1 col on mobile)
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("How SharePlate Works", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Zero waste with certified enterprise food hygiene standards", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)

                    Spacer(modifier = Modifier.height(14.dp))

                    val steps = listOf(
                        Triple("1. Kitchens Log Surplus", "Chefs record prep time, temperature, allergen list, and packing specs.", Icons.Default.AddBox),
                        Triple("2. AI Rescue Matcher", "Matches urgency, capacity, and dietary needs with nearby verified NGOs.", Icons.Default.AutoAwesome),
                        Triple("3. Contactless QR Run", "Volunteers verify pickup and delivery with tamper-proof single-use tokens.", Icons.Default.QrCodeScanner),
                        Triple("4. Audited Impact Ledger", "Verified deliveries update real-time meal counts and carbon savings.", Icons.Default.Insights)
                    )

                    if (isWide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            steps.forEach { (title, desc, icon) ->
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(14.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(ForestGreenContainerLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(imageVector = icon, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(22.dp))
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline, lineHeight = 16.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            steps.forEach { (title, desc, icon) ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(ForestGreenContainerLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(imageVector = icon, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(22.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Active Rescue Opportunities Ticker
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active Rescue Feed", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        TextButton(onClick = onNavigateToExplore) {
                            Text("View All Available", color = ForestGreenLight, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val displayListings = recentListings.take(6)
                    when (sizeClass) {
                        WindowSizeClassType.EXPANDED -> {
                            // DESKTOP: 3 Columns Grid
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                displayListings.chunked(3).forEach { rowListings ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        rowListings.forEach { listing ->
                                            Box(modifier = Modifier.weight(1f)) {
                                                FoodListingCard(
                                                    listing = listing,
                                                    onCardClick = { onListingClicked(listing) },
                                                    onClaimClick = { onListingClicked(listing) }
                                                )
                                            }
                                        }
                                        for (i in 0 until (3 - rowListings.size)) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                        WindowSizeClassType.MEDIUM -> {
                            // TABLET: 2 Columns Grid
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                displayListings.chunked(2).forEach { rowListings ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        rowListings.forEach { listing ->
                                            Box(modifier = Modifier.weight(1f)) {
                                                FoodListingCard(
                                                    listing = listing,
                                                    onCardClick = { onListingClicked(listing) },
                                                    onClaimClick = { onListingClicked(listing) }
                                                )
                                            }
                                        }
                                        if (rowListings.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                        WindowSizeClassType.COMPACT -> {
                            // MOBILE: Vertically stacked single column
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                displayListings.forEach { listing ->
                                    FoodListingCard(
                                        listing = listing,
                                        onCardClick = { onListingClicked(listing) },
                                        onClaimClick = { onListingClicked(listing) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
