package com.example.birdnursery.data.database

import androidx.room.*
import com.example.birdnursery.data.model.Cage
import kotlinx.coroutines.flow.Flow

@Dao
interface CageDao {
    @Query("SELECT * FROM cages ORDER BY name ASC")
    fun getAllCages(): Flow<List<Cage>>

    @Query("SELECT * FROM cages WHERE id = :id")
    suspend fun getCageById(id: Long): Cage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCage(cage: Cage): Long

    @Update
    suspend fun updateCage(cage: Cage)

    @Delete
    suspend fun deleteCage(cage: Cage)
}
