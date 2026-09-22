package com.smartreturn.ai.ui.assistant

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.smartreturn.ai.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.smartreturn.ai.ui.theme.*

private data class ChatMessage(
    val id: Int,
    val text: String,
    val isUser: Boolean,
    val replayItemId: Long? = null,
    val confidence: Float? = null
)

// ─── Query parser (Phase 11 will replace with full engine) ───────────────────

private data class QueryResult(
    val response: String,
    val itemId: Long?,
    val confidence: Float?
)

private fun parseQuery(query: String): QueryResult {
    val lower = query.lowercase()
    return when {
        (lower.contains("bottle") || lower.contains("water")) -> QueryResult(
            "Your Water Bottle was last confirmed in Classroom 204 at 11:20 AM with 91% confidence.\n\nIt was not detected at Canteen (01:15 PM), suggesting it may still be in Classroom 204.",
            3L, 0.91f
        )
        lower.contains("laptop") -> QueryResult(
            "Your Laptop was last confirmed in Classroom 204 at 11:20 AM with 94% confidence.",
            2L, 0.94f
        )
        lower.contains("backpack") || lower.contains("bag") -> QueryResult(
            "Your Backpack was last confirmed at your current location (most recent scan) with 96% confidence.",
            1L, 0.96f
        )
        lower.contains("where") || lower.contains("last") || lower.contains("seen") -> QueryResult(
            "I can track: Backpack, Laptop, and Water Bottle. Please ask about a specific item, e.g. \"Where is my Water Bottle?\"",
            null, null
        )
        else -> QueryResult(
            "I didn't quite understand that. Try asking:\n• \"Where is my water bottle?\"\n• \"Where was my laptop last seen?\"\n• \"When was my backpack detected?\"",
            null, null
        )
    }
}

private val suggestedQueries = listOf(
    "Where did I last have my bottle?",
    "Where was my laptop last seen?",
    "Where is my backpack?",
    "When was my bottle last detected?"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskSmartReturnScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReplay: (Long) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf(
        ChatMessage(0, "Hi! I'm SmartReturn AI. Ask me where any of your belongings were last seen.", false)
    )) }
    var isThinking by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    var msgId by remember { mutableStateOf(1) }
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || isThinking) return
        val userMsg = ChatMessage(msgId++, text, true)
        messages = messages + userMsg
        inputText = ""
        isThinking = true

        coroutineScope.launch {
            delay(800) // simulate AI thinking
            val result = parseQuery(text)
            val aiMsg = ChatMessage(msgId++, result.response, false, result.itemId, result.confidence)
            messages = messages + aiMsg
            isThinking = false
        }
    }

    Scaffold(
        containerColor = BackgroundDeep,
        topBar = {
            SmartReturnTopBar(
                title = "Ask SmartReturn",
                subtitle = "AI memory assistant",
                onBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Chat messages
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(
                        message = msg,
                        onViewReplay = { itemId -> onNavigateToReplay(itemId) }
                    )
                }
                if (isThinking) {
                    item {
                        ThinkingIndicator()
                    }
                }
            }

            // Suggested queries (only when empty)
            if (messages.size == 1) {
                SuggestedQueriesRow(
                    queries = suggestedQueries,
                    onSelect = { sendMessage(it) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Input area
            ChatInputBar(
                value = inputText,
                onValueChange = { inputText = it },
                onSend = { sendMessage(inputText) },
                enabled = !isThinking,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding()
            )
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage, onViewReplay: (Long) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            // AI avatar
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape)
                    .background(CyanPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "◉", fontSize = 14.sp, color = CyanPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = if (message.isUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 16.dp
                ),
                color = if (message.isUser) CyanPrimary.copy(alpha = 0.2f) else BackgroundSurface,
                border = BorderStroke(1.dp, if (message.isUser) CyanPrimary.copy(alpha = 0.4f) else OutlineVariant)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Confidence + replay action for AI messages
            if (!message.isUser) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.confidence != null) {
                        ConfidenceBadge(confidence = message.confidence)
                    }
                    if (message.replayItemId != null) {
                        Surface(
                            onClick = { onViewReplay(message.replayItemId) },
                            shape = RoundedCornerShape(8.dp),
                            color = VioletSecondary.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, VioletSecondary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Timeline, null, tint = VioletSecondary, modifier = Modifier.size(12.dp))
                                Text(text = "View Replay", style = MaterialTheme.typography.labelSmall, color = VioletSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThinkingIndicator() {
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(CyanPrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "◉", fontSize = 14.sp, color = CyanPrimary)
        }
        Surface(
            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
            color = BackgroundSurface,
            border = BorderStroke(1.dp, OutlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp, color = CyanPrimary)
                Text(text = "Thinking...", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun SuggestedQueriesRow(
    queries: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "TRY ASKING",
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary,
            letterSpacing = 1.sp
        )
        queries.forEach { q ->
            Surface(
                onClick = { onSelect(q) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = BackgroundSurface,
                border = BorderStroke(1.dp, OutlineVariant)
            ) {
                Text(
                    text = q,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Ask about your belongings...", color = TextTertiary) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyanPrimary,
                unfocusedBorderColor = OutlineVariant,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = CyanPrimary,
                focusedContainerColor = BackgroundSurface,
                unfocusedContainerColor = BackgroundSurface
            ),
            singleLine = true,
            enabled = enabled,
            trailingIcon = if (value.isNotBlank()) {
                { IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Default.Clear, null, tint = TextTertiary, modifier = Modifier.size(16.dp))
                }}
            } else null
        )
        IconButton(
            onClick = onSend,
            enabled = enabled && value.isNotBlank(),
            modifier = Modifier.size(48.dp).clip(CircleShape)
                .background(if (enabled && value.isNotBlank()) CyanPrimary else SurfaceContainer)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = if (enabled && value.isNotBlank()) CyanOnPrimary else TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}