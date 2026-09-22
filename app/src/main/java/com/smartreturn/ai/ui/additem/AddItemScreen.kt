package com.smartreturn.ai.ui.additem

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
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.delay
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartreturn.ai.ui.components.*
import com.smartreturn.ai.ui.theme.*

private data class ItemTemplate(
    val type: String,
    val name: String,
    val emoji: String,
    val description: String
)

private val supportedItems = listOf(
    ItemTemplate("backpack", "Backpack", "🎒", "School or travel backpack"),
    ItemTemplate("laptop", "Laptop", "💻", "Laptop or tablet"),
    ItemTemplate("bottle", "Water Bottle", "🥤", "Reusable water bottle")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(onNavigateBack: () -> Unit) {
    var selectedItem by remember { mutableStateOf<ItemTemplate?>(null) }
    var itemName by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(1200)
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = BackgroundDeep,
        topBar = {
            SmartReturnTopBar(
                title = "Add Belonging",
                subtitle = "Register an item to track",
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
            // Instructions
            Text(
                text = "Select the item you want SmartReturn AI to remember for you.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            // Item selector
            SectionHeader(title = "CHOOSE ITEM TYPE")

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                supportedItems.forEach { template ->
                    val isSelected = selectedItem?.type == template.type
                    Surface(
                        onClick = {
                            selectedItem = template
                            itemName = template.name
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) CyanPrimary.copy(alpha = 0.1f) else BackgroundSurface,
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) CyanPrimary else OutlineVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Emoji in circle
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) CyanPrimary.copy(alpha = 0.2f)
                                        else SurfaceContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = template.emoji, fontSize = 26.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = template.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isSelected) CyanPrimary else TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = template.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "Not selected",
                                    tint = TextTertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Custom name input
            AnimatedVisibility(visible = selectedItem != null) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader(title = "CUSTOM NAME (OPTIONAL)")
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        placeholder = {
                            Text(
                                text = selectedItem?.name ?: "",
                                color = TextTertiary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = OutlineVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = CyanPrimary,
                            focusedContainerColor = BackgroundSurface,
                            unfocusedContainerColor = BackgroundSurface
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Privacy note
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceContainer,
                border = BorderStroke(1.dp, OutlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "SmartReturn only stores object metadata — no continuous video.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            // Save button
            Button(
                onClick = {
                    if (selectedItem != null) {
                        showSuccess = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = selectedItem != null && !showSuccess,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = CyanOnPrimary,
                    disabledContainerColor = SurfaceContainer,
                    disabledContentColor = TextTertiary
                )
            ) {
                if (showSuccess) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Saved!",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Register Belonging",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
