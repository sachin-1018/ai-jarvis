package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ChatMessageEntity
import com.example.ui.theme.*

@Composable
fun JarvisAssistantScreen(
    messages: List<ChatMessageEntity>,
    isProcessing: Boolean,
    isSpeaking: Boolean,
    isListening: Boolean,
    onSendMessage: (String, Boolean) -> Unit,
    onStartVoice: () -> Unit,
    onStopVoice: () -> Unit,
    onPickImage: () -> Unit,
    onToggleOverlay: () -> Unit = {}
) {
    var textFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    val inputText = textFieldValue.text
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val samplePrompts = listOf(
        "Differentiate d/dx(x^3 + 2x)",
        "Call Sachin 9876543210",
        "Flashlight chalu karo",
        "Open Camera",
        "Speed of light kitna hota hai?",
        "Explain Quantum Entanglement"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .background(DeepSpaceBackground)
    ) {
        // Chat Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    EmptyJarvisWelcomeCard(
                        onSelectPrompt = { prompt -> textFieldValue = TextFieldValue(prompt) },
                        onPickImage = onPickImage,
                        onToggleOverlay = onToggleOverlay
                    )
                }
            }

            items(messages, key = { it.id }) { msg ->
                ChatMessageBubble(message = msg)
            }

            if (isProcessing) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = CyanPrimary,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Jarvis processing reasoning neural models...",
                            fontSize = 12.sp,
                            color = CyanPrimary
                        )
                    }
                }
            }
        }

        // Streamlined Bottom Bar Input Controls for Maximize Full Screen Chat
        Surface(
            color = DarkGlassSurface,
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Sleek Full-Width Input Field with Inline Action Icons
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    placeholder = { Text("Ask Jarvis...", fontSize = 13.sp, color = TextMuted) },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DeepSpaceBackground,
                        unfocusedContainerColor = DeepSpaceBackground,
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = DarkGlassBorder,
                        focusedTextColor = TextCyanGlow,
                        unfocusedTextColor = TextCyanGlow
                    ),
                    leadingIcon = {
                        IconButton(
                            onClick = onPickImage,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Attach Screen or Photo",
                                tint = CyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clipData = clipboard.primaryClip
                                    if (clipData != null && clipData.itemCount > 0) {
                                        val pasted = clipData.getItemAt(0).text.toString()
                                        val currentText = textFieldValue.text
                                        val newText = if (currentText.isNotBlank()) "$currentText $pasted" else pasted
                                        textFieldValue = TextFieldValue(newText)
                                        Toast.makeText(context, "Pasted! 📋", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Clipboard empty!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste Clipboard",
                                    tint = TextCyanGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = false,
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText, false)
                                textFieldValue = TextFieldValue("")
                                focusManager.clearFocus()
                            }
                        }
                    )
                )

                // Compact Voice Mic Button
                IconButton(
                    onClick = {
                        if (isListening || isSpeaking) {
                            onStopVoice()
                        } else {
                            onStartVoice()
                        }
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            if (isListening || isSpeaking) CriticalRed else NeonBlueSecondary,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isListening || isSpeaking) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Voice Assistant",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Compact Send Text Button
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText, false)
                            textFieldValue = TextFieldValue("")
                            focusManager.clearFocus()
                        }
                    },
                    enabled = inputText.isNotBlank(),
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            if (inputText.isNotBlank()) CyanPrimary else DarkGlassBorder,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) Color.Black else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessageEntity) {
    val isUser = message.sender == "USER"
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isUser) NeonBlueSecondary.copy(alpha = 0.85f) else DarkGlassSurface,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            modifier = Modifier
                .widthIn(max = 320.dp)
                .border(
                    width = 1.dp,
                    color = if (isUser) CyanPrimary.copy(alpha = 0.5f) else DarkGlassBorder,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUser) "YOU" else "JARVIS (${message.providerName})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) TextCyanGlow else CyanPrimary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = message.modelName,
                            fontSize = 9.sp,
                            color = TextMuted
                        )

                        // Quick Copy Text Button on Message Box
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Jarvis Message", message.text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Text copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Text",
                                tint = CyanPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = TextCyanGlow,
                    lineHeight = 20.sp
                )

                // Render Math reasoning steps if present
                if (!message.mathSteps.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = DeepSpaceBackground,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🧠 Reasoned Calculation Steps:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TerminalGreen
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Jarvis Math Steps", message.mathSteps)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Math reasoning steps copied! 📋", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Steps",
                                        tint = TerminalGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = message.mathSteps,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TerminalGreen.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HackerLiveTelemetryBar() {
    var uptimeSeconds by remember { mutableStateOf(128) }
    var cpuLoad by remember { mutableStateOf(18.4f) }
    var memoryUsage by remember { mutableStateOf(3.2f) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            uptimeSeconds++
            cpuLoad = (14..29).random() + (0..9).random() / 10f
            memoryUsage = (31..35).random() / 10f
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_matrix")
    val alphaPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "matrix_pulse"
    )

    val hrs = uptimeSeconds / 3600
    val mins = (uptimeSeconds % 3600) / 60
    val secs = uptimeSeconds % 60
    val timeFormatted = String.format("%02d:%02d:%02d", hrs, mins, secs)

    Surface(
        color = Color(0xFF030A12),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = TerminalGreen.copy(alpha = 0.4f),
                shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(TerminalGreen.copy(alpha = alphaPulse))
                )
                Text(
                    text = "root@jarvis-os:~#",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TerminalGreen
                )
                Text(
                    text = "[CPU: ${cpuLoad}% | RAM: ${memoryUsage}GB]",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyanPrimary
                )
            }

            Text(
                text = "UPTIME: $timeFormatted",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = HologramAmber
            )
        }
    }
}

