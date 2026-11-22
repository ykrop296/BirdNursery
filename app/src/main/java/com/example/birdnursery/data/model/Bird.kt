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
    val headColor: Int = 0xFF8C7968.toInt(),
    val cheekColor: Int = 0xFFFFF3E0.toInt(),
    val throatColor: Int = 0xFF5D4037.toInt(),
    val bodyColor: Int = 0xFF9C7B5B.toInt(),
    val chestColor: Int = 0xFFD7C6B8.toInt(),
    val bellyColor: Int = 0xFFFFF8E7.toInt(),
    val wingColor: Int = 0xFF795A40.toInt(),
    val tailColor: Int = 0xFF4A3728.toInt(),
    val legColor: Int = 0xFF8B7355.toInt(),
    val beakColor: Int = 0xFF607D8B.toInt(),
)
