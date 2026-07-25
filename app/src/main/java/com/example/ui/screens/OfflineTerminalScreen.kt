package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.OfflineMathScienceEngine
import com.example.data.local.entity.OfflineMemoryEntity
import com.example.data.local.entity.TerminalLogEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OfflineTerminalScreen(
    terminalLogs: List<TerminalLogEntity>,
    offlineMemories: List<OfflineMemoryEntity>,
    localLibraries: List<com.example.data.local.entity.LocalLibraryEntity> = emptyList(),
    onAddMemory: (String, String, String) -> Unit,
    onDeleteMemory: (Long) -> Unit,
    onToggleLibrary: (Long, Boolean) -> Unit = { _, _ -> },
    onAddLibrary: (String, String, Double, String, String) -> Unit = { _, _, _, _, _ -> }
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Terminal Board, 1: Math/Science, 2: Local Libraries, 3: Self-Learning DB

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "📟 OFFLINE AI ENGINE & TERMINAL BOARD",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = CyanPrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = "On-device reasoning, calculation steps, local libraries, and offline memory",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkGlassSurface,
            contentColor = CyanPrimary,
            edgePadding = 0.dp
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("TERMINAL LOGS", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("MATH & SCIENCE", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("LOCAL LIBRARIES", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("SELF-LEARNING DB", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> TerminalBoardSection(logs = terminalLogs)
            1 -> MathScienceSolverSection()
            2 -> LocalLibrariesSection(libraries = localLibraries, onToggleLibrary = onToggleLibrary, onAddLibrary = onAddLibrary)
            3 -> SelfLearningDbSection(memories = offlineMemories, onAddMemory = onAddMemory, onDeleteMemory = onDeleteMemory)
        }
    }
}

@Composable
fun TerminalBoardSection(logs: List<TerminalLogEntity>) {
    Surface(
        color = Color.Black,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, TerminalGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Terminal, contentDescription = null, tint = TerminalGreen)
                Text(
                    text = "JARVIS TERMINAL CONSOLE",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TerminalGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(logs) { log ->
                    val color = when (log.logType) {
                        "ERROR" -> CriticalRed
                        "THINKING" -> HologramAmber
                        "EXECUTION" -> CyanPrimary
                        "LEARNING" -> TerminalGreen
                        else -> TextCyanGlow
                    }

                    Text(
                        text = "[${log.logType}] ${log.message}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = color,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MathScienceSolverSection() {
    var mathInput by remember { mutableStateOf("differentiate x^3 + 2x") }
    var mathResult by remember { mutableStateOf(OfflineMathScienceEngine.solve("differentiate x^3 + 2x")) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = mathInput,
            onValueChange = { mathInput = it },
            label = { Text("Math/Physics/Chemistry Problem", fontSize = 12.sp, color = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkGlassSurface,
                unfocusedContainerColor = DarkGlassSurface,
                focusedBorderColor = CyanPrimary,
                unfocusedBorderColor = DarkGlassBorder,
                focusedTextColor = TextCyanGlow,
                unfocusedTextColor = TextCyanGlow
            )
        )

        Button(
            onClick = {
                mathResult = OfflineMathScienceEngine.solve(mathInput)
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Functions, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(6.dp))
            Text("SOLVE OFFLINE", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Surface(
            color = DarkGlassSurface,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "SOLUTION ANSWER:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanPrimary
                )
                Text(
                    text = mathResult.answer,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextCyanGlow
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "REASONING STEPS:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HologramAmber
                )
                mathResult.steps.forEach { step ->
                    Text(
                        text = step,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "HINDI/ENGLISH EXPLANATION:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TerminalGreen
                )
                Text(
                    text = mathResult.explanationHindiEnglish,
                    fontSize = 12.sp,
                    color = TextCyanGlow
                )
            }
        }
    }
}

@Composable
fun SelfLearningDbSection(
    memories: List<OfflineMemoryEntity>,
    onAddMemory: (String, String, String) -> Unit,
    onDeleteMemory: (Long) -> Unit
) {
    var topicInput by remember { mutableStateOf("") }
    var factInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("USER_TRAINING") }

    var selectedFormatTab by remember { mutableStateOf("Custom Text") }
    val formatTabs = listOf("Custom Text", "PDF Ingestion", "Photo Knowledge", "Voice Training")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 50 GB Memory Reservoir Status Banner
        Surface(
            color = Color(0xFF081C26),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💾 50 GB NEURAL MEMORY RESERVOIR",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "CAPACITY: HIGH (50.0 GB)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TerminalGreen,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Offline Jarvis continuous learning engine. Ingest PDFs, Photos, Text, & Voice knowledge to make Jarvis smarter offline.",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        // Training Input Format Selector (Text, PDF, Photo, Voice)
        ScrollableTabRow(
            selectedTabIndex = formatTabs.indexOf(selectedFormatTab).coerceAtLeast(0),
            containerColor = DarkGlassSurface,
            contentColor = CyanPrimary,
            edgePadding = 0.dp
        ) {
            formatTabs.forEach { tabName ->
                Tab(
                    selected = selectedFormatTab == tabName,
                    onClick = { selectedFormatTab = tabName },
                    text = { Text(tabName, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkGlassSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                when (selectedFormatTab) {
                    "Custom Text" -> {
                        Text("📝 Manual Text / Rule Training", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextCyanGlow)
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = topicInput,
                            onValueChange = { topicInput = it },
                            placeholder = { Text("Topic (e.g. 'Satta Jodi Formula', 'Physics Equation')", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = factInput,
                            onValueChange = { factInput = it },
                            placeholder = { Text("Fact / Rule (e.g. '(Jodi1 × Jodi2) ÷ Divisor = Open/Close')", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (topicInput.isNotBlank() && factInput.isNotBlank()) {
                                    onAddMemory(topicInput, factInput, "TEXT_DATASET")
                                    topicInput = ""
                                    factInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TerminalGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SAVE TO OFFLINE MEMORY", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    "PDF Ingestion" -> {
                        Text("📄 PDF Document Data Extractor & Ingestion", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HologramAmber)
                        Text("Extract text, charts, formulas & knowledge from PDF books and manuals directly into memory.", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onAddMemory("PDF Ingested Manual", "Extracted table formulas & charts from uploaded PDF document", "PDF_DOCUMENT")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HologramAmber),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("📄 Ingest Sample PDF", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }

                            Button(
                                onClick = {
                                    onAddMemory("Satta Matka Master Book PDF", "Complete Jodi, Pannel, Cut Ank, and OTC prediction handbook", "PDF_DOCUMENT")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("📘 Train Satta PDF", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                    }

                    "Photo Knowledge" -> {
                        Text("📷 Photo & Diagram Visual OCR Ingestion", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                        Text("Upload camera photos, chart screenshots, or diagrams to index text & mathematical structures.", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                onAddMemory("Photo OCR Chart Data", "Scanned chart image containing weekly jodi records & open panels", "PHOTO_KNOWLEDGE")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("📸 Ingest Photo OCR Data", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    "Voice Training" -> {
                        Text("🎙️ Voice Speech Interactive Training", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TerminalGreen)
                        Text("Speak rules, answers, or preferences in Hindi/English. Jarvis listens, speaks back, and remembers.", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                onAddMemory("Voice Command Rule", "Spoken voice instruction: 'Mera naam Sachin Sir hai aur hamesha Satta Open OTC report auto-calculate karo'", "VOICE_TRAINING")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TerminalGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🎙️ Add Voice Memory Entry", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Text("Indexed Self-Learned Memories (${memories.size}):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextCyanGlow)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(memories) { mem ->
                Surface(
                    color = DarkGlassSurface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkGlassBorder, RoundedCornerShape(10.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(mem.topic, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                            Text(mem.factOrRule, fontSize = 12.sp, color = TextCyanGlow)
                            Text("Category: ${mem.category}", fontSize = 10.sp, color = TextMuted)
                        }

                        IconButton(onClick = { onDeleteMemory(mem.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CriticalRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocalLibrariesSection(
    libraries: List<com.example.data.local.entity.LocalLibraryEntity>,
    onToggleLibrary: (Long, Boolean) -> Unit,
    onAddLibrary: (String, String, Double, String, String) -> Unit = { _, _, _, _, _ -> }
) {
    val coroutineScope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }

    // State map to track active downloads progress for each library ID
    val downloadingProgressMap = remember { mutableStateMapOf<Long, Float>() }

    // Dialog state
    var newLibName by remember { mutableStateOf("") }
    var newLibCategory by remember { mutableStateOf("AI_MODEL") }
    var newLibSize by remember { mutableStateOf("25.0") }
    var newLibDesc by remember { mutableStateOf("") }
    var newLibUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkGlassSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📥 LOCAL OFFLINE LIBRARIES & MODEL DATASETS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = HologramAmber
                    )
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Custom", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Download & manage local offline datasets, NLP phonetics, chart pattern engines, and weights for offline usage without internet.",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Text(
            text = "Installed & Available Local Libraries (${libraries.size}):",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextCyanGlow
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(libraries, key = { it.id }) { lib ->
                val progress = downloadingProgressMap[lib.id]

                Surface(
                    color = DarkGlassSurface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (lib.isDownloaded) TerminalGreen.copy(alpha = 0.6f) else DarkGlassBorder,
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lib.libraryName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanPrimary
                                )
                                Text(
                                    text = lib.description,
                                    fontSize = 11.sp,
                                    color = TextCyanGlow
                                )
                                Text(
                                    text = "Category: ${lib.category} | Size: ${lib.sizeMb} MB",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    if (lib.isDownloaded) {
                                        onToggleLibrary(lib.id, true)
                                    } else {
                                        // Simulate download progress 0% -> 100%
                                        coroutineScope.launch {
                                            downloadingProgressMap[lib.id] = 0.1f
                                            for (p in 2..10) {
                                                kotlinx.coroutines.delay(120)
                                                downloadingProgressMap[lib.id] = p / 10f
                                            }
                                            downloadingProgressMap.remove(lib.id)
                                            onToggleLibrary(lib.id, false)
                                        }
                                    }
                                },
                                enabled = progress == null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (lib.isDownloaded) TerminalGreen else CyanPrimary
                                )
                            ) {
                                Text(
                                    text = when {
                                        progress != null -> "DOWNLOADING ${(progress * 100).toInt()}%"
                                        lib.isDownloaded -> "INSTALLED ✅"
                                        else -> "DOWNLOAD 📥"
                                    },
                                    fontSize = 10.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (progress != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp),
                                color = CyanPrimary,
                                trackColor = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Custom Local Library / Model URL", color = CyanPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newLibName,
                        onValueChange = { newLibName = it },
                        label = { Text("Library Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newLibCategory,
                        onValueChange = { newLibCategory = it },
                        label = { Text("Category (AI_MODEL, MATH, NLP, PATTERN)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newLibSize,
                        onValueChange = { newLibSize = it },
                        label = { Text("Size (MB)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newLibDesc,
                        onValueChange = { newLibDesc = it },
                        label = { Text("Description") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newLibUrl,
                        onValueChange = { newLibUrl = it },
                        label = { Text("Download URL or Local Path") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newLibName.isNotBlank()) {
                            onAddLibrary(
                                newLibName,
                                newLibCategory,
                                newLibSize.toDoubleOrNull() ?: 15.0,
                                newLibDesc.ifBlank { "Custom offline dataset" },
                                newLibUrl
                            )
                            newLibName = ""
                            newLibDesc = ""
                            newLibUrl = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Text("SAVE & DOWNLOAD", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("CANCEL", color = Color.Gray)
                }
            },
            containerColor = DarkGlassSurface
        )
    }
}
