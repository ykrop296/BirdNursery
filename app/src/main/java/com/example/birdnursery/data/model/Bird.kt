package com.example.birdnursery.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "birds",
    foreignKeys = [
        ForeignKey(
            entity = Cage::class,
            parentColumns = ["id"],
            childColumns = ["cageId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Bird(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val species: String = "",
    val birthDate: String = "",
    val arrivalDate: String = "",
    val weight: String = "",
    val sex: String = "",
    val diseases: String = "",
    val notes: String = "",
    val feedingIntervalHours: Int = 4,
    val cageId: Long? = null,
    val photoUri: String? = null,
    val motherId: Long? = null,
    val fatherId: Long? = null,
    val expectedHatchDate: Long? = null,
    // Генотипы окраса по модели gouldianfinches.eu (см. GouldianGenetics)
    val headGenotype: String = "RRoo",
    val breastGenotype: String = "PP",
    val bodyGenotype: String = "GGoo",
)
