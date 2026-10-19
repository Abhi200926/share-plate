package com.example.data.local

import com.example.data.local.dao.HistoricalRescueDao
import com.example.data.local.dao.OfflineDraftDao
import com.example.data.local.entity.HistoricalRescueEntity
import com.example.data.local.entity.OfflineDraftEntity
import com.example.model.OfflineListingDraft
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

/**
 * Repository pattern implementation for SharePlate local Room database.
 * Abstracts DAO operations from the UI and ViewModels, providing reactive
 * flows and coroutine-safe mutations.
 */
class SharePlateLocalRepository(
    private val offlineDraftDao: OfflineDraftDao,
    private val historicalRescueDao: HistoricalRescueDao
) {
    // Reactive Streams for UI observation
    val allDrafts: Flow<List<OfflineDraftEntity>> = offlineDraftDao.getAllDrafts()
    val pendingDrafts: Flow<List<OfflineDraftEntity>> = offlineDraftDao.getPendingDrafts()
    val pendingDraftsCount: Flow<Int> = offlineDraftDao.getPendingDraftsCount()

    val allHistoricalRescues: Flow<List<HistoricalRescueEntity>> = historicalRescueDao.getAllHistoricalRescues()
    val totalRescuedKg: Flow<Double> = historicalRescueDao.getTotalRescuedKg()
    val totalEstimatedMeals: Flow<Int> = historicalRescueDao.getTotalEstimatedMeals()
    val totalCo2PreventedKg: Flow<Double> = historicalRescueDao.getTotalCo2PreventedKg()
    val rescueCount: Flow<Int> = historicalRescueDao.getRescueCount()

    // Offline Draft operations
    suspend fun saveDraft(draft: OfflineDraftEntity) {
        offlineDraftDao.insertDraft(draft)
    }

    suspend fun saveDomainDraft(draft: OfflineListingDraft) {
        offlineDraftDao.insertDraft(OfflineDraftEntity.fromDomainModel(draft, isSynced = false))
    }

    suspend fun markDraftAsSynced(id: String) {
        offlineDraftDao.markDraftAsSynced(id)
    }

    suspend fun recordDraftSyncError(id: String, error: String) {
        offlineDraftDao.recordSyncFailure(id, error)
    }

    suspend fun deleteDraftById(id: String) {
        offlineDraftDao.deleteDraftById(id)
    }

    suspend fun clearSyncedDrafts() {
        offlineDraftDao.clearSyncedDrafts()
    }

    // Historical Rescue operations
    suspend fun insertHistoricalRescue(rescue: HistoricalRescueEntity) {
        historicalRescueDao.insertRescue(rescue)
    }

    suspend fun insertHistoricalRescues(rescues: List<HistoricalRescueEntity>) {
        historicalRescueDao.insertRescues(rescues)
    }

    fun getRescuesByDonor(donorOrg: String): Flow<List<HistoricalRescueEntity>> {
        return historicalRescueDao.getRescuesByDonor(donorOrg)
    }

    fun getRescuesByRecipient(recipientOrg: String): Flow<List<HistoricalRescueEntity>> {
        return historicalRescueDao.getRescuesByRecipient(recipientOrg)
    }

    suspend fun seedSampleRescuesIfEmpty(sampleRescues: List<HistoricalRescueEntity>) {
        val currentCount = historicalRescueDao.getRescueCount().firstOrNull() ?: 0
        if (currentCount == 0 && sampleRescues.isNotEmpty()) {
            historicalRescueDao.insertRescues(sampleRescues)
        }
    }
}
