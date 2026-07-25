package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ApiProviderDao
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.ChartPatternDao
import com.example.data.local.dao.LocalLibraryDao
import com.example.data.local.dao.OfflineMemoryDao
import com.example.data.local.dao.TerminalLogDao
import com.example.data.local.entity.ApiProviderEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ChartPatternEntity
import com.example.data.local.entity.LocalLibraryEntity
import com.example.data.local.entity.OfflineMemoryEntity
import com.example.data.local.entity.TerminalLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ChatMessageEntity::class,
        ApiProviderEntity::class,
        OfflineMemoryEntity::class,
        TerminalLogEntity::class,
        ChartPatternEntity::class,
        LocalLibraryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class JarvisDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao
    abstract fun apiProviderDao(): ApiProviderDao
    abstract fun offlineMemoryDao(): OfflineMemoryDao
    abstract fun terminalLogDao(): TerminalLogDao
    abstract fun chartPatternDao(): ChartPatternDao
    abstract fun localLibraryDao(): LocalLibraryDao

    companion object {
        @Volatile
        private var INSTANCE: JarvisDatabase? = null

        fun getInstance(context: Context): JarvisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JarvisDatabase::class.java,
                    "jarvis_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            seedDefaultData(getInstance(context))
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDefaultData(db: JarvisDatabase) {
            val defaultProviders = listOf(
                ApiProviderEntity(
                    providerKey = "gemini",
                    providerName = "Google Gemini (AI Studio)",
                    apiKey = "",
                    baseUrl = "https://generativelanguage.googleapis.com/",
                    selectedModel = "gemini-2.0-flash",
                    availableModels = "gemini-1.5-pro, gemini-1.5-flash, gemini-2.0-flash, gemini-2.0-flash-lite",
                    systemPrompt = "You are AI Jarvis, a loyal and super-intelligent AI assistant created by Sachin Solunke. You understand Hindi and English perfectly.",
                    isEnabled = true,
                    isSelected = true
                ),
                ApiProviderEntity(
                    providerKey = "opencode_zen",
                    providerName = "OpenCode Zen",
                    apiKey = "",
                    baseUrl = "https://api.opencode.ai/v1/",
                    selectedModel = "big-pickle",
                    availableModels = "big-pickle, code-zen-1, custom-model",
                    systemPrompt = "You are Build AI, an expert Android developer and coding assistant inside the Build Studio app.",
                    isEnabled = true,
                    isSelected = false
                ),
                ApiProviderEntity(
                    providerKey = "anthropic",
                    providerName = "Anthropic (Claude)",
                    apiKey = "",
                    baseUrl = "https://api.anthropic.com/v1/",
                    selectedModel = "claude-3-5-sonnet-20241022",
                    availableModels = "claude-3-5-sonnet-20241022, claude-3-5-haiku-20241022, claude-3-opus-20240229",
                    systemPrompt = "You are AI Jarvis in Anthropic mode. Fast and highly logical.",
                    isEnabled = true,
                    isSelected = false
                ),
                ApiProviderEntity(
                    providerKey = "grok",
                    providerName = "xAI Grok",
                    apiKey = "",
                    baseUrl = "https://api.x.ai/v1/",
                    selectedModel = "grok-2",
                    availableModels = "grok-2, grok-beta",
                    systemPrompt = "You are AI Jarvis running Grok mode.",
                    isEnabled = true,
                    isSelected = false
                ),
                ApiProviderEntity(
                    providerKey = "deepseek",
                    providerName = "DeepSeek AI",
                    apiKey = "",
                    baseUrl = "https://api.deepseek.com/v1/",
                    selectedModel = "deepseek-chat",
                    availableModels = "deepseek-chat, deepseek-reasoner",
                    systemPrompt = "You are AI Jarvis DeepSeek reasoning engine.",
                    isEnabled = true,
                    isSelected = false
                ),
                ApiProviderEntity(
                    providerKey = "openrouter",
                    providerName = "OpenRouter",
                    apiKey = "",
                    baseUrl = "https://openrouter.ai/api/v1/",
                    selectedModel = "openai/gpt-4o",
                    availableModels = "openai/gpt-4o, anthropic/claude-3.5-sonnet, google/gemini-pro-1.5",
                    systemPrompt = "You are AI Jarvis connected via OpenRouter.",
                    isEnabled = true,
                    isSelected = false
                ),
                ApiProviderEntity(
                    providerKey = "cohere",
                    providerName = "Cohere",
                    apiKey = "",
                    baseUrl = "https://api.cohere.ai/v1/",
                    selectedModel = "command-r-plus",
                    availableModels = "command-r-plus, command-r",
                    systemPrompt = "You are AI Jarvis in Cohere mode.",
                    isEnabled = true,
                    isSelected = false
                ),
                ApiProviderEntity(
                    providerKey = "mistral",
                    providerName = "Mistral AI",
                    apiKey = "",
                    baseUrl = "https://api.mistral.ai/v1/",
                    selectedModel = "mistral-large-latest",
                    availableModels = "mistral-large-latest, mistral-small-latest, codestral-latest",
                    systemPrompt = "You are AI Jarvis Mistral engine.",
                    isEnabled = true,
                    isSelected = false
                ),
                ApiProviderEntity(
                    providerKey = "groq",
                    providerName = "Groq Ultra-Fast",
                    apiKey = "",
                    baseUrl = "https://api.groq.com/openai/v1/",
                    selectedModel = "llama-3.3-70b-versatile",
                    availableModels = "llama-3.3-70b-versatile, llama-3.1-8b-instant, mixtral-8x7b-32768",
                    systemPrompt = "You are AI Jarvis Groq instant response engine.",
                    isEnabled = true,
                    isSelected = false
                ),
                ApiProviderEntity(
                    providerKey = "together",
                    providerName = "Together AI",
                    apiKey = "",
                    baseUrl = "https://api.together.xyz/v1/",
                    selectedModel = "meta-llama/Llama-3-70b-chat-hf",
                    availableModels = "meta-llama/Llama-3-70b-chat-hf, mistralai/Mixtral-8x22B-Instruct-v0.1",
                    systemPrompt = "You are AI Jarvis Together AI engine.",
                    isEnabled = true,
                    isSelected = false
                )
            )

            db.apiProviderDao().insertAll(defaultProviders)

            val defaultLibraries = listOf(
                LocalLibraryEntity(
                    libraryName = "Hindi & English Voice NLP Pack",
                    category = "HINDI_NLP",
                    sizeMb = 28.5,
                    isDownloaded = true,
                    description = "Offline Speech-to-Text and TTS phonetic maps for Hindi/English bilingual understanding."
                ),
                LocalLibraryEntity(
                    libraryName = "Offline Math & Science Neural Weights",
                    category = "MATH_SCIENCE",
                    sizeMb = 42.0,
                    isDownloaded = true,
                    description = "On-device formula solving rules for Calculus, Algebra, Physics constants, and Chemistry equations."
                ),
                LocalLibraryEntity(
                    libraryName = "Chart & Number Pattern Analytics Pack",
                    category = "PATTERN_ANALYTICS",
                    sizeMb = 18.2,
                    isDownloaded = true,
                    description = "Chart matrix analyzer, Open/Close Cut Ank, Panel Sum, and Sequence diff algorithms."
                ),
                LocalLibraryEntity(
                    libraryName = "Custom Training Weights & Dada Folder DB",
                    category = "PATTERN_ANALYTICS",
                    sizeMb = 12.4,
                    isDownloaded = true,
                    description = "Personal trained rules and saved Dada folder records stored locally in SQLite."
                )
            )

            db.localLibraryDao().insertAll(defaultLibraries)

            db.terminalLogDao().insertLog(
                TerminalLogEntity(
                    logType = "INFO",
                    message = "Jarvis System Initialization Complete. Ready in Bilingual Mode (Hindi/English). Developer: Sachin Solunke."
                )
            )

            db.offlineMemoryDao().insertMemory(
                OfflineMemoryEntity(
                    topic = "Math Rules",
                    factOrRule = "Derivative d/dx(x^n) = n*x^(n-1). Integral of x^n dx = (x^(n+1))/(n+1).",
                    category = "MATH_SCIENCE"
                )
            )
            db.offlineMemoryDao().insertMemory(
                OfflineMemoryEntity(
                    topic = "Physics Rules",
                    factOrRule = "Speed of light c = 3x10^8 m/s. Newton's second law F = m*a. Gravity g = 9.8 m/s^2.",
                    category = "MATH_SCIENCE"
                )
            )
            db.offlineMemoryDao().insertMemory(
                OfflineMemoryEntity(
                    topic = "Developer Info",
                    factOrRule = "App created by Sachin Solunke. Email: woldcom87@gmail.com.",
                    category = "USER_PREF"
                )
            )

            // Seed default Dada folder record
            db.chartPatternDao().insertPattern(
                ChartPatternEntity(
                    folderName = "Dada Folder",
                    title = "Sample Kalyan Open-Close Panel Chart",
                    dataContent = "Mon: 345-23-189, Tue: 250-78-369, Wed: 140-56-240, Thu: 479-01-380",
                    formulaLogic = "Formula: Open Panel Sum = 3+4+5 = 12 -> Open Digit = 2. Close Panel Sum = 1+8+9 = 18 -> Close Digit = 8. Jodi = 28.",
                    language = "Hindi/English"
                )
            )
        }
    }
}
