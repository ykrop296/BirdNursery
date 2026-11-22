package com.example.birdnursery

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Main : NavKey

@Serializable
data object AddBird : NavKey

@Serializable
data class BirdDetail(val birdId: Long) : NavKey

@Serializable
data class EditBird(val birdId: Long) : NavKey
