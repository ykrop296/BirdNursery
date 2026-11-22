package com.example.birdnursery.ui.screens.addbird

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.birdnursery.BirdNurseryApp
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.data.model.Cage
import com.example.birdnursery.data.repository.BirdRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddBirdUiState(
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
    val childIds: Set<Long> = emptySet(),
    val previousChildIds: Set<Long> = emptySet(),
    val expectedHatchDate: Long? = null,
    val headColor: Int = Bird().headColor,
    val cheekColor: Int = Bird().cheekColor,
    val throatColor: Int = Bird().throatColor,
    val bodyColor: Int = Bird().bodyColor,
    val chestColor: Int = Bird().chestColor,
    val bellyColor: Int = Bird().bellyColor,
    val wingColor: Int = Bird().wingColor,
    val tailColor: Int = Bird().tailColor,
    val legColor: Int = Bird().legColor,
    val beakColor: Int = Bird().beakColor,
    val cages: List<Cage> = emptyList(),
    val birds: List<Bird> = emptyList(),
    val isEditing: Boolean = false,
    val editBirdId: Long = 0,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class AddBirdViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BirdRepository = (application as BirdNurseryApp).repository

    private val _uiState = MutableStateFlow(AddBirdUiState())
    val uiState: StateFlow<AddBirdUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllCages().collect { cages ->
                _uiState.update { it.copy(cages = cages) }
            }
        }
        viewModelScope.launch {
            repository.getAllBirds().collect { birds ->
                _uiState.update { it.copy(birds = birds) }
            }
        }
    }

    fun initOrReset(birdId: Long?) {
        val currentCages = _uiState.value.cages
        val currentBirds = _uiState.value.birds
        if (birdId != null && birdId > 0) {
            viewModelScope.launch {
                val bird = repository.getBirdById(birdId) ?: return@launch
                val children = repository.getChildrenOf(birdId).map { it.id }.toSet()
                _uiState.update {
                    it.copy(
                        name = bird.name,
                        species = bird.species,
                        birthDate = bird.birthDate,
                        arrivalDate = bird.arrivalDate,
                        weight = bird.weight,
                        sex = bird.sex,
                        diseases = bird.diseases,
                        notes = bird.notes,
                        feedingIntervalHours = bird.feedingIntervalHours,
                        cageId = bird.cageId,
                        photoUri = bird.photoUri,
                        motherId = bird.motherId,
                        fatherId = bird.fatherId,
                        childIds = children,
                        previousChildIds = children,
                        expectedHatchDate = bird.expectedHatchDate,
                        headColor = bird.headColor,
                        cheekColor = bird.cheekColor,
                        throatColor = bird.throatColor,
                        bodyColor = bird.bodyColor,
                        chestColor = bird.chestColor,
                        bellyColor = bird.bellyColor,
                        wingColor = bird.wingColor,
                        tailColor = bird.tailColor,
                        legColor = bird.legColor,
                        beakColor = bird.beakColor,
                        isEditing = true,
                        editBirdId = bird.id,
                        isSaving = false,
                        errorMessage = null
                    )
                }
            }
        } else {
            _uiState.value = AddBirdUiState(cages = currentCages, birds = currentBirds)
        }
    }

    fun updateName(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun updateSpecies(value: String) = _uiState.update { it.copy(species = value) }
    fun updateBirthDate(value: String) = _uiState.update { it.copy(birthDate = value) }
    fun updateArrivalDate(value: String) = _uiState.update { it.copy(arrivalDate = value) }
    fun updateWeight(value: String) = _uiState.update { it.copy(weight = value) }
    fun updateSex(value: String) = _uiState.update { it.copy(sex = value, errorMessage = null) }
    fun updateDiseases(value: String) = _uiState.update { it.copy(diseases = value) }
    fun updateNotes(value: String) = _uiState.update { it.copy(notes = value) }
    fun updateFeedingInterval(value: Int) = _uiState.update { it.copy(feedingIntervalHours = value) }
    fun updateCageId(value: Long?) = _uiState.update { it.copy(cageId = value) }
    fun updatePhotoUri(value: String?) = _uiState.update { it.copy(photoUri = value) }
    fun updateMotherId(value: Long?) = _uiState.update { it.copy(motherId = value) }
    fun updateFatherId(value: Long?) = _uiState.update { it.copy(fatherId = value) }
    fun updateExpectedHatchDate(value: Long?) = _uiState.update { it.copy(expectedHatchDate = value) }

    fun toggleChild(birdId: Long) {
        _uiState.update { state ->
            val children = state.childIds.toMutableSet().apply {
                if (!add(birdId)) remove(birdId)
            }
            state.copy(childIds = children, errorMessage = null)
        }
    }

    fun updatePartColor(part: String, color: Int) {
        _uiState.update {
            when (part) {
                "head" -> it.copy(headColor = color)
                "cheeks" -> it.copy(cheekColor = color)
                "throat" -> it.copy(throatColor = color)
                "body" -> it.copy(bodyColor = color)
                "chest" -> it.copy(chestColor = color)
                "belly" -> it.copy(bellyColor = color)
                "wing" -> it.copy(wingColor = color)
                "tail" -> it.copy(tailColor = color)
                "legs" -> it.copy(legColor = color)
                "beak" -> it.copy(beakColor = color)
                else -> it
            }
        }
    }

    fun saveBird(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isSaving || state.name.isBlank()) return
        if (state.childIds.isNotEmpty() && state.sex !in listOf("Самец", "Самка")) {
            _uiState.update { it.copy(errorMessage = "Чтобы добавить детей, укажите пол этой птицы") }
            return
        }
        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            val bird = Bird(
                id = if (state.isEditing) state.editBirdId else 0,
                name = state.name.trim(),
                species = state.species.trim(),
                birthDate = state.birthDate.trim(),
                arrivalDate = state.arrivalDate.trim(),
                weight = state.weight.trim(),
                sex = state.sex,
                diseases = state.diseases.trim(),
                notes = state.notes.trim(),
                feedingIntervalHours = state.feedingIntervalHours,
                cageId = state.cageId,
                photoUri = state.photoUri,
                motherId = state.motherId,
                fatherId = state.fatherId,
                expectedHatchDate = state.expectedHatchDate,
                headColor = state.headColor,
                cheekColor = state.cheekColor,
                throatColor = state.throatColor,
                bodyColor = state.bodyColor,
                chestColor = state.chestColor,
                bellyColor = state.bellyColor,
                wingColor = state.wingColor,
                tailColor = state.tailColor,
                legColor = state.legColor,
                beakColor = state.beakColor
            )
            repository.saveWithChildren(
                bird = bird,
                childIds = state.childIds,
                previousChildIds = state.previousChildIds,
                asFather = state.sex == "Самец"
            )
            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}
