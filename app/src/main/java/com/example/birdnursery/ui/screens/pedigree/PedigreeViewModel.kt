package com.example.birdnursery.ui.screens.pedigree

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.birdnursery.BirdNurseryApp
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.data.repository.BirdRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PedigreeEntry(
    val bird: Bird,
    val depth: Int,
    val role: String
)

data class PedigreeUiState(
    val bird: Bird? = null,
    val ancestors: List<PedigreeEntry> = emptyList(),
    val descendants: List<PedigreeEntry> = emptyList()
)

class PedigreeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BirdRepository = (application as BirdNurseryApp).repository

    private val _uiState = MutableStateFlow(PedigreeUiState())
    val uiState: StateFlow<PedigreeUiState> = _uiState.asStateFlow()

    fun load(birdId: Long) {
        viewModelScope.launch {
            repository.getAllBirds().collect { birds ->
                val byId = birds.associateBy { it.id }
                val root = byId[birdId]
                _uiState.update {
                    it.copy(
                        bird = root,
                        ancestors = if (root != null) collectAncestors(root, byId) else emptyList(),
                        descendants = if (root != null) collectDescendants(root, birds) else emptyList()
                    )
                }
            }
        }
    }

    private fun parentRole(isFather: Boolean, depth: Int): String {
        return when (depth) {
            1 -> if (isFather) "Отец" else "Мать"
            2 -> if (isFather) "Дедушка" else "Бабушка"
            else -> if (isFather) "Прадед" else "Прабабушка"
        }
    }

    private fun childRole(bird: Bird, depth: Int): String {
        val male = bird.sex == "Самец"
        val female = bird.sex == "Самка"
        return when (depth) {
            1 -> if (male) "Сын" else if (female) "Дочь" else "Птенец"
            2 -> if (female) "Внучка" else "Внук"
            else -> if (female) "Правнучка" else "Правнук"
        }
    }

    private fun collectAncestors(root: Bird, byId: Map<Long, Bird>): List<PedigreeEntry> {
        val result = mutableListOf<PedigreeEntry>()
        val visited = mutableSetOf(root.id)

        fun visit(bird: Bird, depth: Int) {
            if (depth > 6) return
            listOfNotNull(
                bird.motherId?.let { it to false },
                bird.fatherId?.let { it to true }
            )
                .mapNotNull { (id, isFather) -> byId[id]?.let { it to isFather } }
                .filter { (parent, _) -> visited.add(parent.id) }
                .forEach { (parent, isFather) ->
                    result += PedigreeEntry(parent, depth, parentRole(isFather, depth))
                    visit(parent, depth + 1)
                }
        }
        visit(root, 1)
        return result.sortedWith(compareBy({ it.depth }, { it.role }, { it.bird.name }))
    }

    private fun collectDescendants(root: Bird, birds: List<Bird>): List<PedigreeEntry> {
        val result = mutableListOf<PedigreeEntry>()
        val visited = mutableSetOf(root.id)

        fun visit(bird: Bird, depth: Int) {
            if (depth > 6) return
            birds.filter { (it.motherId == bird.id || it.fatherId == bird.id) && visited.add(it.id) }
                .forEach { child ->
                    result += PedigreeEntry(child, depth, childRole(child, depth))
                    visit(child, depth + 1)
                }
        }
        visit(root, 1)
        return result.sortedWith(compareBy({ it.depth }, { it.bird.name }))
    }
}
