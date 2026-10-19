package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.FoodListing
import com.example.model.IncidentType

@Composable
fun IncidentReportDialog(
    listing: FoodListing,
    onDismiss: () -> Unit,
    onSubmitIncident: (type: IncidentType, description: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(IncidentType.PACKAGING_DAMAGE) }
    var description by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .padding(16.dp)
                .testTag("incident_report_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Report Safety Incident",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Subject: ${listing.title}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Submitting a report will automatically place this listing on administrative hold.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                Spacer(modifier = Modifier.height(12.dp))

                Text("Incident Classification:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                IncidentType.values().take(4).forEach { type ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == type,
                            onClick = { selectedType = type }
                        )
                        Text(text = type.label, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Describe observed condition") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorText!!, color = Color.Red, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (description.isBlank()) {
                            errorText = "Please describe the incident."
                            return@Button
                        }
                        onSubmitIncident(selectedType, description)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_incident_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Submit Safety Escalation", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
