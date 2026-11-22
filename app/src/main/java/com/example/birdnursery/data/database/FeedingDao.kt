package com.example.birdnursery.data.database

import androidx.room.*
import com.example.birdnursery.data.model.FeedingRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedingDao {
    @Query("SELECT * FROM feeding_records WHERE birdId = :birdId ORDER BY feedingTime DESC LIMIT 1")
    fun getLastFeeding(birdId: Long): Flow<FeedingRecord?>

    @Query("SELECT * FROM feeding_records WHERE birdId = :birdId ORDER BY feedingTime DESC")
    fun getFeedingHistory(birdId: Long): Flow<List<FeedingRecord>>

    @Insert
    suspend fun insertFeeding(record: FeedingRecord)

    @Query("DELETE FROM feeding_records WHERE birdId = :birdId")
    suspend fun deleteAllForBird(birdId: Long)
}
