package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.TerminalLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TerminalLogDao {
    @Query("SELECT * FROM terminal_logs ORDER BY timestamp DESC LIMIT 200")
    fun getRecentLogs(): Flow<List<TerminalLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: TerminalLogEntity)

    @Query("DELETE FROM terminal_logs")
    suspend fun clearLogs()
}
