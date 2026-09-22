package com.smartreturn.ai.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartreturn.ai.ui.components.SectionHeader
import com.smartreturn.ai.ui.components.SmartReturnTopBar
import com.smartreturn.ai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {

    var notificationsEnabled by remember { mutableStateOf(true) }
    var highConfidenceOnly by remember { mutableStateOf(false) }
    var demoModeEnabled by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = BackgroundDeep,
        topBar = {
            SmartReturnTopBar(
                title = "Settings",
                subtitle = "SmartReturn AI preferences",
                onBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── Notifications ──────────────────────────────────────────────
            SectionHeader(title = "NOTIFICATIONS")
            Spacer(modifier = Modifier.height(0.dp))

            SettingsCard {
                ToggleRow(
                    icon = Icons.Default.Notifications,
                    iconTint = CyanPrimary,
                    title = "Forgotten Item Alerts",
                    subtitle = "Notify when an item may be left behind",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
                HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 4.dp))
                ToggleRow(
                    icon = Icons.Default.FilterAlt,
                    iconTint = VioletSecondary,
                    title = "High Confidence Only",
                    subtitle = "Only alert at ≥90% detection confidence",
                    checked = highConfidenceOnly,
                    onCheckedChange = { highConfidenceOnly = it }
                )
            }

            // ── Detection ──────────────────────────────────────────────────
            SectionHeader(title = "DETECTION")
            Spacer(modifier = Modifier.height(0.dp))

            SettingsCard {
                InfoRow(
                    icon = Icons.Default.Psychology,
                    iconTint = CyanPrimary,
                    title = "AI Model",
                    value = "TFLite MobileNet (Phase 5)"
                )
                HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 4.dp))
                InfoRow(
                    icon = Icons.Default.Speed,
                    iconTint = ConfidenceMedium,
                    title = "Confidence Threshold",
                    value = "70%"
                )
                HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 4.dp))
                InfoRow(
                    icon = Icons.Default.Inventory2,
                    iconTint = StatusActive,
                    title = "Supported Objects",
                    value = "Backpack · Laptop · Bottle"
                )
            }

            // ── Location ───────────────────────────────────────────────────
            SectionHeader(title = "LOCATION")
            Spacer(modifier = Modifier.height(0.dp))

            SettingsCard {
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    iconTint = CyanPrimary,
                    title = "Location Mode",
                    value = "Demo (manual selection)"
                )
                HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 4.dp))
                InfoRow(
                    icon = Icons.Default.Place,
                    iconTint = TextSecondary,
                    title = "Demo Locations",
                    value = "Home · College · Classroom 204 · Canteen · Library"
                )
            }

            // ── Privacy ────────────────────────────────────────────────────
            SectionHeader(title = "PRIVACY")
            Spacer(modifier = Modifier.height(0.dp))

            SettingsCard {
                InfoRow(
                    icon = Icons.Default.Lock,
                    iconTint = StatusActive,
                    title = "Data Storage",
                    value = "Local only — no cloud upload"
                )
                HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 4.dp))
                InfoRow(
                    icon = Icons.Default.Videocam,
                    iconTint = StatusActive,
                    title = "Video Storage",
                    value = "Disabled — metadata only"
                )
                HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 4.dp))
                InfoRow(
                    icon = Icons.Default.Face,
                    iconTint = StatusActive,
                    title = "Facial Recognition",
                    value = "Never used"
                )
            }

            // ── Demo Mode ─────────────────────────────────────────────────
            SectionHeader(title = "DEMO MODE")
            Spacer(modifier = Modifier.height(0.dp))

            SettingsCard {
                ToggleRow(
                    icon = Icons.Default.PlayCircle,
                    iconTint = AlertWarning,
                    title = "Demo Mode",
                    subtitle = "Use pre-seeded data for hackathon presentation",
                    checked = demoModeEnabled,
                    onCheckedChange = { demoModeEnabled = it }
                )
            }

            // ── About ─────────────────────────────────────────────────────
            SectionHeader(title = "ABOUT")
            Spacer(modifier = Modifier.height(0.dp))

            SettingsCard {
                InfoRow(
                    icon = Icons.Default.Info,
                    iconTint = CyanPrimary,
                    title = "Version",
                    value = "1.0.0 (Phase 1)"
                )
                HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 4.dp))
                InfoRow(
                    icon = Icons.Default.EmojiEvents,
                    iconTint = AlertWarning,
                    title = "Event",
                    value = "iQOO Hackathon 2026"
                )
                HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 4.dp))
                InfoRow(
                    icon = Icons.Default.Category,
                    iconTint = VioletSecondary,
                    title = "Track",
                    value = "Smart Living"
                )
            }

            // Bottom tagline
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "◉", fontSize = 20.sp, color = CyanPrimary)
                Text(
                    text = "SmartReturn AI",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "See it. Remember it. Return to it.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
        }
    }
}

// ─── Settings card container ─────────────────────────────────────────────────

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BackgroundSurface,
        border = BorderStroke(1.dp, OutlineVariant)
    ) {
        Column(modifier = Modifier.padding(4.dp), content = content)
    }
}

// ─── Toggle row ───────────────────────────────────────────────────────────────

@Composable
private fun ToggleRow(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyanOnPrimary,
                checkedTrackColor = CyanPrimary,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = SurfaceContainer
            )
        )
    }
}

// ─── Info row ─────────────────────────────────────────────────────────────────

@Composable
private fun InfoRow(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}
