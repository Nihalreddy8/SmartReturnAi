package com.smartreturn.ai.ui.alert

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartreturn.ai.ui.components.*
import com.smartreturn.ai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReplay: (Long) -> Unit
) {
    var simulationTriggered by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundDeep,
        topBar = {
            SmartReturnTopBar(
                title = "Forgotten Items",
                subtitle = "Left-behind detection",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simulate leaving button (Phase 9 simulation trigger)
            SimulateLeavingCard(
                triggered = simulationTriggered,
                onSimulate = { simulationTriggered = true }
            )

            AnimatedVisibility(
                visible = simulationTriggered,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Expected vs current comparison
                    ExpectedCurrentCard()

                    // Alert card
                    ForgottenItemAlertCard(
                        onViewReplay = { onNavigateToReplay(3L) }
                    )
                }
            }

            if (!simulationTriggered) {
                EmptyState(
                    emoji = "✅",
                    title = "All items accounted for",
                    subtitle = "Press Simulate Leaving to trigger the left-behind detection demo"
                )
            }
        }
    }
}

@Composable
private fun SimulateLeavingCard(triggered: Boolean, onSimulate: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BackgroundSurface,
        border = BorderStroke(1.dp, if (triggered) OutlineVariant else AlertWarning.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.DirectionsWalk, null, tint = AlertWarning, modifier = Modifier.size(20.dp))
                Text(text = "Demo: Simulate Leaving", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            }
            Text(
                text = "Simulates walking away from Classroom 204 with only your Backpack and Laptop, leaving the Water Bottle behind.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Button(
                onClick = onSimulate,
                enabled = !triggered,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AlertWarning,
                    contentColor = BackgroundDeep,
                    disabledContainerColor = SurfaceContainer,
                    disabledContentColor = TextTertiary
                )
            ) {
                if (triggered) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                        Text(text = "Simulation Active", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.DirectionsWalk, null, modifier = Modifier.size(16.dp))
                        Text(text = "Simulate Leaving", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpectedCurrentCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BackgroundSurface,
        border = BorderStroke(1.dp, OutlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Item Check", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Expected column
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Expected", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    listOf("🎒 Backpack", "💻 Laptop", "🥤 Water Bottle").forEach { item ->
                        ItemCheckRow(label = item, present = true)
                    }
                }
                VerticalDivider(modifier = Modifier.height(100.dp), color = OutlineVariant)
                // Current column
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Detected Now", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    ItemCheckRow(label = "🎒 Backpack", present = true)
                    ItemCheckRow(label = "💻 Laptop", present = true)
                    ItemCheckRow(label = "🥤 Water Bottle", present = false)
                }
            }
        }
    }
}

@Composable
private fun ItemCheckRow(label: String, present: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(
            imageVector = if (present) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (present) StatusActive else StatusMissing,
            modifier = Modifier.size(16.dp)
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall,
            color = if (present) TextPrimary else TextTertiary)
    }
}

@Composable
private fun ForgottenItemAlertCard(onViewReplay: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = StatusMissing.copy(alpha = 0.08f),
        border = BorderStroke(1.5.dp, StatusMissing.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "⚠️", fontSize = 36.sp)
            Text(
                text = "POSSIBLE LEFT-BEHIND ITEM",
                style = MaterialTheme.typography.labelLarge,
                color = StatusMissing,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "You may have left your",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🥤", fontSize = 24.sp)
                Text(
                    text = "Water Bottle",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "in Classroom 204",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                InfoItem(emoji = "📍", label = "Classroom 204")
                InfoItem(emoji = "🕐", label = "11:20 AM")
                InfoItem(emoji = "🎯", label = "91% conf.")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onViewReplay,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = CyanOnPrimary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Timeline, null, modifier = Modifier.size(18.dp))
                    Text(text = "View Memory Replay", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun InfoItem(emoji: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = emoji, fontSize = 16.sp)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}