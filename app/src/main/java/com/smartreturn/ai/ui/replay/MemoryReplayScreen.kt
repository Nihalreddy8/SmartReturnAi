package com.smartreturn.ai.ui.replay

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartreturn.ai.ui.components.*
import com.smartreturn.ai.ui.theme.*

private data class TimelineEvent(
    val time: String,
    val location: String,
    val detected: Boolean,
    val confidence: Float,
    val isLast: Boolean = false
)

// Demo timeline for Water Bottle (itemId=3) — other items get a simpler timeline
private val bottleTimeline = listOf(
    TimelineEvent("09:10 AM", "Home", true, 0.95f),
    TimelineEvent("10:05 AM", "College", true, 0.93f),
    TimelineEvent("11:20 AM", "Classroom 204", true, 0.91f, isLast = true),
    TimelineEvent("01:15 PM", "Canteen", false, 0f)
)

private fun getTimeline(itemId: Long): Triple<String, String, List<TimelineEvent>> = when (itemId) {
    1L -> Triple("Backpack", "backpack", listOf(
        TimelineEvent("09:10 AM", "Home", true, 0.96f),
        TimelineEvent("10:05 AM", "College", true, 0.95f),
        TimelineEvent("11:20 AM", "Classroom 204", true, 0.96f),
        TimelineEvent("01:15 PM", "Canteen", true, 0.94f, isLast = true)
    ))
    2L -> Triple("Laptop", "laptop", listOf(
        TimelineEvent("09:10 AM", "Home", true, 0.94f),
        TimelineEvent("10:05 AM", "College", true, 0.94f),
        TimelineEvent("11:20 AM", "Classroom 204", true, 0.94f, isLast = true),
        TimelineEvent("01:15 PM", "Canteen", false, 0f)
    ))
    else -> Triple("Water Bottle", "bottle", bottleTimeline)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryReplayScreen(
    itemId: Long,
    onNavigateBack: () -> Unit
) {
    val (itemName, itemType, timeline) = getTimeline(itemId)
    val lastConfirmed = timeline.lastOrNull { it.detected }

    // Animate items appearing one by one
    var visibleCount by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        timeline.forEachIndexed { index, _ ->
            delay(250L * index)
            visibleCount = index + 1
        }
    }

    Scaffold(
        containerColor = BackgroundDeep,
        topBar = {
            SmartReturnTopBar(
                title = itemName,
                subtitle = "Memory Replay",
                onBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Object hero card
            item {
                ItemHeroCard(
                    name = itemName,
                    type = itemType,
                    eventsCount = timeline.count { it.detected },
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                SectionHeader(
                    title = "TIMELINE",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Timeline events
            itemsIndexed(timeline) { index, event ->
                val visible = index < visibleCount
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 }
                ) {
                    TimelineEventRow(
                        event = event,
                        isFirst = index == 0,
                        isLastItem = index == timeline.lastIndex,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Last confirmed summary card
            if (lastConfirmed != null) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    LastConfirmedCard(
                        event = lastConfirmed,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Closing message
            item {
                Spacer(modifier = Modifier.height(24.dp))
                ClosingMessageCard(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun ItemHeroCard(name: String, type: String, eventsCount: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = BackgroundSurface,
        border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(
                    colors = listOf(CyanPrimary.copy(alpha = 0.06f), BackgroundSurface)
                ))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape)
                    .background(CyanPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = itemEmoji(type), fontSize = 32.sp)
            }
            Column {
                Text(text = name, style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(text = "$eventsCount confirmed observations", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(shape = RoundedCornerShape(6.dp), color = CyanPrimary.copy(alpha = 0.12f)) {
                    Text(text = "Memory Replay", style = MaterialTheme.typography.labelSmall, color = CyanPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            }
        }
    }
}

@Composable
private fun TimelineEventRow(
    event: TimelineEvent,
    isFirst: Boolean,
    isLastItem: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        // Timeline line + node
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            // Top connector line
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(12.dp)
                        .background(if (event.detected) TimelineConnector else TimelineConnector.copy(alpha = 0.3f))
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }
            // Node circle
            Box(
                modifier = Modifier
                    .size(if (event.isLast) 20.dp else 16.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            event.isLast   -> CyanPrimary
                            event.detected -> TimelineNode.copy(alpha = 0.7f)
                            else           -> TimelineConnector.copy(alpha = 0.4f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (event.isLast) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(BackgroundDeep))
                }
            }
            // Bottom connector line
            if (!isLastItem) {
                Box(
                    modifier = Modifier.width(2.dp).height(12.dp)
                        .background(if (event.detected) TimelineConnector else TimelineConnector.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Event card
        Surface(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            shape = RoundedCornerShape(14.dp),
            color = if (event.detected) BackgroundSurface else BackgroundSurface.copy(alpha = 0.5f),
            border = BorderStroke(
                1.dp,
                when {
                    event.isLast   -> CyanPrimary.copy(alpha = 0.5f)
                    event.detected -> OutlineVariant
                    else           -> OutlineVariant.copy(alpha = 0.4f)
                }
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = locationEmoji(event.location), fontSize = 20.sp,
                    modifier = Modifier.alpha(if (event.detected) 1f else 0.4f))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (event.detected) TextPrimary else TextTertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (event.detected) "Object detected" else "Not detected",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (event.detected) TextSecondary else TextTertiary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = event.time, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                    if (event.detected) {
                        Spacer(modifier = Modifier.height(4.dp))
                        ConfidenceBadge(confidence = event.confidence)
                    }
                }
            }
        }
    }
}

@Composable
private fun LastConfirmedCard(event: TimelineEvent, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CyanPrimary.copy(alpha = 0.07f),
        border = BorderStroke(1.5.dp, CyanPrimary.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "LAST CONFIRMED",
                style = MaterialTheme.typography.labelMedium,
                color = CyanPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                InfoChip(icon = "📍", label = event.location)
                InfoChip(icon = "🕐", label = event.time)
                InfoChip(icon = "🎯", label = "${(event.confidence * 100).toInt()}% conf.")
            }
        }
    }
}

@Composable
private fun InfoChip(icon: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = icon, fontSize = 13.sp)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = TextPrimary)
    }
}

@Composable
private fun ClosingMessageCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BackgroundSurface,
        border = BorderStroke(1.dp, VioletSecondary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "◉", fontSize = 24.sp, color = CyanPrimary)
            Text(
                text = "SmartReturn AI doesn't just track a device.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "It helps you remember your physical world.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}