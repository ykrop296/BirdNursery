package com.example.birdnursery.data.repository

import com.example.birdnursery.data.database.BirdDao
import com.example.birdnursery.data.database.CageDao
import com.example.birdnursery.data.database.FeedingDao
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.data.model.Cage
import com.example.birdnursery.data.model.FeedingRecord
import kotlinx.coroutines.flow.Flow

class BirdRepository(
    private val birdDao: BirdDao,
    private val cageDao: CageDao,
    private val feedingDao: FeedingDao
) {
    // Birds
    fun getBirdsByCage(cageId: Long): Flow<List<Bird>> = birdDao.getBirdsByCage(cageId)
    fun getBirdsWithoutCage(): Flow<List<Bird>> = birdDao.getBirdsWithoutCage()
    fun getAllBirds(): Flow<List<Bird>> = birdDao.getAllBirds()
    suspend fun getBirdById(id: Long): Bird? = birdDao.getBirdById(id)
    suspend fun getChildrenOf(parentId: Long): List<Bird> = birdDao.getChildrenOf(parentId)
    suspend fun insertBird(bird: Bird): Long = birdDao.insertBird(bird)
    suspend fun updateBird(bird: Bird) = birdDao.updateBird(bird)
    suspend fun saveWithChildren(
        bird: Bird,
        childIds: Set<Long>,
        previousChildIds: Set<Long>,
        asFather: Boolean
    ): Long = birdDao.saveWithChildren(bird, childIds, previousChildIds, asFather)
    suspend fun deleteBird(bird: Bird) = birdDao.deleteWithRelations(bird)
    suspend fun moveBirdToCage(birdId: Long, cageId: Long?) = birdDao.moveBirdToCage(birdId, cageId)

    // Cages
    fun getAllCages(): Flow<List<Cage>> = cageDao.getAllCages()
    suspend fun getCageById(id: Long): Cage? = cageDao.getCageById(id)
    suspend fun insertCage(cage: Cage): Long = cageDao.insertCage(cage)
    suspend fun updateCage(cage: Cage) = cageDao.updateCage(cage)
    suspend fun deleteCage(cage: Cage) = cageDao.deleteCage(cage)

    // Feeding
    fun getLastFeeding(birdId: Long): Flow<FeedingRecord?> = feedingDao.getLastFeeding(birdId)
    fun getFeedingHistory(birdId: Long): Flow<List<FeedingRecord>> = feedingDao.getFeedingHistory(birdId)
    suspend fun recordFeeding(birdId: Long) = feedingDao.insertFeeding(FeedingRecord(birdId = birdId))
}
