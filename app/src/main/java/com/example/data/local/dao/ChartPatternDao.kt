package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.ChartPatternEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChartPatternDao {
    @Query("SELECT * FROM chart_patterns ORDER BY timestamp DESC")
    fun getAllPatterns(): Flow<List<ChartPatternEntity>>

    @Query("SELECT * FROM chart_patterns WHERE folderName = :folderName ORDER BY timestamp DESC")
    fun getPatternsByFolder(folderName: String): Flow<List<ChartPatternEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPattern(pattern: ChartPatternEntity): Long

    @Query("DELETE FROM chart_patterns WHERE id = :id")
    suspend fun deletePattern(id: Long)

    @Query("DELETE FROM chart_patterns")
    suspend fun clearAll()
}
