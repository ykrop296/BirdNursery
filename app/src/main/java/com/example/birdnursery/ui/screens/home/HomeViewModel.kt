package com.example.birdnursery.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.birdnursery.BirdNurseryApp
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.data.model.Cage
import com.example.birdnursery.data.model.FeedingRecord
import com.example.birdnursery.data.repository.BirdRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val birds: List<Bird> = emptyList(),
    val cages: List<Cage> = emptyList(),
    val selectedCageId: Long? = null,
    val selectedCageName: String = "Все птицы",
    val lastFeedings: Map<Long, FeedingRecord?> = emptyMap()
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BirdRepository = (application as BirdNurseryApp).repository

    private val _selectedCageId = MutableStateFlow<Long?>(null)

    private val _birds: Flow<List<Bird>> = _selectedCageId.flatMapLatest { cageId ->
        if (cageId == null) repository.getAllBirds()
        else repository.getBirdsByCage(cageId)
    }

    private val _cages = repository.getAllCages()

    val uiState: StateFlow<HomeUiState> = combine(
        _birds,
        _cages,
        _selectedCageId
    ) { birds, cages, selectedCageId ->
        val cageName = if (selectedCageId == null) "Все птицы"
        else cages.find { it.id == selectedCageId }?.name ?: "Все птицы"

        HomeUiState(
            birds = birds,
            cages = cages,
            selectedCageId = selectedCageId,
            selectedCageName = cageName
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeUiState()
    )

    // Track last feedings per bird
    private val _lastFeedings = MutableStateFlow<Map<Long, FeedingRecord?>>(emptyMap())
    val lastFeedings: StateFlow<Map<Long, FeedingRecord?>> = _lastFeedings.asStateFlow()

    init {
        // Collect birds and update feeding tracking
        viewModelScope.launch {
            _birds.collect { birds ->
                birds.forEach { bird ->
                    launch {
                        repository.getLastFeeding(bird.id).collect { record ->
                            _lastFeedings.update { it + (bird.id to record) }
                        }
                    }
                }
            }
        }
    }

    fun selectCage(cageId: Long?) {
        _selectedCageId.value = cageId
    }

    fun createCage(name: String) {
        viewModelScope.launch {
            val cage = Cage(name = name)
            repository.insertCage(cage)
        }
    }

    fun deleteCage(cageId: Long) {
        viewModelScope.launch {
            val cage = repository.getCageById(cageId) ?: return@launch
            repository.deleteCage(cage)
            if (_selectedCageId.value == cageId) {
                _selectedCageId.value = null
            }
        }
    }

    fun feedBird(birdId: Long) {
        viewModelScope.launch {
            repository.recordFeeding(birdId)
        }
    }

    fun moveBirdToCage(birdId: Long, cageId: Long?) {
        viewModelScope.launch {
            repository.moveBirdToCage(birdId, cageId)
        }
    }

    fun deleteBird(bird: Bird) {
        viewModelScope.launch {
            repository.deleteBird(bird)
        }
    }
}
