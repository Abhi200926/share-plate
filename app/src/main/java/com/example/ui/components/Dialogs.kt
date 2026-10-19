package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun ClaimDialog(
    listing: FoodListing,
    onDismiss: () -> Unit,
    onConfirmClaim: (quantity: Double) -> Unit,
    errorMessage: String? = null
) {
    var requestedQty by remember { mutableStateOf(listing.remainingQuantity.coerceAtMost(10.0)) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .padding(16.dp)
                .testTag("claim_dialog"),
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
                    Text(
                        text = "Claim Surplus Food",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = listing.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Offered by ${listing.donorOrg}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity selector
                Text(
                    text = "Available to Reserve: ${listing.remainingQuantity} ${listing.unit}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = requestedQty.toFloat(),
                    onValueChange = { requestedQty = (it * 2).roundToInt() / 2.0 },
                    valueRange = 1.0f..listing.remainingQuantity.toFloat(),
                    steps = ((listing.remainingQuantity - 1.0) * 2).toInt().coerceAtLeast(0),
                    modifier = Modifier.testTag("claim_qty_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Request: $requestedQty ${listing.unit}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "~${(requestedQty * 2.5).toInt()} servings",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFB91C1C),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logistics note
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Pickup Window: ${listing.pickupWindow}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Storage: ${listing.storageCondition}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "Location: ${listing.location.address}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onConfirmClaim(requestedQty) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_claim_submit_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Reservation", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun QrHandoverDialog(
    assignment: RescueAssignment,
    tokenType: TokenType,
    expectedToken: String,
    onDismiss: () -> Unit,
    onVerifyToken: (tokenString: String) -> Unit,
    verificationResult: String? = null,
    isError: Boolean = false
) {
    var manualTokenInput by remember { mutableStateOf(expectedToken) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .padding(16.dp)
                .testTag("qr_handover_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (tokenType == TokenType.PICKUP) "Pickup QR Handover" else "Delivery QR Handover",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Simulated QR Box
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "QR Code",
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(96.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (tokenType == TokenType.PICKUP) "DISPATCH PASS" else "DELIVERY VERIFY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreenPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Encrypted Token ID",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                Text(
                    text = expectedToken,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Context detail
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Food: ${assignment.listingTitle}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (tokenType == TokenType.PICKUP) "Donor: ${assignment.donorOrg}" else "Recipient: ${assignment.recipientOrg}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = if (tokenType == TokenType.PICKUP) "At: ${assignment.pickupAddress}" else "At: ${assignment.dropoffAddress}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                if (verificationResult != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = if (isError) Color(0xFFFEE2E2) else Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = verificationResult,
                            color = if (isError) Color(0xFFB91C1C) else Color(0xFF166534),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onVerifyToken(manualTokenInput) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scan_verify_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (tokenType == TokenType.PICKUP) "Confirm Pickup Handover" else "Confirm Delivery Receipt",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CreateListingDialog(
    onDismiss: () -> Unit,
    onSaveOfflineDraft: ((OfflineListingDraft) -> Unit)? = null,
    onSubmit: (
        title: String,
        description: String,
        category: FoodCategory,
        dietary: DietaryType,
        quantity: Double,
        unit: String,
        servings: Int,
        prepTime: String,
        useBy: String,
        pickupDeadline: String,
        storageCondition: String,
        tempReading: String,
        allergens: List<String>,
        packaging: String,
        pickupWindow: String,
        address: String,
        safetyAttested: Boolean
    ) -> Unit
) {
    var selectedLanguage by remember { mutableStateOf("EN") } // EN, ES, FR, HI
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FoodCategory.COOKED_MEALS) }
    var selectedDietary by remember { mutableStateOf(DietaryType.VEGETARIAN) }
    var quantityText by remember { mutableStateOf("15.0") }
    var unit by remember { mutableStateOf("kg") }
    var servingsText by remember { mutableStateOf("35") }
    var prepTime by remember { mutableStateOf("Today, 14:00") }
    var useBy by remember { mutableStateOf("Tonight, 22:00") }
    var pickupDeadline by remember { mutableStateOf("Tonight, 20:00") }
    var storageCondition by remember { mutableStateOf("Refrigerated (2°C - 4°C)") }
    var tempReading by remember { mutableStateOf("3.5°C") }
    var allergensText by remember { mutableStateOf("Gluten, Dairy") }
    var packaging by remember { mutableStateOf("Insulated thermal food crates") }
    var pickupWindow by remember { mutableStateOf("18:30 - 20:00 Tonight") }
    var address by remember { mutableStateOf("742 Evergreen Blvd, Midtown") }
    var safetyAttested by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var aiBannerNotice by remember { mutableStateOf<String?>(null) }
    var isVoiceListening by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)
                .padding(12.dp)
                .testTag("create_listing_dialog"),
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
                    Column {
                        Text(
                            text = when (selectedLanguage) {
                                "ES" -> "Donar Alimentos Excedentes"
                                "FR" -> "Donner des Aliments Excédentaires"
                                "HI" -> "अधिशेष भोजन दान करें"
                                else -> "Donate Surplus Food"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Safe Food Redistribution Form",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    // Multilingual Selector Chips
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("EN", "ES", "FR", "HI").forEach { lang ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (selectedLanguage == lang) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { selectedLanguage = lang }
                            ) {
                                Text(
                                    text = lang,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedLanguage == lang) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // AI Assist Action Toolbar: Photo Assist & Voice Dictation
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ForestGreenLight.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Smart Listing Assistants", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ForestGreenPrimary)
                            Text("Editable suggestions", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Photo Assist Action
                            Button(
                                onClick = {
                                    // Simulates photo analysis of hot buffet trays
                                    title = "Herb Roasted Chicken & Steamed Vegetables"
                                    description = "Chef prepared roasted chicken breast trays with thyme carrots and roast potatoes. Cooled and held in stainless steam inserts."
                                    selectedCategory = FoodCategory.COOKED_MEALS
                                    selectedDietary = DietaryType.NON_VEGETARIAN
                                    quantityText = "22.0"
                                    unit = "kg"
                                    servingsText = "48"
                                    storageCondition = "Hot holding (65°C) or rapid chilled (3.2°C)"
                                    tempReading = "3.4°C"
                                    allergensText = "Celery"
                                    packaging = "Sealed aluminum hotel pans"
                                    aiBannerNotice = "AI photo analysis populated suggestions. Review and modify before attesting safety."
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI Photo Scan", fontSize = 11.sp)
                            }

                            // Voice Dictation Action
                            Button(
                                onClick = {
                                    isVoiceListening = !isVoiceListening
                                    if (isVoiceListening) {
                                        title = "Artisan Sourdough Loaves & Olive Ciabatta"
                                        description = "Baked freshly this morning at 06:00. Naturally leavened sourdough and herb ciabatta rolls."
                                        selectedCategory = FoodCategory.BAKED_GOODS
                                        selectedDietary = DietaryType.VEGAN
                                        quantityText = "16.0"
                                        unit = "kg"
                                        servingsText = "35"
                                        storageCondition = "Ambient dry"
                                        tempReading = "21.0°C"
                                        allergensText = "Gluten"
                                        packaging = "Kraft paper bakery cartons"
                                        aiBannerNotice = "Voice dictation transcribed. All fields ready for edit."
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = if (isVoiceListening) CoralAccent else EmeraldSecondary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isVoiceListening) "Transcribed!" else "Voice Dictate", fontSize = 11.sp)
                            }
                        }

                        if (aiBannerNotice != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = aiBannerNotice!!,
                                    fontSize = 10.sp,
                                    color = Color(0xFF92400E),
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Food Title (e.g. Herb Roasted Chicken Trays)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_listing_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Detailed Food Description & Handling") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_listing_desc"),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantity") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_listing_qty")
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = servingsText,
                        onValueChange = { servingsText = it },
                        label = { Text("Est. Servings") },
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Dietary Classification:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(DietaryType.VEGETARIAN, DietaryType.VEGAN, DietaryType.NON_VEGETARIAN).forEach { d ->
                        FilterChip(
                            selected = selectedDietary == d,
                            onClick = { selectedDietary = d },
                            label = { Text(d.label, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = storageCondition,
                    onValueChange = { storageCondition = it },
                    label = { Text("Storage Requirement (e.g. Refrigerated 2-4°C)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tempReading,
                    onValueChange = { tempReading = it },
                    label = { Text("Temperature Reading Logged at Pack-down") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = allergensText,
                    onValueChange = { allergensText = it },
                    label = { Text("Declared Allergens (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pickupDeadline,
                    onValueChange = { pickupDeadline = it },
                    label = { Text("Pickup Deadline (e.g. Tonight, 20:30)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Pickup Location / Dispatch Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Safety Attestation Checkbox (Mandatory - Never let AI certify food safety)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFEF3C7), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = safetyAttested,
                        onCheckedChange = { safetyAttested = it },
                        modifier = Modifier.testTag("safety_attest_checkbox")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (selectedLanguage) {
                            "ES" -> "Doy fe solemnemente de que este alimento excedente se preparó bajo normas de higiene certificadas y se mantuvo en umbrales de temperatura seguros. (La IA nunca puede certificar la seguridad alimentaria)."
                            "FR" -> "J'atteste solennellement que cette nourriture a été préparée selon les normes d'hygiène et maintenue à des températures sûres. (L'IA ne peut jamais certifier la sécurité alimentaire)."
                            "HI" -> "मैं प्रमाणित करता हूँ कि यह भोजन स्वच्छता मानकों के तहत तैयार किया गया था और सुरक्षित तापमान पर रखा गया है। (एआई खाद्य सुरक्षा को प्रमाणित नहीं कर सकता)।"
                            else -> "I solemnly attest that this surplus food was prepared under certified hygiene regulations and held continuously within safe temperature thresholds. (Never let AI certify food safety)."
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF92400E)
                    )
                }

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMsg!!, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Offline Draft Option
                    if (onSaveOfflineDraft != null) {
                        OutlinedButton(
                            onClick = {
                                val qty = quantityText.toDoubleOrNull() ?: 1.0
                                val srv = servingsText.toIntOrNull() ?: 1
                                val allergenList = allergensText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                val draft = OfflineListingDraft(
                                    id = "draft-" + System.currentTimeMillis().toString().takeLast(6),
                                    title = if (title.isBlank()) "Draft Surplus Batch" else title,
                                    description = description,
                                    category = selectedCategory,
                                    dietaryType = selectedDietary,
                                    quantity = qty,
                                    unit = unit,
                                    estimatedServings = srv,
                                    prepTime = prepTime,
                                    useByDeadline = useBy,
                                    pickupDeadline = pickupDeadline,
                                    storageCondition = storageCondition,
                                    temperatureReading = tempReading,
                                    allergens = allergenList,
                                    packagingRequirement = packaging,
                                    pickupWindow = pickupWindow,
                                    address = address,
                                    safetyAttested = safetyAttested
                                )
                                onSaveOfflineDraft(draft)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.SaveAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Offline Draft", fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = {
                            val qty = quantityText.toDoubleOrNull() ?: 0.0
                            val srv = servingsText.toIntOrNull() ?: 1
                            if (title.isBlank()) {
                                errorMsg = "Please provide a title."
                                return@Button
                            }
                            if (qty <= 0.0) {
                                errorMsg = "Quantity must be greater than zero."
                                return@Button
                            }
                            if (!safetyAttested) {
                                errorMsg = "You must attest to the food safety declaration."
                                return@Button
                            }
                            errorMsg = null
                            val allergenList = allergensText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            onSubmit(
                                title, description, selectedCategory, selectedDietary,
                                qty, unit, srv, prepTime, useBy, pickupDeadline,
                                storageCondition, tempReading, allergenList,
                                packaging, pickupWindow, address, safetyAttested
                            )
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("submit_create_listing_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publish Surplus Offer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

