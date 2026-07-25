package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ApiProviderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApiProviderDao {
    @Query("SELECT * FROM api_providers")
    fun getAllProviders(): Flow<List<ApiProviderEntity>>

    @Query("SELECT * FROM api_providers WHERE providerKey = :key LIMIT 1")
    suspend fun getProviderByKey(key: String): ApiProviderEntity?

    @Query("SELECT * FROM api_providers WHERE isSelected = 1 LIMIT 1")
    suspend fun getSelectedProvider(): ApiProviderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProvider(provider: ApiProviderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(providers: List<ApiProviderEntity>)

    @Query("UPDATE api_providers SET isSelected = CASE WHEN providerKey = :selectedKey THEN 1 ELSE 0 END")
    suspend fun setSelectedProvider(selectedKey: String)
}
