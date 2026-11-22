package com.example.birdnursery.ui.screens.birddetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.birdnursery.BirdNurseryApp
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.data.model.FeedingRecord
import com.example.birdnursery.data.repository.BirdRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BirdDetailUiState(
    val bird: Bird? = null,
    val mother: Bird? = null,
    val father: Bird? = null,
    val children: List<Bird> = emptyList(),
    val feedingHistory: List<FeedingRecord> = emptyList(),
    val lastFeeding: FeedingRecord? = null
)

class BirdDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BirdRepository = (application as BirdNurseryApp).repository

    private val _uiState = MutableStateFlow(BirdDetailUiState())
    val uiState: StateFlow<BirdDetailUiState> = _uiState.asStateFlow()

    fun loadBird(birdId: Long) {
        viewModelScope.launch {
            repository.getAllBirds().collect { birds ->
                val bird = birds.find { it.id == birdId }
                _uiState.update {
                    it.copy(
                        bird = bird,
                        mother = birds.find { parent -> parent.id == bird?.motherId },
                        father = birds.find { parent -> parent.id == bird?.fatherId },
                        children = birds.filter { child ->
                            child.motherId == birdId || child.fatherId == birdId
                        }
                    )
                }
            }
        }
        viewModelScope.launch {
            repository.getFeedingHistory(birdId).collect { history ->
                _uiState.update { it.copy(feedingHistory = history) }
            }
        }
        viewModelScope.launch {
            repository.getLastFeeding(birdId).collect { record ->
                _uiState.update { it.copy(lastFeeding = record) }
            }
        }
    }

    fun feedBird(birdId: Long) {
        viewModelScope.launch {
            repository.recordFeeding(birdId)
        }
    }

    fun deleteBird() {
        viewModelScope.launch {
            _uiState.value.bird?.let { repository.deleteBird(it) }
        }
    }
}
