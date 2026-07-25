package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.device.PhoneControlManager
import com.example.data.device.PhoneControlResult
import com.example.data.local.JarvisDatabase
import com.example.data.local.entity.ApiProviderEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.OfflineMemoryEntity
import com.example.data.local.entity.TerminalLogEntity
import com.example.data.remote.AiRepository
import com.example.data.voice.VoiceManager
import com.example.service.JarvisOverlayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val db = JarvisDatabase.getInstance(application)
    private val chatDao = db.chatDao()
    private val apiProviderDao = db.apiProviderDao()
    private val offlineMemoryDao = db.offlineMemoryDao()
    private val terminalLogDao = db.terminalLogDao()
    private val chartPatternDao = db.chartPatternDao()
    private val localLibraryDao = db.localLibraryDao()

    private val aiRepository = AiRepository(application, apiProviderDao)
    val phoneControlManager = PhoneControlManager(application)
    val voiceManager = VoiceManager(application)

    val chatMessages: StateFlow<List<ChatMessageEntity>> = chatDao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val apiProviders: StateFlow<List<ApiProviderEntity>> = apiProviderDao.getAllProviders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val terminalLogs: StateFlow<List<TerminalLogEntity>> = terminalLogDao.getRecentLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val offlineMemories: StateFlow<List<OfflineMemoryEntity>> = offlineMemoryDao.getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chartPatterns: StateFlow<List<com.example.data.local.entity.ChartPatternEntity>> = chartPatternDao.getAllPatterns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val localLibraries: StateFlow<List<com.example.data.local.entity.LocalLibraryEntity>> = localLibraryDao.getAllLibraries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _isOverlayActive = MutableStateFlow(false)
    val isOverlayActive: StateFlow<Boolean> = _isOverlayActive.asStateFlow()

    private val _testConnectionStatus = MutableStateFlow<String?>(null)
    val testConnectionStatus: StateFlow<String?> = _testConnectionStatus.asStateFlow()

    private val _appLanguage = MutableStateFlow("Hindi/English")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val prefs = application.getSharedPreferences("jarvis_app_settings", android.content.Context.MODE_PRIVATE)

    private val _bgImageUri = MutableStateFlow<String?>(prefs.getString("bg_image_uri", null))
    val bgImageUri: StateFlow<String?> = _bgImageUri.asStateFlow()

    private val _bgDimLevel = MutableStateFlow(prefs.getFloat("bg_dim_level", 0.4f))
    val bgDimLevel: StateFlow<Float> = _bgDimLevel.asStateFlow()

    fun setBackgroundImageUri(uriString: String?) {
        _bgImageUri.value = uriString
        prefs.edit().putString("bg_image_uri", uriString).apply()
        viewModelScope.launch {
            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "INFO",
                    message = "Background Wallpaper updated: ${uriString ?: "Default"}"
                )
            )
        }
    }

    fun setBackgroundDimLevel(dim: Float) {
        val clamped = dim.coerceIn(0f, 1f)
        _bgDimLevel.value = clamped
        prefs.edit().putFloat("bg_dim_level", clamped).apply()
    }

    fun setAppLanguage(language: String) {
        _appLanguage.value = language
        viewModelScope.launch {
            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "INFO",
                    message = "App Language switched to: $language"
                )
            )
        }
    }

    val isVoiceSpeaking = voiceManager.isSpeaking
    val isVoiceListening = voiceManager.isListening
    val isVoiceModeEnabled = voiceManager.isVoiceModeEnabled

    fun setVoiceModeEnabled(enabled: Boolean) {
        voiceManager.setVoiceModeEnabled(enabled)
        viewModelScope.launch {
            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "INFO",
                    message = "Voice Command Mode set to ${if (enabled) "ON 🎙️" else "OFF 🔇"}"
                )
            )
        }
    }

    fun sendMessage(userText: String, isVoice: Boolean = false, imageUri: String? = null) {
        if (userText.trim().isEmpty()) return

        viewModelScope.launch {
            _isProcessing.value = true

            // Insert User Message
            val userMsg = ChatMessageEntity(
                sender = "USER",
                text = userText,
                isVoiceMessage = isVoice,
                imageUri = imageUri
            )
            chatDao.insertMessage(userMsg)

            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "INFO",
                    message = "User Input Received: '$userText' [Voice: $isVoice]"
                )
            )

            // Check if phone control command
            val deviceResult = phoneControlManager.executeCommand(userText)
            if (deviceResult.actionTaken != "UNKNOWN") {
                val jarvisReply = ChatMessageEntity(
                    sender = "JARVIS",
                    text = "⚡ Phone Control Executed (${deviceResult.actionTaken}): ${deviceResult.feedbackMessage}",
                    providerName = "Jarvis Phone Controller",
                    modelName = "Device Intents"
                )
                chatDao.insertMessage(jarvisReply)

                terminalLogDao.insertLog(
                    TerminalLogEntity(
                        logType = "EXECUTION",
                        message = "Device Command Executed: ${deviceResult.actionTaken} -> ${deviceResult.feedbackMessage}"
                    )
                )

                if (isVoice || isVoiceModeEnabled.value) {
                    val langCode = when (_appLanguage.value) {
                        "English" -> "en"
                        "Hindi" -> "hi"
                        else -> "hi"
                    }
                    voiceManager.speak(deviceResult.feedbackMessage, langCode)
                }
                _isProcessing.value = false
                return@launch
            }

            // AI Model Generation Call
            val response = aiRepository.generateResponse(userText, imageUri, offlineMemories.value)

            // Log thinking steps
            response.thinkingSteps.forEach { step ->
                terminalLogDao.insertLog(
                    TerminalLogEntity(
                        logType = "THINKING",
                        message = step
                    )
                )
            }

            val jarvisMsg = ChatMessageEntity(
                sender = "JARVIS",
                text = response.replyText,
                providerName = response.providerUsed,
                modelName = response.modelUsed,
                mathSteps = if (response.thinkingSteps.isNotEmpty()) response.thinkingSteps.joinToString("\n") else null
            )
            chatDao.insertMessage(jarvisMsg)

            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = if (response.isOffline) "LEARNING" else "INFO",
                    message = "Jarvis Response generated via ${response.providerUsed} (${response.modelUsed})"
                )
            )

            if (isVoice || isVoiceModeEnabled.value) {
                val langCode = when (_appLanguage.value) {
                    "English" -> "en"
                    "Hindi" -> "hi"
                    else -> "hi"
                }
                voiceManager.speak(response.replyText, langCode)
            }

            _isProcessing.value = false
        }
    }

    fun selectProvider(providerKey: String) {
        viewModelScope.launch {
            apiProviderDao.setSelectedProvider(providerKey)
            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "INFO",
                    message = "Active AI Provider changed to: $providerKey"
                )
            )
        }
    }

    fun updateProvider(provider: ApiProviderEntity) {
        viewModelScope.launch {
            val trimmed = provider.copy(
                apiKey = provider.apiKey.trim(),
                baseUrl = provider.baseUrl.trim(),
                selectedModel = provider.selectedModel.trim(),
                isSelected = true
            )
            apiProviderDao.insertOrUpdateProvider(trimmed)
            apiProviderDao.setSelectedProvider(trimmed.providerKey)
            _testConnectionStatus.value = "✅ Settings saved for ${trimmed.providerName}!"
            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "INFO",
                    message = "Saved Provider Config: ${trimmed.providerName} (Key Length: ${trimmed.apiKey.length}, Model: ${trimmed.selectedModel})"
                )
            )
        }
    }

    fun testConnectionAndFetchModels(provider: ApiProviderEntity) {
        viewModelScope.launch {
            val trimmed = provider.copy(
                apiKey = provider.apiKey.trim(),
                baseUrl = provider.baseUrl.trim(),
                selectedModel = provider.selectedModel.trim(),
                isSelected = true
            )
            _testConnectionStatus.value = "⏳ Testing API Connection to ${trimmed.providerName}..."
            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "THINKING",
                    message = "Ping test -> ${trimmed.baseUrl} for model ${trimmed.selectedModel}"
                )
            )

            // Save updated key first
            apiProviderDao.insertOrUpdateProvider(trimmed)
            apiProviderDao.setSelectedProvider(trimmed.providerKey)

            // Perform actual live request
            val response = aiRepository.generateResponse("Ping test. Reply in 1 short sentence.", null, offlineMemories.value)
            if (!response.isOffline) {
                _testConnectionStatus.value = "✅ Success! Connected to ${response.providerUsed} (${response.modelUsed}). Response: ${response.replyText.take(120)}"
            } else {
                _testConnectionStatus.value = "⚠️ Test Result: ${response.replyText}"
            }
        }
    }

    fun addOfflineMemory(topic: String, fact: String, category: String) {
        viewModelScope.launch {
            offlineMemoryDao.insertMemory(
                OfflineMemoryEntity(
                    topic = topic,
                    factOrRule = fact,
                    category = category
                )
            )
            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "LEARNING",
                    message = "Self-Learning Memory Indexed: [$category] $topic -> $fact"
                )
            )
        }
    }

    fun deleteOfflineMemory(id: Long) {
        viewModelScope.launch {
            offlineMemoryDao.deleteMemory(id)
        }
    }

    fun addChartPattern(folderName: String, title: String, dataContent: String, formulaLogic: String) {
        viewModelScope.launch {
            chartPatternDao.insertPattern(
                com.example.data.local.entity.ChartPatternEntity(
                    folderName = folderName.ifBlank { "Dada Folder" },
                    title = title,
                    dataContent = dataContent,
                    formulaLogic = formulaLogic,
                    language = "Hindi/English"
                )
            )
            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "LEARNING",
                    message = "Saved Chart Record in '$folderName': $title"
                )
            )
        }
    }

    fun deleteChartPattern(id: Long) {
        viewModelScope.launch {
            chartPatternDao.deletePattern(id)
            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "INFO",
                    message = "Deleted Chart Record #$id"
                )
            )
        }
    }

    fun toggleLibraryDownload(id: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            localLibraryDao.updateDownloadStatus(id, !currentStatus)
            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "INFO",
                    message = "Local Library #$id status set to ${if (!currentStatus) "DOWNLOADED / ACTIVE" else "NOT DOWNLOADED"}"
                )
            )
        }
    }

    fun addLocalLibrary(name: String, category: String, sizeMb: Double, description: String, downloadUrl: String) {
        viewModelScope.launch {
            val lib = com.example.data.local.entity.LocalLibraryEntity(
                libraryName = name,
                category = category,
                sizeMb = sizeMb,
                isDownloaded = false,
                description = description,
                downloadUrl = downloadUrl
            )
            localLibraryDao.insertAll(listOf(lib))
            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "LEARNING",
                    message = "Custom Local Library registered: '$name' ($category)"
                )
            )
        }
    }

    fun analyzeAndSaveChart(folderName: String, title: String, rawData: String, customFormula: String) {
        viewModelScope.launch {
            val analysis = com.example.data.engine.ChartPatternEngine.analyzePattern(rawData, customFormula)
            val fullFormulaLogic = "Open/Close = ${analysis.openDigit}/${analysis.closeDigit} (Jodi: ${analysis.jodi}) | Cut Ank: ${analysis.cutAnkOpen}/${analysis.cutAnkClose} | Formula: ${customFormula.ifBlank { "Auto Panel Total Rule" }}"

            chartPatternDao.insertPattern(
                com.example.data.local.entity.ChartPatternEntity(
                    folderName = folderName.ifBlank { "Dada Folder" },
                    title = title.ifBlank { "Analyzed Chart Pattern ${System.currentTimeMillis() % 1000}" },
                    dataContent = rawData,
                    formulaLogic = fullFormulaLogic,
                    language = "Hindi/English"
                )
            )

            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "LEARNING",
                    message = "Analyzed & Saved to $folderName -> Single: ${analysis.openDigit}-${analysis.closeDigit}, Jodi: ${analysis.jodi}"
                )
            )
        }
    }

    fun captureAndSaveScreenData(title: String, rawContent: String, folderName: String = "Dada Folder") {
        viewModelScope.launch {
            chartPatternDao.insertPattern(
                com.example.data.local.entity.ChartPatternEntity(
                    folderName = folderName,
                    title = title.ifBlank { "Screen Data Capture ${System.currentTimeMillis() % 1000}" },
                    dataContent = rawContent,
                    formulaLogic = "Captured via Overlay Screen Inspector (Chrome/Apps)",
                    language = "Hindi/English"
                )
            )
            terminalLogDao.insertLog(
                TerminalLogEntity(
                    logType = "LEARNING",
                    message = "Captured & Saved Screen Data to '$folderName': $title"
                )
            )
        }
    }

    fun startListeningVoice() {
        voiceManager.startListening { recognized ->
            sendMessage(recognized, isVoice = true)
        }
    }

    fun stopSpeakingVoice() {
        voiceManager.stopSpeaking()
    }

    fun toggleOverlayService() {
        val app = getApplication<Application>()
        val intent = Intent(app, JarvisOverlayService::class.java)
        if (!_isOverlayActive.value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                app.startForegroundService(intent)
            } else {
                app.startService(intent)
            }
            _isOverlayActive.value = true
        } else {
            app.stopService(intent)
            _isOverlayActive.value = false
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            chatDao.clearHistory()
            terminalLogDao.clearLogs()
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.shutdown()
    }
}
