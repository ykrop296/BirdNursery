package com.example.birdnursery

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.birdnursery.ui.screens.addbird.AddBirdScreen
import com.example.birdnursery.ui.screens.birddetail.BirdDetailScreen
import com.example.birdnursery.ui.screens.home.HomeScreen
import com.example.birdnursery.ui.screens.pedigree.PedigreeScreen

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Main)
    // Защита от пустого бэкстека после восстановления состояния (upgrade/process death)
    if (backStack.isEmpty()) {
        backStack.add(Main)
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Main> {
                HomeScreen(
                    onAddBird = { backStack.add(AddBird) },
                    onBirdClick = { birdId -> backStack.add(BirdDetail(birdId)) },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<AddBird> {
                AddBirdScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<BirdDetail> { key ->
                BirdDetailScreen(
                    birdId = key.birdId,
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onEditBird = { birdId -> backStack.add(EditBird(birdId)) },
                    onBirdClick = { relativeId -> backStack.add(BirdDetail(relativeId)) },
                    onShowTree = { birdId -> backStack.add(Pedigree(birdId)) },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<EditBird> { key ->
                AddBirdScreen(
                    birdId = key.birdId,
                    onNavigateBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<Pedigree> { key ->
                PedigreeScreen(
                    birdId = key.birdId,
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onBirdClick = { relativeId -> backStack.add(BirdDetail(relativeId)) },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
        },
    )
}
