package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_providers")
data class ApiProviderEntity(
    @PrimaryKey
    val providerKey: String, // e.g., "gemini", "anthropic", "grok", "deepseek", "openrouter", "cohere", "mistral", "groq", "together", "opencode_zen", "custom"
    val providerName: String, // Display Name
    val apiKey: String,
    val baseUrl: String, // Base URL for custom/OpenAI-compatible APIs
    val selectedModel: String,
    val availableModels: String, // Comma separated list of supported models
    val systemPrompt: String = "",
    val isEnabled: Boolean = true,
    val isSelected: Boolean = false
)
