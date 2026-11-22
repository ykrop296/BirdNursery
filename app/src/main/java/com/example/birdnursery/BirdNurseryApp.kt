package com.example.birdnursery

import android.app.Application
import com.example.birdnursery.data.database.AppDatabase
import com.example.birdnursery.data.repository.BirdRepository

class BirdNurseryApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        BirdRepository(
            database.birdDao(),
            database.cageDao(),
            database.feedingDao()
        )
    }
}
