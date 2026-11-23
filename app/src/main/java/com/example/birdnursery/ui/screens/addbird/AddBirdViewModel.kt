package com.example.birdnursery.ui.screens.addbird

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.birdnursery.BirdNurseryApp
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.data.model.Cage
import com.example.birdnursery.data.model.GouldianGenetics
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
    val headGenotype: String = Bird().headGenotype,
    val breastGenotype: String = Bird().breastGenotype,
    val bodyGenotype: String = Bird().bodyGenotype,
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
                        headGenotype = bird.headGenotype,
                        breastGenotype = bird.breastGenotype,
                        bodyGenotype = bird.bodyGenotype,
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
    fun updateSex(value: String) = _uiState.update {
        val toFemale = GouldianGenetics.sexOf(value) == GouldianGenetics.Sex.FEMALE
        val fromFemale = GouldianGenetics.sexOf(it.sex) == GouldianGenetics.Sex.FEMALE
        if (toFemale == fromFemale) {
            it.copy(sex = value, errorMessage = null)
        } else {
            it.copy(
                sex = value,
                headGenotype = GouldianGenetics.convertHeadForSex(it.headGenotype, toFemale),
                bodyGenotype = GouldianGenetics.convertBodyForSex(it.bodyGenotype, toFemale),
                errorMessage = null
            )
        }
    }
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

    fun updateHeadGenotype(value: String) = _uiState.update { it.copy(headGenotype = value) }
    fun updateBreastGenotype(value: String) = _uiState.update { it.copy(breastGenotype = value) }
    fun updateBodyGenotype(value: String) = _uiState.update { it.copy(bodyGenotype = value) }

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
                headGenotype = state.headGenotype,
                breastGenotype = state.breastGenotype,
                bodyGenotype = state.bodyGenotype
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
