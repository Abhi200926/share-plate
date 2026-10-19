package com.example.ui.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintLight

enum class WindowSizeClassType {
    COMPACT,   // < 768dp (Mobile phones: 320px - 767px)
    MEDIUM,    // 768dp - 1023dp (Tablets, iPads: 768px - 1023px)
    EXPANDED   // >= 1024dp (Laptops, Desktops, Monitors: 1024px, 1440px+)
}

val LocalWindowSizeClass = compositionLocalOf { WindowSizeClassType.COMPACT }

enum class ViewportMode(val title: String, val shortLabel: String, val subtitle: String) {
    AUTO("Auto Responsive", "Auto", "Adapts fluidly based on current screen width"),
    DESKTOP("Desktop / Laptop (1440px)", "Desktop (1440px)", "Persistent 240px sidebar, 4-stat row, wide table/grid"),
    TABLET("Tablet View (768px)", "Tablet (768px)", "Collapsible navigation rail and 2-column cards"),
    MOBILE("Mobile View (375px)", "Mobile (375px)", "Bottom navigation bar and compact vertical cards")
}

fun calculateWindowSizeClass(width: Dp): WindowSizeClassType {
    return when {
        width < 768.dp -> WindowSizeClassType.COMPACT
        width < 1024.dp -> WindowSizeClassType.MEDIUM
        else -> WindowSizeClassType.EXPANDED
    }
}

fun calculateEffectiveSizeClass(
    width: Dp,
    mode: ViewportMode
): WindowSizeClassType {
    return when (mode) {
        ViewportMode.DESKTOP -> WindowSizeClassType.EXPANDED
        ViewportMode.TABLET -> WindowSizeClassType.MEDIUM
        ViewportMode.MOBILE -> WindowSizeClassType.COMPACT
        ViewportMode.AUTO -> calculateWindowSizeClass(width)
    }
}

@Composable
fun ViewportSwitcherBar(
    currentMode: ViewportMode,
    onModeSelected: (ViewportMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF1E2824),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Devices,
                    contentDescription = null,
                    tint = MintLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "VIEWPORT PREVIEW:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintLight
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ViewportMode.values().forEach { mode ->
                    val isSelected = currentMode == mode
                    Surface(
                        onClick = { onModeSelected(mode) },
                        color = if (isSelected) MintLight else Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("viewport_${mode.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (mode) {
                                    ViewportMode.AUTO -> Icons.Default.AspectRatio
                                    ViewportMode.DESKTOP -> Icons.Default.Laptop
                                    ViewportMode.TABLET -> Icons.Default.TabletMac
                                    ViewportMode.MOBILE -> Icons.Default.Smartphone
                                },
                                contentDescription = mode.shortLabel,
                                tint = if (isSelected) ForestGreenPrimary else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = mode.shortLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) ForestGreenPrimary else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Responsive container that constrains extra-wide desktop screens
 * (up to 1440dp) to prevent awkward stretched cards, while filling
 * phones and tablets fluidly.
 */
@Composable
fun ResponsiveContentContainer(
    modifier: Modifier = Modifier,
    maxWidth: Dp = 1440.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = maxWidth),
            content = content
        )
    }
}
