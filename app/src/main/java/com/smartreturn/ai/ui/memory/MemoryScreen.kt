package com.smartreturn.ai.ui.memory

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartreturn.ai.ui.components.ConfidenceBadge
import com.smartreturn.ai.ui.components.SectionHeader
import com.smartreturn.ai.ui.components.SmartReturnTopBar
import com.smartreturn.ai.ui.components.itemEmoji
import com.smartreturn.ai.ui.theme.*

private data class MemoryItem(
    val id: Long,
    val name: String,
    val type: String,
    val lastLocation: String,
    val lastTime: String,
    val confidence: Float,
    val isMissing: Boolean
)

private val demoMemoryItems = listOf(
    MemoryItem(1L, "Backpack", "backpack", "Current Location", "Just now", 0.96f, false),
    MemoryItem(2L, "Laptop", "laptop", "Classroom 204", "11:20 AM", 0.94f, false),
    MemoryItem(3L, "Water Bottle", "bottle", "Classroom 204", "11:20 AM", 0.91f, true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReplay: (Long) -> Unit
) {
    val itemCount = demoMemoryItems.size
    Scaffold(
        containerColor = BackgroundDeep,
        topBar = {
            SmartReturnTopBar(
                title = "My Memories",
                subtitle = "$itemCount items tracked",
                onBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val missingCount = demoMemoryItems.count { it.isMissing }
            if (missingCount > 0) {
                item {
                    MissingItemBanner(count = missingCount)
                }
            }
            item {
                SectionHeader(title = "ALL BELONGINGS")
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(demoMemoryItems) { item ->
                MemoryItemCard(item = item, onViewReplay = { onNavigateToReplay(item.id) })
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "OBSERVATION COUNT")
                Spacer(modifier = Modifier.height(8.dp))
                ObservationStatsCard()
            }
        }
    }
}

@Composable
private fun MissingItemBanner(count: Int) {
    val label = if (count > 1) "items" else "item"
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = StatusMissing.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, StatusMissing.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "⚠️", fontSize = 20.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$count $label may be missing",
                    style = MaterialTheme.typography.titleSmall,
                    color = StatusMissing,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Check the items below for last known locations",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun MemoryItemCard(item: MemoryItem, onViewReplay: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BackgroundSurface,
        border = BorderStroke(
            1.dp,
            if (item.isMissing) StatusMissing.copy(alpha = 0.5f) else OutlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            if (item.isMissing) StatusMissing.copy(alpha = 0.1f)
                            else SurfaceContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = itemEmoji(item.type), fontSize = 24.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Last seen: ${item.lastLocation}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = item.lastTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
                if (item.isMissing) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StatusMissing.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Missing",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusMissing,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ConfidenceBadge(confidence = item.confidence)
                TextButton(
                    onClick = onViewReplay,
                    colors = ButtonDefaults.textButtonColors(contentColor = CyanPrimary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Timeline,
                            null,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Memory Replay",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ObservationStatsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BackgroundSurface,
        border = BorderStroke(1.dp, OutlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = "4", label = "Locations")
            VerticalDivider(modifier = Modifier.height(40.dp), color = OutlineVariant)
            StatItem(value = "12", label = "Observations")
            VerticalDivider(modifier = Modifier.height(40.dp), color = OutlineVariant)
            StatItem(value = "94%", label = "Avg confidence")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = CyanPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}
