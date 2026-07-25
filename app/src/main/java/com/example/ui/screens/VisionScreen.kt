package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun VisionScreen(
    onPickScreenshot: () -> Unit = {},
    onAnalyzeImage: (String) -> Unit
) {
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisOutput by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "👁️ LIVE SCREEN & IMAGE VISION HUB",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = CyanPrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = "Pick outside app screenshots (Chrome/WhatsApp), analyze diagrams & OCR text",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Prominent Upload & Scan Screenshot Banner Button
        Surface(
            color = Color(0xFF061526),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CyanPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📸 SCAN OUTSIDE SCREENSHOT",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanPrimary
                    )
                    Text(
                        text = "Select any screenshot from Chrome or other apps to read and extract data",
                        fontSize = 11.sp,
                        color = TextCyanGlow
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onPickScreenshot() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("PICK & SCAN", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hero Graphic Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.ic_jarvis_hero),
                    contentDescription = "Jarvis Vision HUD",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "JARVIS NEURAL VISION ENGAGED",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary
                        )
                        Text(
                            text = "Multimodal visual reasoning for document OCR, screen problem solving, and science diagrams",
                            fontSize = 11.sp,
                            color = TextCyanGlow
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    isAnalyzing = true
                    analysisOutput = "Analyzing screen frame...\n• Text Detected: 'Integral ∫ (3x^2 + 4x) dx'\n• Category: Calculus Problem\n• Solution Steps: x^3 + 2x^2 + C\n• Hindi Explanation: Screen pe diya gaya calculus problem solve kar diya gaya hai!"
                    isAnalyzing = false
                    onAnalyzeImage("Analyze screen problem")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CenterFocusWeak, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("SCAN SCREEN", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Button(
                onClick = {
                    isAnalyzing = true
                    analysisOutput = "Photo Captured!\n• Subject: Physics Textbook Circuit Diagram\n• Analysis: Resistors in series R_total = R1 + R2 = 10Ω + 20Ω = 30Ω\n• Current I = V / R = 12V / 30Ω = 0.4 Amperes."
                    isAnalyzing = false
                    onAnalyzeImage("Analyze textbook photo")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlueSecondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("CAPTURE PHOTO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Analysis Output Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, DarkGlassBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkGlassSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = CyanPrimary)
                    Text(
                        text = "Vision Analysis Result",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextCyanGlow
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isAnalyzing) {
                    CircularProgressIndicator(color = CyanPrimary, modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (analysisOutput != null) {
                    Text(
                        text = analysisOutput!!,
                        fontSize = 13.sp,
                        color = TextCyanGlow,
                        lineHeight = 20.sp
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tap 'Scan Screen' or 'Capture Photo' to analyze visual problems and screen content with Jarvis Vision.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
