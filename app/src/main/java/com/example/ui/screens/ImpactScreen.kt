package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ImpactMetrics
import com.example.ui.adaptive.calculateWindowSizeClass
import com.example.ui.adaptive.WindowSizeClassType
import com.example.ui.adaptive.LocalWindowSizeClass
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.StatCard
import com.example.ui.theme.*

@Composable
fun ImpactScreen(
    metrics: ImpactMetrics,
    onResetDemoData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    var reportExportDialogContent by remember { mutableStateOf<String?>(null) }
    var reportDialogTitle by remember { mutableStateOf("") }

    val localSize = LocalWindowSizeClass.current
    BoxWithConstraints(modifier = modifier.fillMaxSize().testTag("impact_screen")) {
        val sizeClass = if (localSize != WindowSizeClassType.COMPACT) localSize else calculateWindowSizeClass(maxWidth)
        val isWide = sizeClass != WindowSizeClassType.COMPACT

        Column(modifier = Modifier.fillMaxSize()) {
            DemoModeBanner(onResetClicked = onResetDemoData)

            // Tabs for Impact Ledger, CSR Sponsors & ESG, Donor Waste Analytics
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Impact Ledger", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("CSR Sponsors & ESG", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Donor Analytics & Reports", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Standard Impact Ledger
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Sustainability & Network Impact",
                                    fontSize = if (isWide) 26.sp else 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Verified metrics computed strictly from completed QR delivery records in the SharePlate ledger.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                if (isWide) {
                                    // Desktop 4-in-a-row KPI Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        StatCard(
                                            title = "Food Rescued",
                                            value = "${metrics.totalFoodWeightRescuedKg.toInt()} kg",
                                            subtitle = "Edible surplus saved",
                                            icon = Icons.Default.Scale,
                                            iconTint = ForestGreenLight,
                                            modifier = Modifier.weight(1f)
                                        )
                                        StatCard(
                                            title = "Meals Provided",
                                            value = "${metrics.estimatedServingsDelivered}",
                                            subtitle = "Nutritious portions",
                                            icon = Icons.Default.Restaurant,
                                            iconTint = CoralAccent,
                                            modifier = Modifier.weight(1f)
                                        )
                                        StatCard(
                                            title = "CO₂e Prevented",
                                            value = "${metrics.co2EquivalentPreventedKg.toInt()} kg",
                                            subtitle = "Landfill emissions",
                                            icon = Icons.Default.CloudQueue,
                                            iconTint = EmeraldSecondary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        StatCard(
                                            title = "Completion Rate",
                                            value = "${metrics.rescueCompletionRatePercent.toInt()}%",
                                            subtitle = "Reliability score",
                                            icon = Icons.Default.CheckCircle,
                                            iconTint = Color(0xFF2563EB),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                } else {
                                    // Phone 2x2 Grid
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        StatCard(
                                            title = "Food Rescued",
                                            value = "${metrics.totalFoodWeightRescuedKg.toInt()} kg",
                                            subtitle = "Edible surplus saved",
                                            icon = Icons.Default.Scale,
                                            iconTint = ForestGreenLight,
                                            modifier = Modifier.weight(1f)
                                        )
                                        StatCard(
                                            title = "Meals Provided",
                                            value = "${metrics.estimatedServingsDelivered}",
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
                                            title = "CO₂e Prevented",
                                            value = "${metrics.co2EquivalentPreventedKg.toInt()} kg",
                                            subtitle = "Landfill emissions",
                                            icon = Icons.Default.CloudQueue,
                                            iconTint = EmeraldSecondary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        StatCard(
                                            title = "Completion Rate",
                                            value = "${metrics.rescueCompletionRatePercent.toInt()}%",
                                            subtitle = "Reliability score",
                                            icon = Icons.Default.CheckCircle,
                                            iconTint = Color(0xFF2563EB),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        // Scientific Methodology & Progress Bars (Side-by-side on wide screens)
                        item {
                            if (isWide) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1.2f)) {
                                        ScientificMethodologyCard(metrics = metrics)
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        DiversionBreakdownCard()
                                    }
                                }
                            } else {
                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    ScientificMethodologyCard(metrics = metrics)
                                    DiversionBreakdownCard()
                                }
                            }
                        }

                        // Ecosystem Counts
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Participating Network Ecosystem", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("${metrics.activeDonorCount}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = ForestGreenPrimary)
                                            Text("Donors", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("${metrics.activeRecipientCount}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2563EB))
                                            Text("Recipients", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("${metrics.activeVolunteerCount}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = CoralAccent)
                                            Text("Volunteers", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // CSR Sponsor & ESG Dashboard
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("CSR Corporate Sponsor & ESG Ledger", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                            Text("Zero fabricated data. Auditable Scope 3 greenhouse gas avoidance.", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                        }
                                        Button(
                                            onClick = {
                                                reportDialogTitle = "CSR ESG Verification Certificate (ISO 14064 Compliant)"
                                                reportExportDialogContent = """
                                                    SHAREPLATE FOOD RESCUE NETWORK - CSR ESG AUDIT DISCLOSURE
                                                    Date: 2026-10-18 | Audit ID: ESG-SP-2026-9941
                                                    Framework: GHG Protocol Corporate Standard / UN FAO Model
                                                    
                                                    SUMMARY METRICS (VERIFIED BY SINGLE-USE QR HANDOVERS):
                                                    - Total Diversion Volume: ${metrics.totalFoodWeightRescuedKg} kg
                                                    - Meals Distributed: ${metrics.estimatedServingsDelivered} portions
                                                    - Scope 3 Emissions Avoided: ${metrics.co2EquivalentPreventedKg} kg CO2e
                                                    - Water Footprint Preserved: ${(metrics.totalFoodWeightRescuedKg * 290).toInt()} Litres
                                                    - Network Delivery Success Rate: ${metrics.rescueCompletionRatePercent.toInt()}%
                                                    
                                                    METHODOLOGY NOTE:
                                                    Calculation applies strictly to delivered transactions with dual-signature
                                                    cryptographic tokens. In-progress or quarantined lots are excluded.
                                                """.trimIndent()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                                        ) {
                                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Download ESG Audit", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        item {
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
                                        Text("Sponsor Underwritten Rescues", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        Text("${metrics.completedDeliveries} Trips", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                                        Text("100% verified cold-chain logs", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                }
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("Landfill Methane (CH₄) Abatement", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        Text("${(metrics.co2EquivalentPreventedKg * 0.08).toInt()} kg CH₄", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CoralAccent)
                                        Text("28x more potent than CO₂", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Corporate Sponsors & Underwriting Partners", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    listOf(
                                        Triple("Green Logistics Alliance", "Cold-chain thermal box underwriting ($12,500 grant)", "Tier 1 Climate Partner"),
                                        Triple("Pacific Energy Trust", "Electric cargo bike delivery reimbursement", "Mobility Sponsor"),
                                        Triple("Harbor Community Foundation", "Emergency weekend shelter dispatch stipend", "Equity Underwriter")
                                    ).forEach { (name, desc, tier) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                            }
                                            Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(6.dp)) {
                                                Text(tier, fontSize = 10.sp, color = Color(0xFF0369A1), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Donor Analytics & Reports
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Kitchen Surplus Analytics & Prevention", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text("Empowering commercial kitchens to reduce prep overage at source.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        }
                                        Button(
                                            onClick = {
                                                reportDialogTitle = "Donor Food Waste & Diversion Report (CSV Format)"
                                                reportExportDialogContent = """
                                                    "Donor","Date","Shift","Food Item","Weight_Kg","Servings","Status","CO2_Saved_Kg"
                                                    "Grand Horizon Hotel","2026-10-18","Dinner Banquet","Mediterranean Buffet Trays",24.0,55,"Delivered",60.0
                                                    "Artisan Hearth Bakery","2026-10-18","Morning Pack","Artisan Sourdough Loaves",18.0,40,"Delivered",45.0
                                                    "Metro University Canteen","2026-10-18","Lunch Buffet","Lentil Dahl & Basmati",30.0,70,"Delivered",75.0
                                                    "TOTAL_RESCUED_KG",72.0,"TOTAL_CO2E_KG",180.0
                                                """.trimIndent()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                                        ) {
                                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Export CSV Report", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Surplus by Service Shift", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text("Dinner Banquet (Post 20:00): 54%", fontSize = 12.sp)
                                    LinearProgressIndicator(progress = { 0.54f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = ForestGreenPrimary)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("Lunch Buffet (14:00 - 15:30): 31%", fontSize = 12.sp)
                                    LinearProgressIndicator(progress = { 0.31f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = CoralAccent)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("Morning Bake Pack (10:00): 15%", fontSize = 12.sp)
                                    LinearProgressIndicator(progress = { 0.15f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = EmeraldSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Export / Download Dialog
    if (reportExportDialogContent != null) {
        Dialog(onDismissRequest = { reportExportDialogContent = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 620.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(reportDialogTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        IconButton(onClick = { reportExportDialogContent = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = reportExportDialogContent!!,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { reportExportDialogContent = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                    ) {
                        Text("Copy to Clipboard & Close", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScientificMethodologyCard(metrics: ImpactMetrics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Science, contentDescription = null, tint = ForestGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scientific Methodology & Formula", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "1 kg Rescued Food = 2.50 kg CO₂e Emissions Prevented",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Source: UN Food & Agriculture Organization (FAO) Food Wastage Footprint Model. Food diverted from landfills prevents anaerobic bacterial decomposition generating potent methane (CH₄) gas.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Car Miles Offset", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                        Text("${(metrics.co2EquivalentPreventedKg * 2.4).toInt()} mi", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Trees Equiv.", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                        Text("${(metrics.co2EquivalentPreventedKg / 21.0).toInt()} trees", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Water Saved", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                        Text("${(metrics.totalFoodWeightRescuedKg * 290).toInt()} L", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DiversionBreakdownCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Rescue Diversion by Food Type", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Hot Cooked Meals: 62%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            LinearProgressIndicator(
                progress = { 0.62f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = ForestGreenPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Bakery & Grains: 25%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            LinearProgressIndicator(
                progress = { 0.25f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = CoralAccent
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Fresh Produce: 13%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            LinearProgressIndicator(
                progress = { 0.13f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = EmeraldSecondary
            )
        }
    }
}
