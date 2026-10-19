package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.HistoricalRescueEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for historical food rescue audit records.
 * Provides aggregations for impact reporting and reactive queries.
 */
@Dao
interface HistoricalRescueDao {

    @Query("SELECT * FROM historical_rescues ORDER BY deliveryTimestamp DESC")
    fun getAllHistoricalRescues(): Flow<List<HistoricalRescueEntity>>

    @Query("SELECT * FROM historical_rescues WHERE donorOrg = :donorOrg ORDER BY deliveryTimestamp DESC")
    fun getRescuesByDonor(donorOrg: String): Flow<List<HistoricalRescueEntity>>

    @Query("SELECT * FROM historical_rescues WHERE recipientOrg = :recipientOrg ORDER BY deliveryTimestamp DESC")
    fun getRescuesByRecipient(recipientOrg: String): Flow<List<HistoricalRescueEntity>>

    @Query("SELECT * FROM historical_rescues WHERE volunteerId = :volunteerId ORDER BY deliveryTimestamp DESC")
    fun getRescuesByVolunteer(volunteerId: String): Flow<List<HistoricalRescueEntity>>

    @Query("SELECT * FROM historical_rescues WHERE id = :id")
    suspend fun getRescueById(id: String): HistoricalRescueEntity?

    @Query("SELECT COUNT(*) FROM historical_rescues")
    fun getRescueCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(quantityKg), 0.0) FROM historical_rescues")
    fun getTotalRescuedKg(): Flow<Double>

    @Query("SELECT COALESCE(SUM(estimatedMeals), 0) FROM historical_rescues")
    fun getTotalEstimatedMeals(): Flow<Int>

    @Query("SELECT COALESCE(SUM(co2PreventedKg), 0.0) FROM historical_rescues")
    fun getTotalCo2PreventedKg(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRescue(rescue: HistoricalRescueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRescues(rescues: List<HistoricalRescueEntity>)

    @Query("DELETE FROM historical_rescues WHERE id = :id")
    suspend fun deleteRescueById(id: String)

    @Query("DELETE FROM historical_rescues")
    suspend fun deleteAllRescues()
}