@Composable
fun EmptyJarvisWelcomeCard(
    onSelectPrompt: (String) -> Unit,
    onPickImage: () -> Unit = {},
    onToggleOverlay: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.padding(6.dp)
    ) {
        // Hacker Live Terminal Welcome Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = CyanPrimary,
                    shape = RoundedCornerShape(16.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = DarkGlassSurface)
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFF050C16))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "[ 01_JARVIS_HACKER_CORE ]",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TerminalGreen
                        )
                    }

                    Surface(
                        color = CyanPrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "SYSTEM READY ⚡",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyanPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "⚡ AI JARVIS HACKER SYSTEM ⚡",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextCyanGlow,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Hello Bhahi 🫂! Full phone control and multi-AI assistant ready. Designed by Sachin Solunke. You can ask queries in Hindi/English, control system settings, inspect Chrome text, solve math/physics formulas, or run local offline neural models.",
                    fontSize = 12.sp,
                    color = Color(0xFFB0BEC5),
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Action Hacker Grid
                Text(
                    text = ">>> QUICK HACKER COMMANDS:",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = HologramAmber
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSelectPrompt("Flashlight chalu karo aur Battery percent batao") },
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, CyanPrimary)
                    ) {
                        Text("⚡ Flashlight + Battery", fontSize = 10.sp, color = CyanPrimary, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onSelectPrompt("Differentiate d/dx(x^3 + 4x^2)") },
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, TerminalGreen)
                    ) {
                        Text("🧠 Solve Math Logic", fontSize = 10.sp, color = TerminalGreen, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onPickImage,
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, HologramAmber)
                    ) {
                        Text("📸 Scan Screenshot / OCR", fontSize = 10.sp, color = HologramAmber, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onSelectPrompt("Call Sachin 9876543210") },
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, CriticalRed)
                    ) {
                        Text("📞 Phone Dialer Command", fontSize = 10.sp, color = CriticalRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Floating Permissions & Floating Overlay Banner Popup
        com.example.ui.components.SystemPermissionsCard(
            onToggleOverlay = onToggleOverlay
        )
    }
}
