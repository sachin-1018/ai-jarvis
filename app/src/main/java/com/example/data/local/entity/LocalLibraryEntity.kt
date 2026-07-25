package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_libraries")
data class LocalLibraryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val libraryName: String,
    val category: String, // "AI_MODEL", "MATH_SCIENCE", "PATTERN_ANALYTICS", "HINDI_NLP"
    val sizeMb: Double,
    val isDownloaded: Boolean = false,
    val description: String,
    val downloadUrl: String = ""
)
