package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_memory")
data class OfflineMemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val topic: String,
    val factOrRule: String,
    val category: String, // "MATH_SCIENCE", "USER_PREF", "PHONE_CONTROL", "LANGUAGE_HINDI"
    val timestamp: Long = System.currentTimeMillis()
)
