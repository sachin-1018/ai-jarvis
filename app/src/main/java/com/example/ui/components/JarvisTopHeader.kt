package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun JarvisTopHeader(
    isSpeaking: Boolean,
    isListening: Boolean,
    activeProviderName: String,
    isOverlayActive: Boolean,
    onToggleOverlay: () -> Unit,
    isVoiceModeEnabled: Boolean = true,
    onToggleVoiceMode: (Boolean) -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "arc_reactor")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkGlassSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Compact Arc Reactor Icon & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(CyanPrimary.copy(alpha = 0.15f * pulseGlow))
                            .border(1.dp, CyanPrimary.copy(alpha = pulseGlow), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_jarvis_core),
                            contentDescription = "Jarvis Core",
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .rotate(rotationAngle)
                        )
                    }

                    Text(
                        text = "AI JARVIS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextCyanGlow,
                        letterSpacing = 1.sp
                    )
                }

                // Right Side Controls: Voice Mode & Overlay Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Voice Command ON/OFF Mode Button
                    Surface(
                        onClick = { onToggleVoiceMode(!isVoiceModeEnabled) },
                        color = if (isVoiceModeEnabled) TerminalGreen.copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isVoiceModeEnabled) TerminalGreen else Color.Gray
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Mode",
                                tint = if (isVoiceModeEnabled) TerminalGreen else Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isVoiceModeEnabled) "VOICE: ON" else "OFF",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVoiceModeEnabled) TerminalGreen else Color.Gray
                            )
                        }
                    }

                    // Overlay Floating Head Toggle
                    IconButton(
                        onClick = onToggleOverlay,
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                if (isOverlayActive) CyanPrimary.copy(alpha = 0.25f) else DarkGlassBorder,
                                CircleShape
                            )
                            .border(1.dp, if (isOverlayActive) CyanPrimary else DarkGlassBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Toggle Floating Overlay",
                            tint = if (isOverlayActive) CyanPrimary else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Voice Status Banner if Speaking or Listening
            if (isSpeaking || isListening) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = NeonBlueSecondary.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.Mic,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isSpeaking) "Jarvis Speaking..." else "Jarvis Listening...",
                            fontSize = 11.sp,
                            color = TextCyanGlow,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
