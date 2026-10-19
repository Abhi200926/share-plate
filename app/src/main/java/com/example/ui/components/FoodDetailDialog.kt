package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.FoodListing
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun FoodDetailDialog(
    listing: FoodListing,
    onDismiss: () -> Unit,
    onClaimClicked: () -> Unit,
    onReportSafetyClicked: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .padding(12.dp)
                .testTag("food_detail_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(status = listing.status)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = listing.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Prepared by ${listing.donorOrg}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = listing.description,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Logistics Grid
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Surplus: ${listing.remainingQuantity} / ${listing.quantity} ${listing.unit} (~${listing.estimatedServings} servings)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = ForestGreenPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Dietary: ${listing.dietaryType.label}", fontSize = 12.sp)
                        Text(text = "Preparation Logged: ${listing.preparationTime}", fontSize = 12.sp)
                        Text(text = "Use-By Expiry: ${listing.useByDeadline}", fontSize = 12.sp, color = CoralAccent, fontWeight = FontWeight.SemiBold)
                        Text(text = "Pickup Deadline: ${listing.pickupDeadline}", fontSize = 12.sp, color = CoralAccent, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Food Safety & Temperature Box
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Thermostat, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cold Chain & Temperature", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF92400E))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Storage: ${listing.storageCondition}", fontSize = 11.sp, color = Color(0xFF78350F))
                        if (listing.temperatureReading != null) {
                            Text(text = "Reading: ${listing.temperatureReading}", fontSize = 11.sp, color = Color(0xFF78350F), fontWeight = FontWeight.SemiBold)
                        }
                        Text(text = "Packaging: ${listing.packagingRequirement}", fontSize = 11.sp, color = Color(0xFF78350F))
                    }
                }

                // Allergens
                if (listing.allergens.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Declared Allergens: ${listing.allergens.joinToString(", ")}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = "Cross-Contact Warning: ${listing.crossContactWarning}",
                                fontSize = 10.sp,
                                color = Color(0xFF7F1D1D)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                if (listing.isAvailable) {
                    Button(
                        onClick = {
                            onDismiss()
                            onClaimClicked()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("detail_claim_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.VolunteerActivism, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Claim This Surplus Food", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onReportSafetyClicked()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.ReportProblem, contentDescription = null, tint = CoralAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Report Food Safety or Packaging Concern", fontSize = 11.sp, color = CoralAccent)
                }
            }
        }
    }
}
