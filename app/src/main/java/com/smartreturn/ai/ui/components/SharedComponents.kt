package com.smartreturn.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartreturn.ai.ui.theme.*

// ─── Top App Bar ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartReturnTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundDeep,
            scrolledContainerColor = BackgroundSurface
        )
    )
}

// ─── Confidence Badge ─────────────────────────────────────────────────────────

@Composable
fun ConfidenceBadge(confidence: Float, modifier: Modifier = Modifier) {
    val percentage = (confidence * 100).toInt()
    val (color, label) = when {
        percentage >= 90 -> Pair(ConfidenceHigh, "High")
        percentage >= 70 -> Pair(ConfidenceMedium, "Medium")
        else             -> Pair(ConfidenceLow, "Low")
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.15f),
        contentColor = color
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// ─── Section Header ──────────────────────────────────────────────────────────

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            letterSpacing = 1.sp
        )
        action?.invoke()
    }
}

// ─── AI Pulsing Dot ──────────────────────────────────────────────────────────

@Composable
fun AiStatusDot(
    active: Boolean = true,
    modifier: Modifier = Modifier
) {
    val color = if (active) CyanPrimary else TextTertiary
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

// ─── Gradient Card ────────────────────────────────────────────────────────────

@Composable
fun SmartCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundSurface),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = OutlineVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    } else {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundSurface),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = OutlineVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}

// ─── Empty State ─────────────────────────────────────────────────────────────

@Composable
fun EmptyState(
    emoji: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = emoji, fontSize = 48.sp)
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

// ─── Loading State ────────────────────────────────────────────────────────────

@Composable
fun LoadingState(message: String = "Processing…", modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(color = CyanPrimary, strokeWidth = 2.dp)
        Text(text = message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

// ─── Item emoji helper ────────────────────────────────────────────────────────

fun itemEmoji(type: String): String = when (type.lowercase()) {
    "backpack" -> "🎒"
    "laptop"   -> "💻"
    "bottle", "water bottle" -> "🥤"
    else       -> "📦"
}

fun locationEmoji(location: String): String = when {
    location.contains("home", ignoreCase = true)      -> "🏠"
    location.contains("canteen", ignoreCase = true)   -> "🍴"
    location.contains("library", ignoreCase = true)   -> "📚"
    location.contains("classroom", ignoreCase = true) -> "🚪"
    location.contains("college", ignoreCase = true)   -> "🏫"
    else -> "📍"
}
