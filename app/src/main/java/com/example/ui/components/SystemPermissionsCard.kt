package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkGlassSurface
import com.example.ui.theme.TextCyanGlow

@Composable
fun SystemPermissionsCard(
    onToggleOverlay: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = CyanPrimary,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGlassSurface)
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF070E17))
                .padding(20.dp)
        ) {
            Text(
                text = "SYSTEM PERMISSIONS & ACCESSIBILITY",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CyanPrimary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Enable required Android permissions for full device control & floating popup access.",
                fontSize = 12.sp,
                color = Color(0xFFB0BEC5),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ACCESSIBILITY Button
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            context.startActivity(intent)
                            Toast.makeText(context, "Opening Accessibility Settings...", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Accessibility settings unavailable", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    border = ButtonDefaults.outlinedToolboxBorder(color = CyanPrimary, width = 1.2.dp)
                ) {
                    Text(
                        text = "ACCESSIBILITY",
                        color = TextCyanGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // OVERLAY POPUP Button
                OutlinedButton(
                    onClick = {
                        try {
                            if (!Settings.canDrawOverlays(context)) {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                                Toast.makeText(context, "Grant Floating Overlay Permission", Toast.LENGTH_SHORT).show()
                            } else {
                                onToggleOverlay()
                                Toast.makeText(context, "Floating Overlay Popup Active on Screen!", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            onToggleOverlay()
                            Toast.makeText(context, "Overlay Service Toggled", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    border = ButtonDefaults.outlinedToolboxBorder(color = Color(0xFF8E24AA), width = 1.2.dp)
                ) {
                    Text(
                        text = "OVERLAY POPUP",
                        color = Color(0xFFCE93D8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Helper extension for custom outline border color
@Composable
private fun ButtonDefaults.outlinedToolboxBorder(color: Color, width: androidx.compose.ui.unit.Dp) =
    androidx.compose.foundation.BorderStroke(width, color)
