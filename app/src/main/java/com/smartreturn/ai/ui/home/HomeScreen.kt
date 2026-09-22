package com.smartreturn.ai.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartreturn.ai.ui.components.*
import com.smartreturn.ai.ui.theme.*

// ─── Demo data models (Phase 3 will replace with Room entities) ───────────────

data class DemoItem(
    val id: Long,
    val name: String,
    val type: String,
    val lastLocation: String,
    val lastTime: String,
    val confidence: Float,
    val status: ItemStatus
)

enum class ItemStatus { PRESENT, LAST_SEEN, MISSING, UNKNOWN }

data class RecentMemory(
    val time: String,
    val objectType: String,
    val location: String,
    val confidence: Float
)

// Demo seed — will be replaced by database in Phase 3
private val demoItems = listOf(
    DemoItem(1L, "Backpack", "backpack", "Current Location", "Just now", 0.96f, ItemStatus.PRESENT),
    DemoItem(2L, "Laptop", "laptop", "Classroom 204", "11:20 AM", 0.94f, ItemStatus.LAST_SEEN),
    DemoItem(3L, "Water Bottle", "bottle", "Classroom 204", "11:20 AM", 0.91f, ItemStatus.MISSING)
)

private val demoMemories = listOf(
    RecentMemory("11:20 AM", "bottle", "Classroom 204", 0.91f),
    RecentMemory("10:05 AM", "laptop", "College", 0.94f),
    RecentMemory("09:10 AM", "backpack", "Home", 0.96f)
)

// ─── HomeScreen ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddItem: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToMemory: () -> Unit,
    onNavigateToAsk: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAlert: () -> Unit,
    onNavigateToReplay: (Long) -> Unit
) {
    Scaffold(
        containerColor = BackgroundDeep,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SmartReturn AI",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimary
                        )
                        Text(
                            text = "Your physical-world memory",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDeep
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ── AI Status Banner ──
            item {
                AiStatusBanner(modifier = Modifier.padding(16.dp))
            }

            // ── Quick Actions ──
            item {
                QuickActionsRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onScan = onNavigateToScan,
                    onAddItem = onNavigateToAddItem,
                    onAsk = onNavigateToAsk,
                    onAlert = onNavigateToAlert
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // ── Belongings Section ──
            item {
                SectionHeader(
                    title = "MY BELONGINGS",
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    TextButton(onClick = onNavigateToAddItem) {
                        Text(
                            text = "+ Add",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyanPrimary
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            items(demoItems) { item ->
                BelongingCard(
                    item = item,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .fillMaxWidth(),
                    onClick = { onNavigateToReplay(item.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // ── Recent Memory ──
            item {
                SectionHeader(
                    title = "RECENT MEMORY",
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    TextButton(onClick = onNavigateToMemory) {
                        Text(
                            text = "See all",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyanPrimary
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            items(demoMemories) { mem ->
                RecentMemoryCard(
                    memory = mem,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // ── Memory Replay CTA ──
            item {
                MemoryReplayCta(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { onNavigateToReplay(3L) } // Water Bottle by default
                )
            }
        }
    }
}

// ─── AI Status Banner ─────────────────────────────────────────────────────────

@Composable
fun AiStatusBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainer,
        border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pulsing dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Memory Active",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary
                )
                Text(
                    text = "3 items being tracked · Last scan 11:20 AM",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ConfidenceLow.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "⚠️ 1 missing",
                    style = MaterialTheme.typography.labelSmall,
                    color = ConfidenceLow,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ─── Quick Action Row ─────────────────────────────────────────────────────────

@Composable
fun QuickActionsRow(
    modifier: Modifier = Modifier,
    onScan: () -> Unit,
    onAddItem: () -> Unit,
    onAsk: () -> Unit,
    onAlert: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickActionButton(
            icon = Icons.Filled.CameraAlt,
            label = "Scan",
            color = CyanPrimary,
            modifier = Modifier.weight(1f),
            onClick = onScan
        )
        QuickActionButton(
            icon = Icons.Filled.Add,
            label = "Add Item",
            color = VioletSecondary,
            modifier = Modifier.weight(1f),
            onClick = onAddItem
        )
        QuickActionButton(
            icon = Icons.Filled.Psychology,
            label = "Ask AI",
            color = CyanPrimary,
            modifier = Modifier.weight(1f),
            onClick = onAsk
        )
        QuickActionButton(
            icon = Icons.Filled.NotificationsActive,
            label = "Alerts",
            color = AlertWarning,
            modifier = Modifier.weight(1f),
            onClick = onAlert
        )
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                maxLines = 1
            )
        }
    }
}

// ─── Belonging Card ───────────────────────────────────────────────────────────

@Composable
fun BelongingCard(
    item: DemoItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val statusColor = when (item.status) {
        ItemStatus.PRESENT   -> StatusActive
        ItemStatus.LAST_SEEN -> ConfidenceMedium
        ItemStatus.MISSING   -> StatusMissing
        ItemStatus.UNKNOWN   -> StatusUnknown
    }
    val statusLabel = when (item.status) {
        ItemStatus.PRESENT   -> "Present"
        ItemStatus.LAST_SEEN -> "Last seen"
        ItemStatus.MISSING   -> "⚠ Missing"
        ItemStatus.UNKNOWN   -> "Unknown"
    }

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = BackgroundSurface,
        border = BorderStroke(
            width = if (item.status == ItemStatus.MISSING) 1.dp else 1.dp,
            color = if (item.status == ItemStatus.MISSING)
                StatusMissing.copy(alpha = 0.5f)
            else
                OutlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Object emoji in a circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = itemEmoji(item.type), fontSize = 22.sp)
            }

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "📍", fontSize = 11.sp)
                    Text(
                        text = item.lastLocation,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                    Text(
                        text = item.lastTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }

            // Status badge
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                ConfidenceBadge(confidence = item.confidence)
            }
        }
    }
}

// ─── Recent Memory Card ───────────────────────────────────────────────────────

@Composable
fun RecentMemoryCard(memory: RecentMemory, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundSurface)
            .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = itemEmoji(memory.objectType),
            fontSize = 24.sp
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = memory.objectType.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )
            Text(
                text = "${locationEmoji(memory.location)} ${memory.location}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = memory.time,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
            Spacer(modifier = Modifier.height(4.dp))
            ConfidenceBadge(confidence = memory.confidence)
        }
    }
}

// ─── Memory Replay CTA ────────────────────────────────────────────────────────

@Composable
fun MemoryReplayCta(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BackgroundSurface,
        border = BorderStroke(1.dp, VioletSecondary.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            VioletSecondary.copy(alpha = 0.08f),
                            BackgroundSurface
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Timeline,
                contentDescription = null,
                tint = VioletSecondary,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Memory Replay",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "View your Water Bottle's full journey",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextTertiary
            )
        }
    }
}
