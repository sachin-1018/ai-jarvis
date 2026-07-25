package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "terminal_logs")
data class TerminalLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val logType: String, // "INFO", "THINKING", "EXECUTION", "ERROR", "LEARNING"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
