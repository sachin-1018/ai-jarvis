package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun DeveloperScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Profile Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(CyanPrimary.copy(alpha = 0.2f))
                .border(2.dp, CyanPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_jarvis_core),
                contentDescription = "Sachin Solunke Jarvis",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
        }

        Text(
            text = "AI JARVIS PERSONAL ASSISTANT",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CyanPrimary,
            letterSpacing = 1.5.sp
        )

        Surface(
            color = TerminalGreen.copy(alpha = 0.2f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "⚡ FULL ACTIVE MODE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TerminalGreen,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        // Developer Info Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkGlassSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = CyanPrimary)
                    Column {
                        Text("DEVELOPER", fontSize = 10.sp, color = TextMuted)
                        Text("Sachin Solunke", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextCyanGlow)
                    }
                }

                Divider(color = DarkGlassBorder)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = NeonBlueSecondary)
                    Column {
                        Text("CONTACT EMAIL", fontSize = 10.sp, color = TextMuted)
                        Text("woldcom87@gmail.com", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextCyanGlow)
                    }
                }

                Divider(color = DarkGlassBorder)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = HologramAmber)
                    Column {
                        Text("LANGUAGES SUPPORTED", fontSize = 10.sp, color = TextMuted)
                        Text("Hindi & English (Bilingual Voice & Text)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextCyanGlow)
                    }
                }
            }
        }

        // Capabilities Summary Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkGlassSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkGlassBorder, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = TerminalGreen)
                    Text("SYSTEM CAPABILITIES", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextCyanGlow)
                }

                Text("• Online AI Models: Google Gemini, Anthropic Claude, xAI Grok, DeepSeek, OpenRouter, Cohere, Mistral AI, Groq, Together AI, OpenCode Zen.", fontSize = 12.sp, color = TextMuted)
                Text("• Phone Controls: Calling, SMS, App Shortcuts, Flashlight, Volume, Settings, Battery.", fontSize = 12.sp, color = TextMuted)
                Text("• Offline Engine: On-device Calculus, Algebra, Physics Constants, Self-Learning Memory DB.", fontSize = 12.sp, color = TextMuted)
                Text("• Overlay Assistant: Floating Jarvis service outside app.", fontSize = 12.sp, color = TextMuted)
            }
        }
    }
}
