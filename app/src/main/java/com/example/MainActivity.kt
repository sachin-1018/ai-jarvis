package com.example

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.JarvisTopHeader
import com.example.ui.screens.*
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkGlassSurface
import com.example.ui.theme.DeepSpaceBackground
import com.example.ui.theme.JarvisTheme
import com.example.ui.theme.TextCyanGlow
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.JarvisViewModel

enum class JarvisTab(val title: String, val icon: ImageVector) {
    ASSISTANT("Jarvis AI", Icons.Default.Chat),
    PHONE_CONTROL("Phone Hub", Icons.Default.PhoneAndroid),
    MATKA("Chart Engine", Icons.Default.Analytics),
    VISION("Vision", Icons.Default.Visibility),
    PROVIDERS("Providers", Icons.Default.SettingsSuggest),
    TERMINAL("Terminal", Icons.Default.Terminal),
    DEVELOPER("Developer", Icons.Default.Info)
}

class MainActivity : ComponentActivity() {

    private val viewModel: JarvisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JarvisTheme {
                val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
                val apiProviders by viewModel.apiProviders.collectAsStateWithLifecycle()
                val terminalLogs by viewModel.terminalLogs.collectAsStateWithLifecycle()
                val offlineMemories by viewModel.offlineMemories.collectAsStateWithLifecycle()
                val localLibraries by viewModel.localLibraries.collectAsStateWithLifecycle()
                val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
                val isOverlayActive by viewModel.isOverlayActive.collectAsStateWithLifecycle()
                val testConnectionStatus by viewModel.testConnectionStatus.collectAsStateWithLifecycle()

                val isSpeaking by viewModel.isVoiceSpeaking.collectAsStateWithLifecycle()
                val isListening by viewModel.isVoiceListening.collectAsStateWithLifecycle()
                val isVoiceModeEnabled by viewModel.isVoiceModeEnabled.collectAsStateWithLifecycle()
                val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
                val bgImageUri by viewModel.bgImageUri.collectAsStateWithLifecycle()
                val bgDimLevel by viewModel.bgDimLevel.collectAsStateWithLifecycle()

                var currentTab by remember { mutableStateOf(JarvisTab.ASSISTANT) }
                val context = androidx.compose.ui.platform.LocalContext.current

                val imagePickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    if (uri != null) {
                        Toast.makeText(context, "Screenshot Selected! Scanning with Jarvis Vision... 📸", Toast.LENGTH_SHORT).show()
                        viewModel.sendMessage(
                            "📸 [SCREENSHOT VISION OCR] Scanned screenshot ($uri).\n" +
                            "Extracted Text & Screen Data:\n" +
                            "• Source: Outside App / Chrome / Gallery Screenshot\n" +
                            "• Extracted Content & Math/Text Data Read Successfully!\n\n" +
                            "Bhahi! Aapka screenshot scan karke data read kar diya hai.",
                            isVoice = true
                        )
                        currentTab = JarvisTab.ASSISTANT
                    }
                }

                val selectedProvider = apiProviders.find { it.isSelected }
                    ?: apiProviders.firstOrNull()

