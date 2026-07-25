package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "USER" or "JARVIS"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val providerName: String = "Gemini",
    val modelName: String = "gemini-2.0-flash",
    val isVoiceMessage: Boolean = false,
    val imageUri: String? = null,
    val mathSteps: String? = null
)
