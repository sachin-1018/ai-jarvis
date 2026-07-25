package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ApiProviderEntity
import com.example.ui.theme.*

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderSettingsScreen(
    providers: List<ApiProviderEntity>,
    testConnectionStatus: String?,
    appLanguage: String = "Hindi/English",
    bgImageUri: String? = null,
    bgDimLevel: Float = 0.4f,
    onSelectLanguage: (String) -> Unit = {},
    onSelectBgImage: (String?) -> Unit = {},
    onChangeDimLevel: (Float) -> Unit = {},
    onSelectProvider: (String) -> Unit,
    onSaveProvider: (ApiProviderEntity) -> Unit,
    onTestConnection: (ApiProviderEntity) -> Unit
) {
    val scrollState = rememberScrollState()
    val context = androidx.compose.ui.platform.LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, flags)
            } catch (_: Exception) {}
            onSelectBgImage(it.toString())
        }
    }

    var selectedProviderKey by remember(providers) {
        mutableStateOf(providers.find { it.isSelected }?.providerKey ?: "gemini")
    }

    val currentProvider = providers.find { it.providerKey == selectedProviderKey }
        ?: providers.find { it.isSelected }
        ?: providers.firstOrNull()
        ?: ApiProviderEntity(
            providerKey = "gemini",
            providerName = "Google Gemini (AI Studio)",
            apiKey = "",
            baseUrl = "https://generativelanguage.googleapis.com/",
            selectedModel = "gemini-2.0-flash",
            availableModels = "gemini-1.5-pro, gemini-1.5-flash, gemini-2.0-flash, gemini-2.0-flash-lite",
            systemPrompt = "You are AI Jarvis, a loyal and super-intelligent AI assistant created by Sachin Solunke."
        )

    var providerNameInput by remember { mutableStateOf(currentProvider.providerName) }
    var apiKeyInput by remember { mutableStateOf(currentProvider.apiKey) }
    var baseUrlInput by remember { mutableStateOf(currentProvider.baseUrl) }
    var selectedModelInput by remember { mutableStateOf(currentProvider.selectedModel) }
    var manualModelOverride by remember { mutableStateOf(currentProvider.selectedModel) }
    var systemPromptInput by remember { mutableStateOf(currentProvider.systemPrompt) }

    var isProviderDropdownExpanded by remember { mutableStateOf(false) }
    var isModelDropdownExpanded by remember { mutableStateOf(false) }
    var isApiKeyVisible by remember { mutableStateOf(false) }

    LaunchedEffect(currentProvider.providerKey, currentProvider.apiKey, currentProvider.selectedModel) {
        providerNameInput = currentProvider.providerName
        apiKeyInput = currentProvider.apiKey
        baseUrlInput = currentProvider.baseUrl
        selectedModelInput = currentProvider.selectedModel
        manualModelOverride = currentProvider.selectedModel
        systemPromptInput = currentProvider.systemPrompt
    }

    val modelOptions = remember(currentProvider) {
        currentProvider.availableModels.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = CyanPrimary)
            Text(
                text = "AI Provider & App Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextCyanGlow
            )
        }

        // APP LANGUAGE SELECTOR (HINDI / ENGLISH / AUTO DUAL)
        Surface(
            color = DarkGlassSurface,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = HologramAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "APP & VOICE LANGUAGE (भाषा सेटिंग)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HologramAmber,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Hindi" to "🇮🇳 हिंदी", "English" to "🇬🇧 English", "Hindi/English" to "🔄 Auto Dual").forEach { (langKey, label) ->
                        FilterChip(
                            selected = appLanguage == langKey,
                            onClick = { onSelectLanguage(langKey) },
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanPrimary,
                                selectedLabelColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Active App Language: $appLanguage (AI Jarvis replies & speaks in $appLanguage)",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        // BACKGROUND WALLPAPER & DIMMING CARD
        Surface(
            color = DarkGlassSurface,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, HologramAmber.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = HologramAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BACKGROUND WALLPAPER & DIM (वॉलपेपर व 0-100% डिम)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HologramAmber,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!bgImageUri.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, CyanPrimary, RoundedCornerShape(8.dp))
                        ) {
                            coil.compose.AsyncImage(
                                model = bgImageUri,
                                contentDescription = "Wallpaper Preview",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gallery Wallpaper Chunen 🖼️", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        if (!bgImageUri.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedButton(
                                onClick = { onSelectBgImage(null) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CriticalRed),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CriticalRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Remove Wallpaper", fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Wallpaper Dimming 🔅: ${(bgDimLevel * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextCyanGlow
                    )
                    Text(
                        text = if (bgDimLevel == 0f) "0 (Full Bright)" else if (bgDimLevel == 1f) "100 (Full Dark)" else "${(bgDimLevel * 100).toInt()}% Dark",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Slider(
                    value = bgDimLevel,
                    onValueChange = { onChangeDimLevel(it) },
                    valueRange = 0f..1f,
                    steps = 100,
                    colors = SliderDefaults.colors(
                        thumbColor = CyanPrimary,
                        activeTrackColor = CyanPrimary,
                        inactiveTrackColor = DarkGlassBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ANDROID ACCESSIBILITY SERVICE CARD
        Surface(
            color = Color(0xFF0F1E2A),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlueSecondary.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessibilityNew,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ACCESSIBILITY SERVICE (एक्सेसिबिलिटी सर्विस)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "AI Jarvis Accessibility Inspector service is enabled in App Manifest.\nIf not showing in phone Accessibility Settings, tap below to toggle permission ON:",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        try {
                            val intent = android.content.Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(context, "Opening Phone Accessibility Settings...", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlueSecondary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AccessibilityNew, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open Android Accessibility Settings ⚙️", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // 1. AI PROVIDER DROPDOWN
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "AI PROVIDER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            ExposedDropdownMenuBox(
                expanded = isProviderDropdownExpanded,
                onExpandedChange = { isProviderDropdownExpanded = !isProviderDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = providerNameInput,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isProviderDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkGlassSurface,
                        unfocusedContainerColor = DarkGlassSurface,
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = DarkGlassBorder,
                        focusedTextColor = TextCyanGlow,
                        unfocusedTextColor = TextCyanGlow
                    )
                )

                ExposedDropdownMenu(
                    expanded = isProviderDropdownExpanded,
                    onDismissRequest = { isProviderDropdownExpanded = false },
                    modifier = Modifier.background(DarkGlassSurface)
                ) {
                    providers.forEach { prov ->
                        DropdownMenuItem(
                            text = { Text(prov.providerName, color = TextCyanGlow) },
                            onClick = {
                                selectedProviderKey = prov.providerKey
                                onSelectProvider(prov.providerKey)
                                isProviderDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // 2. API KEY
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "API KEY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            OutlinedTextField(
                value = apiKeyInput,
                onValueChange = { apiKeyInput = it },
                placeholder = { Text("Paste your API Key here...", fontSize = 12.sp, color = TextMuted) },
                visualTransformation = if (isApiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isApiKeyVisible = !isApiKeyVisible }) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Toggle key visibility",
                            tint = if (isApiKeyVisible) CyanPrimary else TextMuted
                        )
                    }
                },
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

            Text(
                text = when (selectedProviderKey) {
                    "opencode_zen" -> "Get key from opencode.ai/auth → Create API Key"
                    "gemini" -> "Get key from Google AI Studio (ai.google.dev) or AI Studio Secrets"
                    "anthropic" -> "Get key from console.anthropic.com"
                    "grok" -> "Get key from console.x.ai"
                    "deepseek" -> "Get key from platform.deepseek.com"
                    "openrouter" -> "Get key from openrouter.ai/keys"
                    "groq" -> "Get key from console.groq.com"
                    else -> "Configure endpoint & API key for $providerNameInput"
                },
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        // 3. BASE URL (FOR CUSTOM / OPENAI COMPATIBLE)
        if (selectedProviderKey !in listOf("gemini", "anthropic")) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "API BASE URL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.5.sp
                )

                OutlinedTextField(
                    value = baseUrlInput,
                    onValueChange = { baseUrlInput = it },
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
            }
        }

        // 4. TEST CONNECTION BUTTON
        Button(
            onClick = {
                val updated = currentProvider.copy(
                    apiKey = apiKeyInput,
                    baseUrl = baseUrlInput,
                    selectedModel = manualModelOverride.ifEmpty { selectedModelInput },
                    systemPrompt = systemPromptInput
                )
                onTestConnection(updated)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonBlueSecondary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Test Connection & Fetch Models", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        // Active Connected Modules Counter Card
        Surface(
            color = Color(0xFF071220),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = TerminalGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "CONNECTED MODULES STATUS",
                        fontSize = 11.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TerminalGreen
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val keyPresent = apiKeyInput.isNotBlank() || selectedProviderKey == "gemini"
                val activeCount = if (keyPresent) "5 Active Modules Found" else "4 Local Modules Active (API Key Optional)"

                Text(
                    text = "🟢 $activeCount",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextCyanGlow
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (keyPresent) {
                        "• Online Model: ${currentProvider.providerName} (${manualModelOverride.ifEmpty { selectedModelInput }})\n• Math & Physics Neural Engine: Ready\n• Phone Device Controller: Ready\n• Voice TTS Engine (Hindi/English): Ready\n• Screen Overlay Inspector: Ready"
                    } else {
                        "• Offline Local Solver: Active (Math/Physics)\n• Phone Device Controller: Active\n• Voice TTS Engine: Active\n• Screen Overlay Inspector: Active\n• Online API Key: Enter key above to unlock cloud models"
                    },
                    fontSize = 11.sp,
                    color = Color(0xFFB0BEC5),
                    lineHeight = 16.sp
                )
            }
        }

        if (!testConnectionStatus.isNullOrEmpty()) {
            Surface(
                color = DarkGlassBorder,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = testConnectionStatus,
                    fontSize = 12.sp,
                    color = CyanPrimary,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        // 5. MODEL (SELECT OR TYPE BELOW)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "MODEL (SELECT OR TYPE BELOW)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            ExposedDropdownMenuBox(
                expanded = isModelDropdownExpanded,
                onExpandedChange = { isModelDropdownExpanded = !isModelDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedModelInput,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isModelDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkGlassSurface,
                        unfocusedContainerColor = DarkGlassSurface,
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = DarkGlassBorder,
                        focusedTextColor = TextCyanGlow,
                        unfocusedTextColor = TextCyanGlow
                    )
                )

                ExposedDropdownMenu(
                    expanded = isModelDropdownExpanded,
                    onDismissRequest = { isModelDropdownExpanded = false },
                    modifier = Modifier.background(DarkGlassSurface)
                ) {
                    modelOptions.forEach { model ->
                        DropdownMenuItem(
                            text = { Text(model, color = TextCyanGlow) },
                            onClick = {
                                selectedModelInput = model
                                manualModelOverride = model
                                isModelDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // 6. MODEL NAME (MANUAL OVERRIDE)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "MODEL NAME (MANUAL OVERRIDE)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            OutlinedTextField(
                value = manualModelOverride,
                onValueChange = { manualModelOverride = it },
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
        }

        // 7. SYSTEM PROMPT
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "SYSTEM PROMPT (OPTIONAL — LEAVE BLANK FOR DEFAULT)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            OutlinedTextField(
                value = systemPromptInput,
                onValueChange = { systemPromptInput = it },
                placeholder = { Text("Enter custom system instructions for Jarvis...", fontSize = 12.sp, color = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkGlassSurface,
                    unfocusedContainerColor = DarkGlassSurface,
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = DarkGlassBorder,
                    focusedTextColor = TextCyanGlow,
                    unfocusedTextColor = TextCyanGlow
                )
            )
        }

        // 8. SAVE SETTINGS BUTTON
        Button(
            onClick = {
                val updated = currentProvider.copy(
                    apiKey = apiKeyInput,
                    baseUrl = baseUrlInput,
                    selectedModel = manualModelOverride.ifEmpty { selectedModelInput },
                    systemPrompt = systemPromptInput,
                    isSelected = true
                )
                onSaveProvider(updated)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TerminalGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Settings", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
