package com.example.birdnursery.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cages")
data class Cage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = ""
)