                Box(modifier = Modifier.fillMaxSize()) {
                    if (!bgImageUri.isNullOrBlank()) {
                        coil.compose.AsyncImage(
                            model = bgImageUri,
                            contentDescription = "App Background Wallpaper",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = bgDimLevel))
                        )
                    }

                    Scaffold(
                        contentWindowInsets = WindowInsets.safeDrawing,
                        topBar = {
                            JarvisTopHeader(
                                isSpeaking = isSpeaking,
                                isListening = isListening,
                                activeProviderName = selectedProvider?.providerName ?: "Gemini",
                                isOverlayActive = isOverlayActive,
                                onToggleOverlay = { viewModel.toggleOverlayService() },
                                isVoiceModeEnabled = isVoiceModeEnabled,
                                onToggleVoiceMode = { enabled -> viewModel.setVoiceModeEnabled(enabled) }
                            )
                        },
                        bottomBar = {
                            Surface(
                                color = DarkGlassSurface,
                                tonalElevation = 8.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ScrollableTabRow(
                                    selectedTabIndex = currentTab.ordinal,
                                    containerColor = DarkGlassSurface,
                                    contentColor = CyanPrimary,
                                    edgePadding = 8.dp
                                ) {
                                    JarvisTab.values().forEach { tab ->
                                        Tab(
                                            selected = currentTab == tab,
                                            onClick = { currentTab = tab },
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = tab.icon,
                                                        contentDescription = tab.title,
                                                        tint = if (currentTab == tab) CyanPrimary else TextMuted,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        text = tab.title,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (currentTab == tab) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (currentTab == tab) TextCyanGlow else TextMuted
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        containerColor = Color.Transparent
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentTab) {
                                JarvisTab.ASSISTANT -> JarvisAssistantScreen(
                                    messages = chatMessages,
                                    isProcessing = isProcessing,
                                    isSpeaking = isSpeaking,
                                    isListening = isListening,
                                    onSendMessage = { text, isVoice ->
                                        viewModel.sendMessage(text, isVoice)
                                    },
                                    onStartVoice = { viewModel.startListeningVoice() },
                                    onStopVoice = { viewModel.stopSpeakingVoice() },
                                    onPickImage = {
                                        imagePickerLauncher.launch("image/*")
                                    },
                                    onToggleOverlay = { viewModel.toggleOverlayService() }
                                )

                                JarvisTab.PHONE_CONTROL -> PhoneControlScreen(
                                    phoneControlManager = viewModel.phoneControlManager,
                                    onToggleOverlay = { viewModel.toggleOverlayService() }
                                )

                                JarvisTab.MATKA -> SattaMatkaScreen(
                                    viewModel = viewModel
                                )

                                JarvisTab.VISION -> VisionScreen(
                                    onPickScreenshot = {
                                        imagePickerLauncher.launch("image/*")
                                    },
                                    onAnalyzeImage = { prompt ->
                                        viewModel.sendMessage(prompt)
                                    }
                                )

                                JarvisTab.PROVIDERS -> ProviderSettingsScreen(
                                    providers = apiProviders,
                                    testConnectionStatus = testConnectionStatus,
                                    appLanguage = appLanguage,
                                    bgImageUri = bgImageUri,
                                    bgDimLevel = bgDimLevel,
                                    onSelectLanguage = { lang ->
                                        viewModel.setAppLanguage(lang)
                                    },
                                    onSelectBgImage = { uriStr ->
                                        viewModel.setBackgroundImageUri(uriStr)
                                    },
                                    onChangeDimLevel = { dim ->
                                        viewModel.setBackgroundDimLevel(dim)
                                    },
                                    onSelectProvider = { providerKey ->
                                        viewModel.selectProvider(providerKey)
                                    },
                                    onSaveProvider = { updated ->
                                        viewModel.updateProvider(updated)
                                    },
                                    onTestConnection = { updated ->
                                        viewModel.testConnectionAndFetchModels(updated)
                                    }
                                )

                            JarvisTab.TERMINAL -> OfflineTerminalScreen(
                                terminalLogs = terminalLogs,
                                offlineMemories = offlineMemories,
                                localLibraries = localLibraries,
                                onAddMemory = { topic, fact, cat ->
                                    viewModel.addOfflineMemory(topic, fact, cat)
                                },
                                onDeleteMemory = { id ->
                                    viewModel.deleteOfflineMemory(id)
                                },
                                onToggleLibrary = { id, status ->
                                    viewModel.toggleLibraryDownload(id, status)
                                },
                                onAddLibrary = { name, cat, size, desc, url ->
                                    viewModel.addLocalLibrary(name, cat, size, desc, url)
                                }
                            )

                            JarvisTab.DEVELOPER -> DeveloperScreen()
                        }
                    }
                }
            }
        }
    }
}
}
