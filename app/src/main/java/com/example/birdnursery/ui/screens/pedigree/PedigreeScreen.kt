package com.example.birdnursery.ui.screens.pedigree

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.data.model.GouldianGenetics
import com.example.birdnursery.theme.BeigeLight
import com.example.birdnursery.theme.BeigeMedium
import com.example.birdnursery.theme.BrownDark
import com.example.birdnursery.theme.BrownGold
import com.example.birdnursery.theme.BrownMedium
import com.example.birdnursery.ui.components.GouldianBirdImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedigreeScreen(
    birdId: Long,
    onNavigateBack: () -> Unit,
    onBirdClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PedigreeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(birdId) {
        viewModel.load(birdId)
    }

    val bird = uiState.bird

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Дерево: ${bird?.name ?: "…"}",
                        color = BrownDark,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = BrownDark)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            bird?.let {
                                PedigreePrinter.print(
                                    context, it, uiState.ancestors, uiState.descendants
                                )
                            }
                        },
                        enabled = bird != null
                    ) {
                        Icon(Icons.Default.Print, "Распечатать", tint = BrownGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BeigeMedium)
            )
        },
        containerColor = BeigeLight
    ) { paddingValues ->
        if (bird == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrownGold)
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PedigreeSection(title = "Предки") {
                    if (uiState.ancestors.isEmpty()) {
                        Text("Не указаны", color = BrownMedium, fontSize = 13.sp)
                    } else {
                        uiState.ancestors.forEach { entry ->
                            PedigreeRow(entry, onBirdClick)
                        }
                    }
                }

                PedigreeSection(title = "Эта птица") {
                    PedigreeRow(
                        PedigreeEntry(bird, 0, bird.sex.ifBlank { "Птица" }),
                        onBirdClick = {},
                        highlighted = true
                    )
                }

                PedigreeSection(title = "Потомки") {
                    if (uiState.descendants.isEmpty()) {
                        Text("Нет", color = BrownMedium, fontSize = 13.sp)
                    } else {
                        uiState.descendants.forEach { entry ->
                            PedigreeRow(entry, onBirdClick)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PedigreeSection(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BeigeMedium)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = BrownDark, fontSize = 17.sp)
            content()
        }
    }
}

@Composable
private fun PedigreeRow(
    entry: PedigreeEntry,
    onBirdClick: (Long) -> Unit,
    highlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (entry.depth * 12).dp)
            .clickable(enabled = !highlighted) { onBirdClick(entry.bird.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        GouldianBirdImage(bird = entry.bird, modifier = Modifier.size(44.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "${entry.role}: ${entry.bird.name}",
                color = BrownDark,
                fontSize = 14.sp,
                fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Medium
            )
            Text(
                GouldianGenetics.phenotypeLabel(entry.bird),
                color = BrownMedium,
                fontSize = 12.sp
            )
        }
    }
}
