package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.horizontalScroll
import com.example.data.SharePlateRepository
import com.example.model.*
import com.example.ui.adaptive.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePlateApp(
    repository: SharePlateRepository = remember { SharePlateRepository() }
) {
    val currentRole by repository.currentRole.collectAsStateWithLifecycle()
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    val listings by repository.listings.collectAsStateWithLifecycle()
    val claims by repository.claims.collectAsStateWithLifecycle()
    val assignments by repository.assignments.collectAsStateWithLifecycle()
    val qrTokens by repository.qrTokens.collectAsStateWithLifecycle()
    val incidents by repository.incidents.collectAsStateWithLifecycle()
    val notifications by repository.notifications.collectAsStateWithLifecycle()
    val auditLogs by repository.auditLogs.collectAsStateWithLifecycle()
    val isDemoMode by repository.isDemoMode.collectAsStateWithLifecycle()

    var selectedNavIndex by remember { mutableStateOf(0) }
    var showRoleDropdown by remember { mutableStateOf(false) }
    var sidebarExpanded by remember { mutableStateOf(true) }
    var viewportMode by remember { mutableStateOf(ViewportMode.AUTO) }
    var globalHeaderSearch by remember { mutableStateOf("") }

    // Dialog States
    var selectedListingDetail by remember { mutableStateOf<FoodListing?>(null) }
    var claimTargetListing by remember { mutableStateOf<FoodListing?>(null) }
    var claimErrorMsg by remember { mutableStateOf<String?>(null) }
    var showCreateListingDialog by remember { mutableStateOf(false) }

    var qrDialogAssignment by remember { mutableStateOf<RescueAssignment?>(null) }
    var qrDialogTokenType by remember { mutableStateOf(TokenType.PICKUP) }
    var qrVerificationResult by remember { mutableStateOf<String?>(null) }
    var qrIsError by remember { mutableStateOf(false) }

    var incidentReportListing by remember { mutableStateOf<FoodListing?>(null) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    val impactMetrics = remember(listings, claims, assignments, isDemoMode) {
        repository.computeImpactMetrics()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Viewport Preview Switcher Toolbar: enables toggling Desktop (1440px), Tablet (768px), Mobile (375px), or Auto
        ViewportSwitcherBar(
            currentMode = viewportMode,
            onModeSelected = { viewportMode = it }
        )

        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val effectiveSizeClass = calculateEffectiveSizeClass(maxWidth, viewportMode)

            CompositionLocalProvider(LocalWindowSizeClass provides effectiveSizeClass) {
                when (effectiveSizeClass) {
                    WindowSizeClassType.EXPANDED -> {
                        // --- DESKTOP / LAPTOP LAYOUT WITH PERSISTENT 240PX SIDEBAR ---
                        // Fills 100% of available screen on laptops/desktops (>= 1024dp).
                        // In explicit Desktop (1440px) mode on smaller viewports, provides full 1440px width with horizontal scroll.
                        val desktopRowModifier = when {
                            viewportMode == ViewportMode.DESKTOP && maxWidth < 1440.dp -> {
                                Modifier
                                    .fillMaxHeight()
                                    .horizontalScroll(rememberScrollState())
                                    .width(1440.dp)
                            }
                            else -> {
                                Modifier.fillMaxSize()
                            }
                        }
                        Row(modifier = desktopRowModifier) {
                        // Left Desktop Persistent 240px Sidebar
                        Surface(
                            modifier = Modifier
                                .width(240.dp)
                                .fillMaxHeight(),
                            color = ForestGreenPrimary,
                            tonalElevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    // Branding Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = MintLight,
                                            shape = CircleShape,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.VolunteerActivism,
                                                    contentDescription = "SharePlate Logo",
                                                    tint = ForestGreenPrimary,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "SharePlate",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 19.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "Food Rescue Network",
                                                fontSize = 11.sp,
                                                color = MintLight.copy(alpha = 0.8f)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Nav Items
                                    val navItems = listOf(
                                        Triple("Home", Icons.Default.Home, 0),
                                        Triple("Dashboard", when (currentRole) {
                                            UserRole.DONOR -> Icons.Default.Storefront
                                            UserRole.RECIPIENT -> Icons.Default.Dashboard
                                            UserRole.VOLUNTEER -> Icons.AutoMirrored.Filled.DirectionsRun
                                            UserRole.ADMINISTRATOR -> Icons.Default.Security
                                        }, 1),
                                        Triple("AI Matcher", Icons.Default.AutoAwesome, 2),
                                        Triple("Rescue Radar", Icons.Default.Map, 3),
                                        Triple("Impact Ledger", Icons.Default.Insights, 4)
                                    )

                                    navItems.forEach { (label, icon, index) ->
                                        val isSelected = selectedNavIndex == index
                                        Surface(
                                            onClick = { selectedNavIndex = index },
                                            color = if (isSelected) MintLight.copy(alpha = 0.25f) else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .testTag("desktop_nav_${label.lowercase().replace(" ", "_")}")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = label,
                                                    tint = if (isSelected) MintLight else Color.White.copy(alpha = 0.8f),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = label,
                                                    fontSize = 14.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.85f)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Bottom Sidebar: Current User Info
                                Surface(
                                    color = Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = currentUser.name,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = null,
                                                tint = MintLight,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Text(
                                            text = currentUser.organisationName,
                                            color = MintLight.copy(alpha = 0.8f),
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            color = Color(currentRole.badgeColorHex),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = currentRole.displayName,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Desktop Main Content Column
                        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            // Desktop Top Navigation Bar (SharePlate branding, search, notifications, profile controls)
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Section Branding & Title
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = MintLight,
                                            shape = CircleShape,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.VolunteerActivism,
                                                    contentDescription = "SharePlate Logo",
                                                    tint = ForestGreenPrimary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "SharePlate",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 16.sp,
                                                color = ForestGreenPrimary
                                            )
                                            Text(
                                                text = when (selectedNavIndex) {
                                                    0 -> "Network Overview & Highlights"
                                                    1 -> "${currentRole.displayName} Dashboard"
                                                    2 -> "AI Smart Rescue Matcher"
                                                    3 -> "Logistics Radar & Hubs"
                                                    4 -> "Sustainability & Impact Ledger"
                                                    else -> "Food Rescue Network"
                                                },
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }

                                    // Global Search in Desktop Header
                                    OutlinedTextField(
                                        value = globalHeaderSearch,
                                        onValueChange = { globalHeaderSearch = it },
                                        placeholder = { Text("Search listings, kitchens, shelters...", fontSize = 13.sp) },
                                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp)) },
                                        trailingIcon = {
                                            if (globalHeaderSearch.isNotEmpty()) {
                                                IconButton(onClick = { globalHeaderSearch = "" }) {
                                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .width(300.dp)
                                            .height(48.dp)
                                            .testTag("desktop_header_search"),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true
                                    )

                                    // Profile Controls & Notifications
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Role Switcher Dropdown
                                        RoleSwitcherMenu(
                                            currentRole = currentRole,
                                            expanded = showRoleDropdown,
                                            onToggle = { showRoleDropdown = it },
                                            onSelectRole = { role ->
                                                repository.switchRole(role)
                                                selectedNavIndex = 1
                                            }
                                        )

                                        // Notification Icon with badge
                                        IconButton(
                                            onClick = { showNotificationDialog = true },
                                            modifier = Modifier.testTag("btn_notifications_desktop")
                                        ) {
                                            BadgedBox(
                                                badge = {
                                                    if (notifications.isNotEmpty()) {
                                                        Badge { Text("${notifications.size}") }
                                                    }
                                                }
                                            ) {
                                                Icon(imageVector = Icons.Default.NotificationsNone, contentDescription = "Notifications")
                                            }
                                        }

                                        // Profile Pill
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(20.dp),
                                            modifier = Modifier.clickable { showRoleDropdown = true }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    color = ForestGreenPrimary,
                                                    shape = CircleShape,
                                                    modifier = Modifier.size(26.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = currentUser.name.take(1),
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = currentUser.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    )
                                                    Text(
                                                        text = currentUser.organisationName,
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.outline
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }

                        // Content Body inside Responsive Container
                        ResponsiveContentContainer(modifier = Modifier.weight(1f)) {
                            MainContentScreen(
                                selectedIndex = selectedNavIndex,
                                currentRole = currentRole,
                                currentUser = currentUser,
                                repository = repository,
                                listings = listings,
                                claims = claims,
                                assignments = assignments,
                                incidents = incidents,
                                auditLogs = auditLogs,
                                impactMetrics = impactMetrics,
                                onSelectNav = { selectedNavIndex = it },
                                onListingDetail = { selectedListingDetail = it },
                                onClaimClick = {
                                    claimTargetListing = it
                                    claimErrorMsg = null
                                },
                                onCreateListing = { showCreateListingDialog = true },
                                onShowPickupPass = { asgn ->
                                    qrDialogAssignment = asgn
                                    qrDialogTokenType = TokenType.PICKUP
                                    qrVerificationResult = null
                                },
                                onShowReceiveQr = { asgn ->
                                    qrDialogAssignment = asgn
                                    qrDialogTokenType = TokenType.DELIVERY
                                    qrVerificationResult = null
                                },
                                onOpenQrScan = { asgn, type ->
                                    qrDialogAssignment = asgn
                                    qrDialogTokenType = type
                                    qrVerificationResult = null
                                }
                            )
                        }
                    }
                }
            }

            WindowSizeClassType.MEDIUM -> {
                // --- TABLET LAYOUT WITH NAVIGATION RAIL ---
                val tabletRowModifier = when {
                    viewportMode == ViewportMode.TABLET && maxWidth < 768.dp -> {
                        Modifier
                            .fillMaxHeight()
                            .horizontalScroll(rememberScrollState())
                            .width(768.dp)
                    }
                    else -> {
                        Modifier.fillMaxSize()
                    }
                }
                Row(modifier = tabletRowModifier) {
                    NavigationRail(
                        containerColor = ForestGreenPrimary,
                        contentColor = Color.White,
                        header = {
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = null,
                                tint = MintLight,
                                modifier = Modifier.size(28.dp).padding(vertical = 4.dp)
                            )
                        }
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        NavigationRailItem(
                            selected = selectedNavIndex == 0,
                            onClick = { selectedNavIndex = 0 },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = ForestGreenPrimary,
                                indicatorColor = MintLight,
                                unselectedIconColor = Color.White.copy(alpha = 0.8f),
                                unselectedTextColor = Color.White.copy(alpha = 0.8f),
                                selectedTextColor = Color.White
                            )
                        )
                        NavigationRailItem(
                            selected = selectedNavIndex == 1,
                            onClick = { selectedNavIndex = 1 },
                            icon = {
                                Icon(
                                    imageVector = when (currentRole) {
                                        UserRole.DONOR -> Icons.Default.Storefront
                                        UserRole.RECIPIENT -> Icons.Default.Dashboard
                                        UserRole.VOLUNTEER -> Icons.AutoMirrored.Filled.DirectionsRun
                                        UserRole.ADMINISTRATOR -> Icons.Default.Security
                                    },
                                    contentDescription = "Dashboard"
                                )
                            },
                            label = { Text("Dashboard", fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = ForestGreenPrimary,
                                indicatorColor = MintLight,
                                unselectedIconColor = Color.White.copy(alpha = 0.8f),
                                unselectedTextColor = Color.White.copy(alpha = 0.8f),
                                selectedTextColor = Color.White
                            )
                        )
                        NavigationRailItem(
                            selected = selectedNavIndex == 2,
                            onClick = { selectedNavIndex = 2 },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI") },
                            label = { Text("AI Match", fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = ForestGreenPrimary,
                                indicatorColor = MintLight,
                                unselectedIconColor = Color.White.copy(alpha = 0.8f),
                                unselectedTextColor = Color.White.copy(alpha = 0.8f),
                                selectedTextColor = Color.White
                            )
                        )
                        NavigationRailItem(
                            selected = selectedNavIndex == 3,
                            onClick = { selectedNavIndex = 3 },
                            icon = { Icon(Icons.Default.Map, contentDescription = "Radar") },
                            label = { Text("Radar", fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = ForestGreenPrimary,
                                indicatorColor = MintLight,
                                unselectedIconColor = Color.White.copy(alpha = 0.8f),
                                unselectedTextColor = Color.White.copy(alpha = 0.8f),
                                selectedTextColor = Color.White
                            )
                        )
                        NavigationRailItem(
                            selected = selectedNavIndex == 4,
                            onClick = { selectedNavIndex = 4 },
                            icon = { Icon(Icons.Default.Insights, contentDescription = "Impact") },
                            label = { Text("Impact", fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = ForestGreenPrimary,
                                indicatorColor = MintLight,
                                unselectedIconColor = Color.White.copy(alpha = 0.8f),
                                unselectedTextColor = Color.White.copy(alpha = 0.8f),
                                selectedTextColor = Color.White
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "SharePlate • ${currentRole.displayName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            },
                            actions = {
                                RoleSwitcherMenu(
                                    currentRole = currentRole,
                                    expanded = showRoleDropdown,
                                    onToggle = { showRoleDropdown = it },
                                    onSelectRole = { role ->
                                        repository.switchRole(role)
                                        selectedNavIndex = 1
                                    }
                                )
                                IconButton(onClick = { showNotificationDialog = true }) {
                                    Icon(imageVector = Icons.Default.NotificationsNone, contentDescription = "Notifications")
                                }
                            }
                        )

                        ResponsiveContentContainer(modifier = Modifier.weight(1f)) {
                            MainContentScreen(
                                selectedIndex = selectedNavIndex,
                                currentRole = currentRole,
                                currentUser = currentUser,
                                repository = repository,
                                listings = listings,
                                claims = claims,
                                assignments = assignments,
                                incidents = incidents,
                                auditLogs = auditLogs,
                                impactMetrics = impactMetrics,
                                onSelectNav = { selectedNavIndex = it },
                                onListingDetail = { selectedListingDetail = it },
                                onClaimClick = {
                                    claimTargetListing = it
                                    claimErrorMsg = null
                                },
                                onCreateListing = { showCreateListingDialog = true },
                                onShowPickupPass = { asgn ->
                                    qrDialogAssignment = asgn
                                    qrDialogTokenType = TokenType.PICKUP
                                    qrVerificationResult = null
                                },
                                onShowReceiveQr = { asgn ->
                                    qrDialogAssignment = asgn
                                    qrDialogTokenType = TokenType.DELIVERY
                                    qrVerificationResult = null
                                },
                                onOpenQrScan = { asgn, type ->
                                    qrDialogAssignment = asgn
                                    qrDialogTokenType = type
                                    qrVerificationResult = null
                                }
                            )
                        }
                    }
                }
            }

            WindowSizeClassType.COMPACT -> {
                // --- PHONE COMPACT MOBILE LAYOUT WITH BOTTOM NAVIGATION ---
                val mobileBoxModifier = when {
                    viewportMode == ViewportMode.MOBILE && maxWidth > 480.dp -> {
                        Modifier.fillMaxHeight().width(375.dp)
                    }
                    else -> {
                        Modifier.fillMaxSize()
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(modifier = mobileBoxModifier) {
                        Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = ForestGreenPrimary,
                                        shape = CircleShape,
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.VolunteerActivism,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "SharePlate",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp,
                                            color = ForestGreenPrimary
                                        )
                                        Text(
                                            text = "Food Rescue Network",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            },
                            actions = {
                                RoleSwitcherMenu(
                                    currentRole = currentRole,
                                    expanded = showRoleDropdown,
                                    onToggle = { showRoleDropdown = it },
                                    onSelectRole = { role ->
                                        repository.switchRole(role)
                                        selectedNavIndex = 1
                                    }
                                )
                                IconButton(
                                    onClick = { showNotificationDialog = true },
                                    modifier = Modifier.testTag("btn_notifications")
                                ) {
                                    BadgedBox(
                                        badge = {
                                            if (notifications.isNotEmpty()) {
                                                Badge { Text("${notifications.size}") }
                                            }
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.NotificationsNone, contentDescription = "Notifications")
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp
                        ) {
                            NavigationBarItem(
                                selected = selectedNavIndex == 0,
                                onClick = { selectedNavIndex = 0 },
                                icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontSize = 11.sp) },
                                modifier = Modifier.testTag("nav_home")
                            )
                            NavigationBarItem(
                                selected = selectedNavIndex == 1,
                                onClick = { selectedNavIndex = 1 },
                                icon = {
                                    Icon(
                                        imageVector = when (currentRole) {
                                            UserRole.DONOR -> Icons.Default.Storefront
                                            UserRole.RECIPIENT -> Icons.Default.Dashboard
                                            UserRole.VOLUNTEER -> Icons.AutoMirrored.Filled.DirectionsRun
                                            UserRole.ADMINISTRATOR -> Icons.Default.Security
                                        },
                                        contentDescription = "Dashboard"
                                    )
                                },
                                label = { Text("Dashboard", fontSize = 11.sp) },
                                modifier = Modifier.testTag("nav_dashboard")
                            )
                            NavigationBarItem(
                                selected = selectedNavIndex == 2,
                                onClick = { selectedNavIndex = 2 },
                                icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Matcher") },
                                label = { Text("AI Match", fontSize = 11.sp) },
                                modifier = Modifier.testTag("nav_ai")
                            )
                            NavigationBarItem(
                                selected = selectedNavIndex == 3,
                                onClick = { selectedNavIndex = 3 },
                                icon = { Icon(imageVector = Icons.Default.Map, contentDescription = "Radar") },
                                label = { Text("Radar", fontSize = 11.sp) },
                                modifier = Modifier.testTag("nav_map")
                            )
                            NavigationBarItem(
                                selected = selectedNavIndex == 4,
                                onClick = { selectedNavIndex = 4 },
                                icon = { Icon(imageVector = Icons.Default.Insights, contentDescription = "Impact") },
                                label = { Text("Impact", fontSize = 11.sp) },
                                modifier = Modifier.testTag("nav_impact")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        MainContentScreen(
                            selectedIndex = selectedNavIndex,
                            currentRole = currentRole,
                            currentUser = currentUser,
                            repository = repository,
                            listings = listings,
                            claims = claims,
                            assignments = assignments,
                            incidents = incidents,
                            auditLogs = auditLogs,
                            impactMetrics = impactMetrics,
                            onSelectNav = { selectedNavIndex = it },
                            onListingDetail = { selectedListingDetail = it },
                            onClaimClick = {
                                claimTargetListing = it
                                claimErrorMsg = null
                            },
                            onCreateListing = { showCreateListingDialog = true },
                            onShowPickupPass = { asgn ->
                                qrDialogAssignment = asgn
                                qrDialogTokenType = TokenType.PICKUP
                                qrVerificationResult = null
                            },
                            onShowReceiveQr = { asgn ->
                                qrDialogAssignment = asgn
                                qrDialogTokenType = TokenType.DELIVERY
                                qrVerificationResult = null
                            },
                            onOpenQrScan = { asgn, type ->
                                qrDialogAssignment = asgn
                                qrDialogTokenType = type
                                qrVerificationResult = null
                            }
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

    // --- DIALOGS (SHARED ACROSS ALL SCREEN SIZES) ---
    if (selectedListingDetail != null) {
        FoodDetailDialog(
            listing = selectedListingDetail!!,
            onDismiss = { selectedListingDetail = null },
            onClaimClicked = {
                claimTargetListing = selectedListingDetail
                claimErrorMsg = null
            },
            onReportSafetyClicked = {
                incidentReportListing = selectedListingDetail
            }
        )
    }

    if (claimTargetListing != null) {
        ClaimDialog(
            listing = claimTargetListing!!,
            errorMessage = claimErrorMsg,
            onDismiss = {
                claimTargetListing = null
                claimErrorMsg = null
            },
            onConfirmClaim = { qty ->
                val result = repository.claimFoodListing(claimTargetListing!!.id, qty, currentUser)
                result.fold(
                    onSuccess = {
                        claimTargetListing = null
                        claimErrorMsg = null
                        selectedNavIndex = 1
                    },
                    onFailure = { err ->
                        claimErrorMsg = err.message ?: "Failed to reserve food. Please check remaining quantity."
                    }
                )
            }
        )
    }

    if (showCreateListingDialog) {
        CreateListingDialog(
            onDismiss = { showCreateListingDialog = false },
            onSubmit = { title, desc, cat, diet, qty, unit, servings, prep, useBy, pickup, storage, temp, allergens, pkg, window, addr, attested ->
                val res = repository.createFoodListing(
                    title = title,
                    description = desc,
                    category = cat,
                    dietaryType = diet,
                    quantity = qty,
                    unit = unit,
                    estimatedServings = servings,
                    preparationTime = prep,
                    useByDeadline = useBy,
                    pickupDeadline = pickup,
                    storageCondition = storage,
                    temperatureReading = temp,
                    allergens = allergens,
                    crossContactWarning = "Commercial hygiene verified batch.",
                    packagingRequirement = pkg,
                    pickupWindow = window,
                    address = addr,
                    neighborhood = "Downtown",
                    safetyAttested = attested,
                    donor = currentUser
                )
                if (res.isSuccess) {
                    showCreateListingDialog = false
                }
            }
        )
    }

    if (qrDialogAssignment != null) {
        val expectedToken = if (qrDialogTokenType == TokenType.PICKUP) {
            qrDialogAssignment!!.pickupQrToken
        } else {
            qrDialogAssignment!!.deliveryQrToken
        }

        QrHandoverDialog(
            assignment = qrDialogAssignment!!,
            tokenType = qrDialogTokenType,
            expectedToken = expectedToken,
            verificationResult = qrVerificationResult,
            isError = qrIsError,
            onDismiss = {
                qrDialogAssignment = null
                qrVerificationResult = null
            },
            onVerifyToken = { tokenStr ->
                val res = repository.verifyAndExecuteQrHandover(tokenStr, currentUser)
                res.fold(
                    onSuccess = { msg ->
                        qrVerificationResult = msg
                        qrIsError = false
                    },
                    onFailure = { err ->
                        qrVerificationResult = "Handover Failed: ${err.message}"
                        qrIsError = true
                    }
                )
            }
        )
    }

    if (incidentReportListing != null) {
        IncidentReportDialog(
            listing = incidentReportListing!!,
            onDismiss = { incidentReportListing = null },
            onSubmitIncident = { type, desc ->
                repository.submitSafetyIncident(
                    listingId = incidentReportListing!!.id,
                    listingTitle = incidentReportListing!!.title,
                    type = type,
                    description = desc
                )
                incidentReportListing = null
            }
        )
    }

    if (showNotificationDialog) {
        NotificationDialog(
            notifications = notifications,
            onDismiss = { showNotificationDialog = false }
        )
    }
}

@Composable
private fun RoleSwitcherMenu(
    currentRole: UserRole,
    expanded: Boolean,
    onToggle: (Boolean) -> Unit,
    onSelectRole: (UserRole) -> Unit
) {
    Box {
        Surface(
            onClick = { onToggle(true) },
            color = Color(currentRole.badgeColorHex).copy(alpha = 0.12f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("role_switcher_chip")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentRole.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(currentRole.badgeColorHex)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color(currentRole.badgeColorHex),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onToggle(false) }
        ) {
            UserRole.values().forEach { role ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = role.displayName,
                            fontWeight = if (currentRole == role) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelectRole(role)
                        onToggle(false)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = when (role) {
                                UserRole.DONOR -> Icons.Default.Storefront
                                UserRole.RECIPIENT -> Icons.Default.VolunteerActivism
                                UserRole.VOLUNTEER -> Icons.Default.ElectricBike
                                UserRole.ADMINISTRATOR -> Icons.Default.AdminPanelSettings
                            },
                            contentDescription = null,
                            tint = Color(role.badgeColorHex)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun MainContentScreen(
    selectedIndex: Int,
    currentRole: UserRole,
    currentUser: UserProfile,
    repository: SharePlateRepository,
    listings: List<FoodListing>,
    claims: List<RescueClaim>,
    assignments: List<RescueAssignment>,
    incidents: List<SafetyIncidentReport>,
    auditLogs: List<AuditEntry>,
    impactMetrics: ImpactMetrics,
    onSelectNav: (Int) -> Unit,
    onListingDetail: (FoodListing) -> Unit,
    onClaimClick: (FoodListing) -> Unit,
    onCreateListing: () -> Unit,
    onShowPickupPass: (RescueAssignment) -> Unit,
    onShowReceiveQr: (RescueAssignment) -> Unit,
    onOpenQrScan: (RescueAssignment, TokenType) -> Unit
) {
    when (selectedIndex) {
        0 -> {
            LandingScreen(
                impactMetrics = impactMetrics,
                recentListings = listings,
                onRoleSelected = { role ->
                    repository.switchRole(role)
                    onSelectNav(1)
                },
                onNavigateToExplore = {
                    repository.switchRole(UserRole.RECIPIENT)
                    onSelectNav(1)
                },
                onNavigateToAiMatcher = { onSelectNav(2) },
                onListingClicked = onListingDetail
            )
        }
        1 -> {
            when (currentRole) {
                UserRole.DONOR -> {
                    DonorScreen(
                        donorUser = currentUser,
                        listings = listings,
                        claims = claims,
                        assignments = assignments,
                        onCreateListingClicked = onCreateListing,
                        onListingClicked = onListingDetail,
                        onShowPickupPass = onShowPickupPass
                    )
                }
                UserRole.RECIPIENT -> {
                    RecipientScreen(
                        recipientUser = currentUser,
                        listings = listings,
                        claims = claims,
                        assignments = assignments,
                        onClaimClicked = onClaimClick,
                        onListingClicked = onListingDetail,
                        onShowReceiveQr = onShowReceiveQr
                    )
                }
                UserRole.VOLUNTEER -> {
                    VolunteerScreen(
                        volunteerUser = currentUser,
                        assignments = assignments,
                        onAcceptAssignment = { repository.acceptAssignment(it) },
                        onOpenQrScan = onOpenQrScan
                    )
                }
                UserRole.ADMINISTRATOR -> {
                    AdminScreen(
                        adminUser = currentUser,
                        listings = listings,
                        incidents = incidents,
                        auditLogs = auditLogs,
                        onResolveIncident = { id, safe -> repository.resolveIncident(id, safe) }
                    )
                }
            }
        }
        2 -> {
            AiMatcherScreen(
                listings = listings,
                currentRecipient = currentUser,
                onListingClicked = onListingDetail,
                onClaimClicked = onClaimClick
            )
        }
        3 -> {
            MapScreen(
                listings = listings,
                onListingClicked = onListingDetail
            )
        }
        4 -> {
            ImpactScreen(
                metrics = impactMetrics,
                onResetDemoData = { repository.resetToDemoData() }
            )
        }
    }
}
