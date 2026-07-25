package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.OfflineMemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflineMemoryDao {
    @Query("SELECT * FROM offline_memory ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<OfflineMemoryEntity>>

    @Query("SELECT * FROM offline_memory WHERE category = :category ORDER BY timestamp DESC")
    fun getMemoriesByCategory(category: String): Flow<List<OfflineMemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: OfflineMemoryEntity): Long

    @Query("DELETE FROM offline_memory WHERE id = :id")
    suspend fun deleteMemory(id: Long)
}
