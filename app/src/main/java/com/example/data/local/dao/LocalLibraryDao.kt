package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.LocalLibraryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalLibraryDao {
    @Query("SELECT * FROM local_libraries ORDER BY id ASC")
    fun getAllLibraries(): Flow<List<LocalLibraryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(libraries: List<LocalLibraryEntity>)

    @Query("UPDATE local_libraries SET isDownloaded = :isDownloaded WHERE id = :id")
    suspend fun updateDownloadStatus(id: Long, isDownloaded: Boolean)
}
