package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.adaptive.calculateWindowSizeClass
import com.example.ui.adaptive.WindowSizeClassType
import com.example.ui.adaptive.LocalWindowSizeClass
import com.example.ui.theme.*

@Composable
fun VolunteerScreen(
    volunteerUser: UserProfile,
    assignments: List<RescueAssignment>,
    onAcceptAssignment: (String) -> Unit,
    onOpenQrScan: (assignment: RescueAssignment, type: TokenType) -> Unit,
    onCancelAssignment: (assignmentId: String, reason: String) -> Unit = { _, _ -> },
    onLogColdChainTemp: (assignmentId: String, temp: Double, notes: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val myAssignments = assignments.filter { it.volunteerId == volunteerUser.id }
    val activeAssignment = myAssignments.find { it.status != AssignmentStatus.DELIVERED && it.status != AssignmentStatus.CANCELLED }
    val completedAssignments = myAssignments.filter { it.status == AssignmentStatus.DELIVERED }


    val localSize = LocalWindowSizeClass.current
    BoxWithConstraints(modifier = modifier.fillMaxSize().testTag("volunteer_dashboard")) {
        val sizeClass = if (localSize != WindowSizeClassType.COMPACT) localSize else calculateWindowSizeClass(maxWidth)
        val isWide = sizeClass != WindowSizeClassType.COMPACT

        if (isWide) {
            // Desktop & Tablet 2-Column Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Left Column: Active Rescue Mission Card
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Current Transport Assignment",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (activeAssignment == null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(36.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircleOutline, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(52.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("All Caught Up!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("No pending runs right now. Real-time rescue dispatches will appear here.", fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    } else {
                        ActiveMissionCard(
                            assignment = activeAssignment,
                            onAccept = onAcceptAssignment,
                            onOpenQr = onOpenQrScan,
                            onCancel = onCancelAssignment,
                            onLogTemp = onLogColdChainTemp
                        )
                    }
                }

                // Right Column: Courier Profile & Completed Trips History
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile & Stats Card
                    CourierProfileCard(volunteerUser = volunteerUser)

                    Text(
                        text = "Completed Trips (${completedAssignments.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (completedAssignments.isEmpty()) {
                            item {
                                Text("No completed rescue trips recorded yet.", fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        } else {
                            items(completedAssignments) { asgn ->
                                CompletedMissionCard(asgn = asgn)
                            }
                        }
                    }
                }
            }
        } else {
            // Mobile Single-Column Scroll
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    CourierProfileCard(volunteerUser = volunteerUser)
                }

                item {
                    Text(
                        text = "Current Rescue Mission",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (activeAssignment == null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircleOutline, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(44.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("All Caught Up!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("No active delivery runs pending right now.", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    } else {
                        ActiveMissionCard(
                            assignment = activeAssignment,
                            onAccept = onAcceptAssignment,
                            onOpenQr = onOpenQrScan,
                            onCancel = onCancelAssignment,
                            onLogTemp = onLogColdChainTemp
                        )
                    }
                }

                item {
                    Text(
                        text = "Completed Rescue History",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (completedAssignments.isEmpty()) {
                    item {
                        Text("No completed rescues yet.", fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    items(completedAssignments) { asgn ->
                        CompletedMissionCard(asgn = asgn)
                    }
                }
            }
        }
    }
}

@Composable
private fun CourierProfileCard(volunteerUser: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = volunteerUser.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${volunteerUser.organisationName} • Verified Courier", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }
                Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(8.dp)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.ElectricBike, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Green Courier", fontSize = 11.sp, color = Color(0xFF92400E), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Deliveries", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        Text("${volunteerUser.completedRescues}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Reliability", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        Text("${volunteerUser.rating} ★", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CoralAccent)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveMissionCard(
    assignment: RescueAssignment,
    onAccept: (String) -> Unit,
    onOpenQr: (RescueAssignment, TokenType) -> Unit,
    onCancel: (assignmentId: String, reason: String) -> Unit,
    onLogTemp: (assignmentId: String, temp: Double, notes: String) -> Unit
) {
    var tempInput by remember { mutableStateOf("3.8") }
    var tempLogFeedback by remember { mutableStateOf<String?>(null) }
    var isTempBreach by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = assignment.listingTitle, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Surface(color = Color(0xFFDBEAFE), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = assignment.status.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4-step Visual Stepper
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val steps = listOf("Assigned", "Accepted", "Picked Up", "Delivered")
                steps.forEachIndexed { index, stepName ->
                    val isDone = assignment.status.stepIndex >= index
                    val isCurrent = assignment.status.stepIndex == index

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(if (isDone) ForestGreenPrimary else Color(0xFFE5E7EB))
                                .border(if (isCurrent) 2.dp else 0.dp, if (isCurrent) CoralAccent else Color.Transparent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            } else {
                                Text("${index + 1}", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stepName,
                            fontSize = 10.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) ForestGreenPrimary else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Logistics Details
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pick Up: ${assignment.donorOrg}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Text("At: ${assignment.pickupAddress}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(start = 24.dp))

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VolunteerActivism, contentDescription = null, tint = CoralAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Deliver To: ${assignment.recipientOrg}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Text("At: ${assignment.dropoffAddress}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(start = 24.dp))

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Package: ${assignment.quantitySummary}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary, modifier = Modifier.padding(start = 24.dp))
                }
            }

            // In-Transit Cold Chain Temperature Logger
            if (assignment.status == AssignmentStatus.PICKED_UP || assignment.status == AssignmentStatus.ACCEPTED) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Thermostat, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cold Chain Transit Log", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = tempInput,
                                onValueChange = { tempInput = it },
                                label = { Text("Temp (°C)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    val t = tempInput.toDoubleOrNull() ?: 4.0
                                    onLogTemp(assignment.id, t, "Courier verified reading in thermal bag.")
                                    if (t > 5.0) {
                                        isTempBreach = true
                                        tempLogFeedback = "ALERT: ${t}°C exceeds safe 5.0°C limit! Safety report auto-escalated."
                                    } else {
                                        isTempBreach = false
                                        tempLogFeedback = "Compliant: ${t}°C recorded in cold chain ledger."
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                Text("Log Temp", fontSize = 11.sp)
                            }
                        }

                        val currentFeedback = tempLogFeedback
                        if (currentFeedback != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentFeedback,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTempBreach) Color(0xFFDC2626) else ForestGreenPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (assignment.status) {
                AssignmentStatus.ASSIGNED -> {
                    Button(
                        onClick = { onAccept(assignment.id) },
                        modifier = Modifier.fillMaxWidth().testTag("volunteer_accept_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ThumbUp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Accept Transport Run", fontWeight = FontWeight.Bold)
                    }
                }
                AssignmentStatus.ACCEPTED -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel Run", color = Color(0xFFDC2626), fontSize = 12.sp)
                        }
                        Button(
                            onClick = { onOpenQr(assignment, TokenType.PICKUP) },
                            modifier = Modifier.weight(2f).testTag("volunteer_pickup_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verify Pickup QR", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                AssignmentStatus.PICKED_UP -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Escalate Issue", color = Color(0xFFDC2626), fontSize = 12.sp)
                        }
                        Button(
                            onClick = { onOpenQr(assignment, TokenType.DELIVERY) },
                            modifier = Modifier.weight(2f).testTag("volunteer_deliver_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CoralAccent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verify Delivery QR", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel / Escalate Rescue Run", fontWeight = FontWeight.Bold) },
            text = {
                Text("Select cancellation reason. The listing will be returned to the dispatch queue immediately.", fontSize = 13.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancel(assignment.id, "Courier vehicle mechanical issue / traffic delay")
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Confirm Cancellation")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Go Back")
                }
            }
        )
    }
}


@Composable
private fun CompletedMissionCard(asgn: RescueAssignment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = asgn.listingTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "From ${asgn.donorOrg} to ${asgn.recipientOrg}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                Text(text = asgn.quantitySummary, fontSize = 11.sp, color = ForestGreenPrimary, fontWeight = FontWeight.SemiBold)
            }
            Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(24.dp))
        }
    }
}
