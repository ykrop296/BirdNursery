package com.example.birdnursery.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "feeding_records",
    foreignKeys = [
        ForeignKey(
            entity = Bird::class,
            parentColumns = ["id"],
            childColumns = ["birdId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FeedingRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val birdId: Long,
    val feedingTime: Long = System.currentTimeMillis()
)
