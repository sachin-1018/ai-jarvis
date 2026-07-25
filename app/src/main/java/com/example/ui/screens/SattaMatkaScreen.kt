package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.ChartPatternEngine
import com.example.data.engine.DayReportStatus
import com.example.data.engine.PassFailScanResult
import com.example.data.engine.UltimatePredictorReport
import com.example.data.local.entity.ChartPatternEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.JarvisViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SattaMatkaScreen(
    viewModel: JarvisViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val chartPatterns by viewModel.chartPatterns.collectAsState()

    // Sub-Navigation Tabs inside Satta
    var activeSubTab by remember { mutableStateOf("Report Card") }
    val sattaSubTabs = listOf("Report Card", "Formula Tester", "Pass/Fail Scan", "Data & Folders")

    // Folders
    var selectedFolderTab by remember { mutableStateOf("Dada Folder") }
    val folders = listOf("Dada Folder", "Kalyan Chart", "Sridevi", "Main Bazar", "Custom Formula Rules")

    // Input Format Toggle: 1 = Pannel + Jodi + Pannel, 2 = Only Jodi
    var inputFormatMode by remember { mutableStateOf(1) } // 1 or 2

    // Entry Inputs
    var openPanelInput by remember { mutableStateOf("123") }
    var jodiInput by remember { mutableStateOf("45") }
    var closePanelInput by remember { mutableStateOf("678") }

    // Calendar Day & Date States
    val daysList = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var selectedDay by remember { mutableStateOf("Mon") }
    val currentDateStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    var selectedDate by remember { mutableStateOf(currentDateStr) }

    // Formula Calculator States
    var formulaJodi1 by remember { mutableStateOf("45") }
    var formulaJodi2 by remember { mutableStateOf("45") }
    var formulaDivisor by remember { mutableStateOf("1") }
    var customFormulaName by remember { mutableStateOf("Jodi × Jodi Formula") }
    var customFormulaText by remember { mutableStateOf("Jodi × Jodi ÷ 1") }

    // Scan Pass/Fail Result & Ultimate Predictor States
    var scanResult by remember { mutableStateOf<PassFailScanResult?>(null) }
    var ultimateReport by remember { mutableStateOf<UltimatePredictorReport?>(null) }

    // Auto-Refresh on Screen Open & Pattern Update
    LaunchedEffect(chartPatterns, selectedFolderTab) {
        val reportMarket = if (selectedFolderTab.contains("Kalyan", true)) "KALYAN" else "SRIDEVI"
        ultimateReport = ChartPatternEngine.generateUltimatePredictorReport(chartPatterns, reportMarket)
        scanResult = ChartPatternEngine.scanPassFailRecords(chartPatterns)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpaceBackground)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header Banner with Auto-Refresh Indicator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkGlassSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Brush.horizontalGradient(listOf(CyanPrimary, HologramAmber)),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = HologramAmber,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Jarvis AI Satta Engine v30.0",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Auto-Refreshed | Formula Tester & Pass/Fail Reports",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Refresh Button
                        IconButton(onClick = {
                            val reportMarket = if (selectedFolderTab.contains("Kalyan", true)) "KALYAN" else "SRIDEVI"
                            ultimateReport = ChartPatternEngine.generateUltimatePredictorReport(chartPatterns, reportMarket)
                            scanResult = ChartPatternEngine.scanPassFailRecords(chartPatterns)
                            Toast.makeText(context, "🔄 Satta Engine Auto-Refreshed!", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = CyanPrimary)
                        }
                    }
                }
            }
        }

        // Sub-Navigation Bar inside Satta
        item {
            ScrollableTabRow(
                selectedTabIndex = sattaSubTabs.indexOf(activeSubTab).coerceAtLeast(0),
                containerColor = DarkGlassSurface,
                contentColor = CyanPrimary,
                edgePadding = 4.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, CyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            ) {
                sattaSubTabs.forEach { tabName ->
                    Tab(
                        selected = activeSubTab == tabName,
                        onClick = { activeSubTab = tabName },
                        text = {
                            Text(
                                text = tabName,
                                color = if (activeSubTab == tabName) CyanPrimary else TextMuted,
                                fontWeight = if (activeSubTab == tabName) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }
        }

        // --- SUB-TAB 1: ULTIMATE PREDICTOR REPORT CARD (HD Look from Image/Python Script) ---
        if (activeSubTab == "Report Card") {
            item {
                val rep = ultimateReport ?: ChartPatternEngine.generateUltimatePredictorReport(chartPatterns, "SRIDEVI")

                Surface(
                    color = Color(0xFF050508), // Pitch Black Background as in python script
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(3.dp, CyanPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title: JARVIS AI v30.0
                        Text(
                            text = rep.versionTitle,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyanPrimary,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "ULTIMATE PREDICTOR REPORT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Market Info Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF333333), RoundedCornerShape(8.dp))
                                .padding(vertical = 10.dp, horizontal = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "MARKET: ${rep.marketName}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "DATE: ${rep.reportDate}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 7-DAY REPORT CARD
                        Text(
                            text = "7-DAY REPORT CARD",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 7 Status Circles with ✓ or ✕
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            rep.sevenDayReport.forEach { status ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = 2.dp,
                                            color = if (status.isPass) TerminalGreen else CriticalRed,
                                            shape = CircleShape
                                        )
                                        .background(Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (status.isPass) Icons.Default.Check else Icons.Default.Close,
                                        contentDescription = if (status.isPass) "PASS" else "FAIL",
                                        tint = if (status.isPass) TerminalGreen else CriticalRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Main Output Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, CyanPrimary, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "TODAY'S OTC",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace
                                )

                                Text(
                                    text = rep.mainOtc,
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CyanPrimary,
                                    fontFamily = FontFamily.Monospace
                                )

                                Text(
                                    text = "Support: ${rep.supportOtc}",
                                    fontSize = 13.sp,
                                    color = TextMuted,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                Text(
                                    text = "SUPER JODI: ${rep.superJodi}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HologramAmber,
                                    fontFamily = FontFamily.Monospace
                                )

                                Text(
                                    text = "SAFE DAY: ${rep.safeDay}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonBlueSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Footer
                        Text(
                            text = "DESIGNED BY ${rep.designer}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Last Entry: ${rep.lastEntryDate} | Jodi: ${rep.lastEntryJodi}",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // --- SUB-TAB 2: FORMULA TESTER & CUSTOM FORMULA SAVER ---
        if (activeSubTab == "Formula Tester") {
            item {
                Surface(
                    color = DarkGlassSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HologramAmber.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🧮 FORMULA TESTER & CUSTOM RULE SAVER",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = HologramAmber
                        )
                        Text(
                            text = "Azmae koi bhi formula (Jodi1 × Jodi2 ÷ N) or save karke rakhe!",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = formulaJodi1,
                                onValueChange = { formulaJodi1 = it.take(3) },
                                label = { Text("Jodi 1", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = CyanPrimary
                                ),
                                singleLine = true
                            )

                            Text("×", color = HologramAmber, fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterVertically))

                            OutlinedTextField(
                                value = formulaJodi2,
                                onValueChange = { formulaJodi2 = it.take(3) },
                                label = { Text("Jodi 2", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = CyanPrimary
                                ),
                                singleLine = true
                            )

                            Text("÷", color = HologramAmber, fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterVertically))

                            OutlinedTextField(
                                value = formulaDivisor,
                                onValueChange = { formulaDivisor = it },
                                label = { Text("Divisor", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = CyanPrimary
                                ),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val j1 = formulaJodi1.toIntOrNull() ?: 45
                        val j2 = formulaJodi2.toIntOrNull() ?: 45
                        val div = formulaDivisor.toDoubleOrNull() ?: 1.0
                        val calcResult = ChartPatternEngine.calculateJodiFormula(j1, j2, div, customFormulaText)

                        Surface(
                            color = Color(0xFF071B26),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Output Result: ${calcResult.formattedResult}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Front 2 Digits: ${calcResult.frontTwoDigits}", color = TerminalGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Back 2 Digits: ${calcResult.backTwoDigits}", color = CyanPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom Formula Save Section
                        OutlinedTextField(
                            value = customFormulaName,
                            onValueChange = { customFormulaName = it },
                            label = { Text("Formula Name / Rule Title", fontSize = 10.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = HologramAmber
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val formulaRule = "($j1 × $j2) ÷ $div = ${calcResult.formattedResult} [Front: ${calcResult.frontTwoDigits}, Back: ${calcResult.backTwoDigits}]"
                                viewModel.analyzeAndSaveChart(
                                    folderName = "Custom Formula Rules",
                                    title = customFormulaName,
                                    rawData = formulaRule,
                                    customFormula = "Saved Formula Rule"
                                )
                                Toast.makeText(context, "Formula Saved to 'Custom Formula Rules'!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HologramAmber),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("💾 Save Formula Rule to Satta Engine", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // --- SUB-TAB 3: PASS / FAIL RECORD SCANNER ---
        if (activeSubTab == "Pass/Fail Scan") {
            item {
                Surface(
                    color = DarkGlassSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔍 HISTORICAL PASS / FAIL SCAN REPORT",
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TerminalGreen
                            )

                            Button(
                                onClick = {
                                    scanResult = ChartPatternEngine.scanPassFailRecords(chartPatterns)
                                    Toast.makeText(context, "Record Scan Complete!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TerminalGreen),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Re-Scan", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val res = scanResult ?: ChartPatternEngine.scanPassFailRecords(chartPatterns)
                        Text(res.summaryText, color = Color.White, fontSize = 12.sp)

                        if (res.detailedLogs.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Detailed Record Scan Logs:", color = HologramAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            res.detailedLogs.forEach { log ->
                                Text("• $log", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }

        // --- SUB-TAB 4: DATA ENTRY & FOLDERS ---
        if (activeSubTab == "Data & Folders") {
            item {
                // Folder Selection Tabs
                ScrollableTabRow(
                    selectedTabIndex = folders.indexOf(selectedFolderTab).coerceAtLeast(0),
                    containerColor = Color.Transparent,
                    contentColor = CyanPrimary,
                    edgePadding = 0.dp
                ) {
                    folders.forEach { folder ->
                        Tab(
                            selected = selectedFolderTab == folder,
                            onClick = { selectedFolderTab = folder },
                            text = {
                                Text(
                                    text = folder,
                                    color = if (selectedFolderTab == folder) CyanPrimary else Color.Gray,
                                    fontWeight = if (selectedFolderTab == folder) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
            }

            item {
                Surface(
                    color = DarkGlassSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "📅 DATA ENTRY FOR $selectedFolderTab",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Format Selection: Mode (1) vs Mode (2)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = inputFormatMode == 1,
                                onClick = { inputFormatMode = 1 },
                                label = { Text("Panel + Jodi + Panel", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyanPrimary, selectedLabelColor = Color.Black)
                            )

                            FilterChip(
                                selected = inputFormatMode == 2,
                                onClick = { inputFormatMode = 2 },
                                label = { Text("Only Jodi", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = HologramAmber, selectedLabelColor = Color.Black)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calendar Day Selector
                        Text("Select Day / Date:", color = TextMuted, fontSize = 11.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            daysList.forEach { day ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selectedDay == day) CyanPrimary else Color(0xFF132A38))
                                        .clickable { selectedDay = day }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = day,
                                        color = if (selectedDay == day) Color.Black else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = selectedDate,
                            onValueChange = { selectedDate = it },
                            label = { Text("Date (yyyy-MM-dd)", fontSize = 10.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyanPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Data Input Fields based on Format Mode
                        if (inputFormatMode == 1) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = openPanelInput,
                                    onValueChange = { openPanelInput = it.take(3) },
                                    label = { Text("Open Panel", fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = CyanPrimary
                                    ),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = jodiInput,
                                    onValueChange = { jodiInput = it.take(2) },
                                    label = { Text("Jodi", fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = HologramAmber
                                    ),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = closePanelInput,
                                    onValueChange = { closePanelInput = it.take(3) },
                                    label = { Text("Close Panel", fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = CyanPrimary
                                    ),
                                    singleLine = true
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = jodiInput,
                                onValueChange = { jodiInput = it.take(2) },
                                label = { Text("Only Jodi (e.g. 45)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = HologramAmber
                                ),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val dataString = if (inputFormatMode == 1) {
                                    "$openPanelInput - $jodiInput - $closePanelInput"
                                } else {
                                    "Jodi: $jodiInput"
                                }

                                val fullTitle = "[$selectedDay $selectedDate] $dataString"
                                val formulaNote = "Format ($inputFormatMode) | Day: $selectedDay | Formula: Jodi×Jodi"

                                viewModel.analyzeAndSaveChart(
                                    folderName = selectedFolderTab,
                                    title = fullTitle,
                                    rawData = dataString,
                                    customFormula = formulaNote
                                )

                                Toast.makeText(context, "Added to $selectedFolderTab!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HologramAmber),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Record to '$selectedFolderTab'", fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Saved Records in Selected Folder
            item {
                val filteredPatterns = chartPatterns.filter { it.folderName == selectedFolderTab }

                Text(
                    text = "📂 Records in $selectedFolderTab (${filteredPatterns.size})",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            val filteredPatterns = chartPatterns.filter { it.folderName == selectedFolderTab }

            if (filteredPatterns.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No records in '$selectedFolderTab' yet.\nAdd new entries using the input box above!",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(filteredPatterns, key = { it.id }) { pattern ->
                    ChartPatternCard(
                        pattern = pattern,
                        onDelete = { viewModel.deleteChartPattern(pattern.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChartPatternCard(
    pattern: ChartPatternEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGlassSurface)
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, Color(0xFF2C3E50), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pattern.title,
                    color = CyanPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📊 Record Data: ${pattern.dataContent}",
                color = Color.White,
                fontSize = 12.sp
            )

            if (pattern.formulaLogic.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⚙️ Logic: ${pattern.formulaLogic}",
                    color = HologramAmber,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Folder: ${pattern.folderName} | ID: #${pattern.id}",
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}


