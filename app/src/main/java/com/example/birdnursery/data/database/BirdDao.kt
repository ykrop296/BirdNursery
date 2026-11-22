package com.example.birdnursery.data.database

import androidx.room.*
import com.example.birdnursery.data.model.Bird
import kotlinx.coroutines.flow.Flow

@Dao
interface BirdDao {
    @Query("SELECT * FROM birds WHERE cageId = :cageId ORDER BY name ASC")
    fun getBirdsByCage(cageId: Long): Flow<List<Bird>>

    @Query("SELECT * FROM birds WHERE cageId IS NULL ORDER BY name ASC")
    fun getBirdsWithoutCage(): Flow<List<Bird>>

    @Query("SELECT * FROM birds ORDER BY name ASC")
    fun getAllBirds(): Flow<List<Bird>>

    @Query("SELECT * FROM birds WHERE id = :id")
    suspend fun getBirdById(id: Long): Bird?

    @Query("SELECT * FROM birds WHERE motherId = :parentId OR fatherId = :parentId ORDER BY name ASC")
    suspend fun getChildrenOf(parentId: Long): List<Bird>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBird(bird: Bird): Long

    @Update
    suspend fun updateBird(bird: Bird)

    @Delete
    suspend fun deleteBird(bird: Bird)

    @Query("UPDATE birds SET cageId = :cageId WHERE id = :birdId")
    suspend fun moveBirdToCage(birdId: Long, cageId: Long?)

    @Query("UPDATE birds SET motherId = :parentId WHERE id = :childId")
    suspend fun setMother(childId: Long, parentId: Long?)

    @Query("UPDATE birds SET fatherId = :parentId WHERE id = :childId")
    suspend fun setFather(childId: Long, parentId: Long?)

    @Query("UPDATE birds SET motherId = NULL WHERE motherId = :parentId")
    suspend fun clearMotherReferences(parentId: Long)

    @Query("UPDATE birds SET fatherId = NULL WHERE fatherId = :parentId")
    suspend fun clearFatherReferences(parentId: Long)

    @Transaction
    suspend fun saveWithChildren(
        bird: Bird,
        childIds: Set<Long>,
        previousChildIds: Set<Long>,
        asFather: Boolean
    ): Long {
        val birdId = if (bird.id == 0L) insertBird(bird) else {
            updateBird(bird)
            bird.id
        }
        (previousChildIds - childIds).forEach { childId ->
            if (asFather) setFather(childId, null) else setMother(childId, null)
        }
        childIds.forEach { childId ->
            if (asFather) setFather(childId, birdId) else setMother(childId, birdId)
        }
        return birdId
    }

    @Transaction
    suspend fun deleteWithRelations(bird: Bird) {
        clearMotherReferences(bird.id)
        clearFatherReferences(bird.id)
        deleteBird(bird)
    }
}
