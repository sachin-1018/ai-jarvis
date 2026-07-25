package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.device.PhoneControlManager
import com.example.data.device.PhoneControlResult
import com.example.ui.theme.*

@Composable
fun PhoneControlScreen(
    phoneControlManager: PhoneControlManager,
    onToggleOverlay: () -> Unit = {}
) {
    var commandInput by remember { mutableStateOf("") }
    var lastActionResult by remember { mutableStateOf<PhoneControlResult?>(null) }
    var isTorchOn by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "📱 PHONE CONTROL & ASSISTANT HUB",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = CyanPrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = "Control device features via voice or tap shortcuts (Calling, Apps, Flashlight, Volume, Settings)",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        // System Permissions & Accessibility Floating Overlay Banner (Matches User Image)
        com.example.ui.components.SystemPermissionsCard(
            onToggleOverlay = onToggleOverlay
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Custom Command Executor
        Surface(
            color = DarkGlassSurface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Execute Custom Command (Hindi / English)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextCyanGlow
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commandInput,
                        onValueChange = { commandInput = it },
                        placeholder = { Text("e.g. 'Call 9876543210' or 'Open Camera'", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = DarkGlassBorder,
                            focusedTextColor = TextCyanGlow,
                            unfocusedTextColor = TextCyanGlow
                        )
                    )
                    Button(
                        onClick = {
                            if (commandInput.isNotBlank()) {
                                lastActionResult = phoneControlManager.executeCommand(commandInput)
                                commandInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                    ) {
                        Text("EXECUTE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                if (lastActionResult != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Result: ${lastActionResult?.feedbackMessage}",
                        fontSize = 12.sp,
                        color = if (lastActionResult?.isSuccess == true) TerminalGreen else CriticalRed,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Grid Controls
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ControlCard(
                    title = "Phone Dialer",
                    subtitle = "Make Phone Call",
                    icon = Icons.Default.Call,
                    accentColor = TerminalGreen,
                    onClick = {
                        lastActionResult = phoneControlManager.executeCommand("call phone")
                    }
                )
            }

            item {
                ControlCard(
                    title = "SMS Composer",
                    subtitle = "Send Message",
                    icon = Icons.Default.Sms,
                    accentColor = NeonBlueSecondary,
                    onClick = {
                        lastActionResult = phoneControlManager.executeCommand("message 9876543210")
                    }
                )
            }

            item {
                ControlCard(
                    title = if (isTorchOn) "Torch ON" else "Torch OFF",
                    subtitle = "Toggle Flashlight",
                    icon = Icons.Default.FlashOn,
                    accentColor = HologramAmber,
                    onClick = {
                        isTorchOn = !isTorchOn
                        lastActionResult = phoneControlManager.executeCommand(if (isTorchOn) "flashlight on" else "flashlight off")
                    }
                )
            }

            item {
                ControlCard(
                    title = "Volume Control",
                    subtitle = "Set 70% Volume",
                    icon = Icons.Default.VolumeUp,
                    accentColor = CyanPrimary,
                    onClick = {
                        lastActionResult = phoneControlManager.executeCommand("volume 70%")
                    }
                )
            }

            item {
                ControlCard(
                    title = "Battery Status",
                    subtitle = "Check Battery Level",
                    icon = Icons.Default.BatteryChargingFull,
                    accentColor = TerminalGreen,
                    onClick = {
                        lastActionResult = phoneControlManager.executeCommand("battery level")
                    }
                )
            }

            item {
                ControlCard(
                    title = "Camera App",
                    subtitle = "Open Camera",
                    icon = Icons.Default.PhotoCamera,
                    accentColor = CyanPrimary,
                    onClick = {
                        lastActionResult = phoneControlManager.executeCommand("open camera")
                    }
                )
            }

            item {
                ControlCard(
                    title = "YouTube",
                    subtitle = "Launch YouTube",
                    icon = Icons.Default.PlayArrow,
                    accentColor = CriticalRed,
                    onClick = {
                        lastActionResult = phoneControlManager.executeCommand("open youtube")
                    }
                )
            }

            item {
                ControlCard(
                    title = "WhatsApp",
                    subtitle = "Launch WhatsApp",
                    icon = Icons.Default.Chat,
                    accentColor = TerminalGreen,
                    onClick = {
                        lastActionResult = phoneControlManager.executeCommand("open whatsapp")
                    }
                )
            }

            item {
                ControlCard(
                    title = "Inspect Screen",
                    subtitle = "Read Chrome / App Text",
                    icon = Icons.Default.FindInPage,
                    accentColor = HologramAmber,
                    onClick = {
                        val clip = phoneControlManager.openApp("com.android.chrome")
                        lastActionResult = PhoneControlResult(true, "Opening Chrome & Screen Inspector Active! Copy text in Chrome or take a screenshot to analyze with Jarvis.", "CHROME_INSPECT")
                    }
                )
            }

            item {
                ControlCard(
                    title = "Screenshot Scan",
                    subtitle = "Read Outside App Data",
                    icon = Icons.Default.AddAPhoto,
                    accentColor = CyanPrimary,
                    onClick = {
                        lastActionResult = PhoneControlResult(true, "Screenshot Scanner Active! Go to Vision Tab or Chat Tab to pick & scan any screenshot from Gallery.", "SCREENSHOT_SCAN")
                    }
                )
            }

            item {
                ControlCard(
                    title = "Chrome",
                    subtitle = "Open Browser",
                    icon = Icons.Default.Language,
                    accentColor = NeonBlueSecondary,
                    onClick = {
                        lastActionResult = phoneControlManager.executeCommand("open chrome")
                    }
                )
            }

            item {
                ControlCard(
                    title = "Settings",
                    subtitle = "System Preferences",
                    icon = Icons.Default.Settings,
                    accentColor = TextMuted,
                    onClick = {
                        lastActionResult = phoneControlManager.executeCommand("open settings")
                    }
                )
            }
        }
    }
}

@Composable
fun ControlCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = DarkGlassSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = accentColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextCyanGlow
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}
