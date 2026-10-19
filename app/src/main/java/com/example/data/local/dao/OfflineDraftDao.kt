package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.OfflineDraftEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for offline-friendly donation drafts.
 * Follows Room reactive principles, returning Flow for live observation
 * and marking mutations with suspend.
 */
@Dao
interface OfflineDraftDao {

    @Query("SELECT * FROM offline_drafts ORDER BY savedAtTimestamp DESC")
    fun getAllDrafts(): Flow<List<OfflineDraftEntity>>

    @Query("SELECT * FROM offline_drafts WHERE isSynced = 0 ORDER BY savedAtTimestamp DESC")
    fun getPendingDrafts(): Flow<List<OfflineDraftEntity>>

    @Query("SELECT COUNT(*) FROM offline_drafts WHERE isSynced = 0")
    fun getPendingDraftsCount(): Flow<Int>

    @Query("SELECT * FROM offline_drafts WHERE id = :id")
    suspend fun getDraftById(id: String): OfflineDraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: OfflineDraftEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrafts(drafts: List<OfflineDraftEntity>)

    @Update
    suspend fun updateDraft(draft: OfflineDraftEntity)

    @Query("UPDATE offline_drafts SET isSynced = 1, lastSyncError = null WHERE id = :id")
    suspend fun markDraftAsSynced(id: String)

    @Query("UPDATE offline_drafts SET syncAttempts = syncAttempts + 1, lastSyncError = :error WHERE id = :id")
    suspend fun recordSyncFailure(id: String, error: String)

    @Query("DELETE FROM offline_drafts WHERE id = :id")
    suspend fun deleteDraftById(id: String)

    @Query("DELETE FROM offline_drafts WHERE isSynced = 1")
    suspend fun clearSyncedDrafts()

    @Query("DELETE FROM offline_drafts")
    suspend fun deleteAllDrafts()
}
