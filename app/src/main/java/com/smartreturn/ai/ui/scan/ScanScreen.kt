package com.smartreturn.ai.ui.scan

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
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartreturn.ai.ui.components.*
import com.smartreturn.ai.ui.theme.*

/**
 * ScanScreen — Phase 1 placeholder.
 * Phase 4 will replace the centre area with a live CameraX preview.
 * Phase 5 will overlay TFLite bounding boxes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onNavigateBack: () -> Unit,
    onScanComplete: () -> Unit
) {
    var scanState by remember { mutableStateOf(ScanState.READY) }
    var selectedLocation by remember { mutableStateOf("Classroom 204") }
    var showLocationPicker by remember { mutableStateOf(false) }

    val demoLocations = listOf("Home", "College", "Classroom 204", "Canteen", "Library")

    // Simulate scanning animation
    LaunchedEffect(scanState) {
        if (scanState == ScanState.SCANNING) {
            delay(2000)
            scanState = ScanState.DETECTED
        }
    }

    Scaffold(
        containerColor = BackgroundDeep,
        topBar = {
            SmartReturnTopBar(
                title = "Smart Scan",
                subtitle = "Point camera at your belongings",
                onBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Location selector
            LocationSelector(
                selected = selectedLocation,
                onClick = { showLocationPicker = true }
            )

            // Camera viewfinder area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0A0F1E))
                    .border(1.dp, OutlineColor, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                when (scanState) {
                    ScanState.READY -> ReadyCameraPlaceholder()
                    ScanState.SCANNING -> ScanningAnimation()
                    ScanState.DETECTED -> DetectedOverlay()
                }
            }

            // Detection results (shown after scan)
            if (scanState == ScanState.DETECTED) {
                DetectionResultsCard()
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (scanState != ScanState.DETECTED) {
                    Button(
                        onClick = { scanState = ScanState.SCANNING },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        enabled = scanState == ScanState.READY,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanPrimary,
                            contentColor = CyanOnPrimary
                        )
                    ) {
                        if (scanState == ScanState.SCANNING) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = CyanOnPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Scan Now",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = onScanComplete,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusActive,
                            contentColor = BackgroundDeep
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Save Memory",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { scanState = ScanState.READY },
                        modifier = Modifier.height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, OutlineColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Location picker bottom sheet
        if (showLocationPicker) {
            LocationPickerSheet(
                locations = demoLocations,
                selected = selectedLocation,
                onSelect = {
                    selectedLocation = it
                    showLocationPicker = false
                },
                onDismiss = { showLocationPicker = false }
            )
        }
    }
}

enum class ScanState { READY, SCANNING, DETECTED }

@Composable
private fun ReadyCameraPlaceholder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(SurfaceContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(
            text = "Camera Preview",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )
        Text(
            text = "Camera will activate here in Phase 4.\nPress Scan Now to simulate detection.",
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary,
            textAlign = TextAlign.Center
        )
        // Corner brackets to suggest camera viewfinder
        ScannerCornerBrackets()
    }
}

@Composable
private fun ScannerCornerBrackets() {
    Box(modifier = Modifier.size(160.dp)) {
        val bracketColor = CyanPrimary.copy(alpha = 0.6f)
        val strokeWidth = 3.dp
        val size = 24.dp
        // Top-left
        Box(modifier = Modifier.align(Alignment.TopStart).size(size).border(
            BorderStroke(strokeWidth, bracketColor),
            RoundedCornerShape(topStart = 8.dp)
        ))
        // Top-right
        Box(modifier = Modifier.align(Alignment.TopEnd).size(size).border(
            BorderStroke(strokeWidth, bracketColor),
            RoundedCornerShape(topEnd = 8.dp)
        ))
        // Bottom-left
        Box(modifier = Modifier.align(Alignment.BottomStart).size(size).border(
            BorderStroke(strokeWidth, bracketColor),
            RoundedCornerShape(bottomStart = 8.dp)
        ))
        // Bottom-right
        Box(modifier = Modifier.align(Alignment.BottomEnd).size(size).border(
            BorderStroke(strokeWidth, bracketColor),
            RoundedCornerShape(bottomEnd = 8.dp)
        ))
    }
}

@Composable
private fun ScanningAnimation() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(color = CyanPrimary, strokeWidth = 3.dp, modifier = Modifier.size(56.dp))
        Text(
            text = "Scanning...",
            style = MaterialTheme.typography.titleMedium,
            color = CyanPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Detecting objects with AI",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun DetectedOverlay() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "✅", fontSize = 36.sp)
        Text(
            text = "Objects Detected",
            style = MaterialTheme.typography.titleMedium,
            color = StatusActive,
            fontWeight = FontWeight.Bold
        )
        // Simulated bounding box labels
        listOf("🎒 Backpack 96%", "💻 Laptop 94%", "🥤 Water Bottle 91%").forEach { label ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyanPrimary.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.4f))
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = CyanPrimary
                )
            }
        }
    }
}

@Composable
private fun DetectionResultsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BackgroundSurface,
        border = BorderStroke(1.dp, StatusActive.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Detection Summary",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            listOf(
                Triple("🎒", "Backpack", 0.96f),
                Triple("💻", "Laptop", 0.94f),
                Triple("🥤", "Water Bottle", 0.91f)
            ).forEach { (emoji, name, conf) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = emoji, fontSize = 16.sp)
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                    ConfidenceBadge(confidence = conf)
                }
            }
        }
    }
}

@Composable
private fun LocationSelector(selected: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = BackgroundSurface,
        border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = CyanPrimary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = selected,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Change",
                style = MaterialTheme.typography.labelSmall,
                color = CyanPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationPickerSheet(
    locations: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundSurface,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Select Location",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            locations.forEach { loc ->
                val isSelected = loc == selected
                Surface(
                    onClick = { onSelect(loc) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) CyanPrimary.copy(alpha = 0.1f) else BackgroundElevated,
                    border = if (isSelected) BorderStroke(1.dp, CyanPrimary) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = locationEmoji(loc), fontSize = 18.sp)
                            Text(
                                text = loc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) CyanPrimary else TextPrimary
                            )
                        }
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
