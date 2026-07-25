package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chart_patterns")
data class ChartPatternEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val folderName: String = "Dada Folder", // "Dada Folder", "Kalyan Chart", "Main Bazar", "Custom Formula"
    val title: String,
    val dataContent: String, // Historical digits, copied live screen data, panel records
    val formulaLogic: String = "", // Trained logic e.g., "Cut Ank = (Open + Close) % 10"
    val language: String = "Hindi/English",
    val timestamp: Long = System.currentTimeMillis()
)
