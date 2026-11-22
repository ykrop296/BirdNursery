package com.example.birdnursery.ui.screens.birddetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.data.model.feedingLevel
import com.example.birdnursery.data.model.formatRemainingTime
import com.example.birdnursery.data.model.hasHatched
import com.example.birdnursery.theme.BeigeLight
import com.example.birdnursery.theme.BeigeMedium
import com.example.birdnursery.theme.BrownDark
import com.example.birdnursery.theme.BrownGold
import com.example.birdnursery.theme.BrownMedium
import com.example.birdnursery.theme.FeedingFed
import com.example.birdnursery.theme.FeedingHungry
import com.example.birdnursery.ui.components.FeederView
import com.example.birdnursery.ui.components.HatchingEggView
import com.example.birdnursery.ui.components.drawDetailedBird
import com.example.birdnursery.ui.components.rememberCurrentTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirdDetailScreen(
    birdId: Long,
    onNavigateBack: () -> Unit,
    onEditBird: (Long) -> Unit,
    onBirdClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BirdDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val now = rememberCurrentTime(1_000L)

    LaunchedEffect(birdId) {
        viewModel.loadBird(birdId)
    }

    val bird = uiState.bird

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(bird?.name ?: "Птица", color = BrownDark, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = BrownDark)
                    }
                },
                actions = {
                    IconButton(onClick = { bird?.let { onEditBird(it.id) } }) {
                        Icon(Icons.Default.Edit, "Редактировать", tint = BrownGold)
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, "Удалить", tint = FeedingHungry)
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
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BeigeMedium),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .padding(12.dp)
                    ) {
                        drawDetailedBird(
                            headColor = Color(bird.headColor),
                            cheekColor = Color(bird.cheekColor),
                            throatColor = Color(bird.throatColor),
                            bodyColor = Color(bird.bodyColor),
                            chestColor = Color(bird.chestColor),
                            bellyColor = Color(bird.bellyColor),
                            wingColor = Color(bird.wingColor),
                            tailColor = Color(bird.tailColor),
                            legColor = Color(bird.legColor),
                            beakColor = Color(bird.beakColor),
                            isLarge = true
                        )
                    }
                }

                bird.expectedHatchDate?.let { hatchDate ->
                    val hatched = hasHatched(hatchDate, now)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BeigeMedium)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HatchingEggView(
                                expectedHatchDate = hatchDate,
                                now = now,
                                modifier = Modifier.size(width = 130.dp, height = 160.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (hatched) "Птенец вылупился!" else "Скоро вылупление",
                                    fontWeight = FontWeight.Bold,
                                    color = BrownDark,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    if (hatched) "Яйцо раскрылось, ${bird.name} уже в гнезде"
                                    else "Осталось ${formatRemainingTime(hatchDate - now)}",
                                    color = if (hatched) FeedingFed else BrownMedium,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Примерная дата: ${formatHatchDate(hatchDate)}",
                                    color = BrownMedium,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                val level = feedingLevel(
                    lastFeedingTime = uiState.lastFeeding?.feedingTime,
                    feedingIntervalHours = bird.feedingIntervalHours,
                    now = now
                )
                val needsFeeding = level <= 0f
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BeigeMedium)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("Кормушка", fontWeight = FontWeight.Bold, color = BrownDark, fontSize = 18.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FeederView(
                                level = level,
                                modifier = Modifier.size(width = 110.dp, height = 140.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (needsFeeding) "Корм закончился — пора кормить"
                                    else "Корма хватит ещё на ${formatRemainingTime((bird.feedingIntervalHours * 3_600_000L) - (now - (uiState.lastFeeding?.feedingTime ?: now)))}",
                                    color = if (needsFeeding) FeedingHungry else FeedingFed,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                uiState.lastFeeding?.let { feeding ->
                                    Text(
                                        "Последнее кормление: ${formatDateTime(feeding.feedingTime)}",
                                        color = BrownMedium,
                                        fontSize = 12.sp
                                    )
                                } ?: Text("Ещё не кормили", color = BrownMedium, fontSize = 12.sp)
                                Text(
                                    "Интервал: каждые ${bird.feedingIntervalHours} ч",
                                    color = BrownMedium,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Button(
                            onClick = { viewModel.feedBird(bird.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (needsFeeding) FeedingHungry else FeedingFed,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (needsFeeding) "Насыпать корм" else "Покормить ещё раз", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BeigeMedium)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Информация", fontWeight = FontWeight.Bold, color = BrownDark, fontSize = 18.sp)
                        InfoRow("Имя", bird.name)
                        if (bird.species.isNotEmpty()) InfoRow("Вид", bird.species)
                        if (bird.sex.isNotEmpty()) InfoRow("Пол", bird.sex)
                        if (bird.weight.isNotEmpty()) InfoRow("Вес", "${bird.weight} г")
                        if (bird.birthDate.isNotEmpty()) InfoRow("Дата рождения", bird.birthDate)
                        if (bird.arrivalDate.isNotEmpty()) InfoRow("Дата привоза", bird.arrivalDate)
                        if (bird.diseases.isNotEmpty()) InfoRow("Болезни", bird.diseases)
                        if (bird.notes.isNotEmpty()) InfoRow("Заметки", bird.notes)
                    }
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BeigeMedium)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Семейное дерево",
                            fontWeight = FontWeight.Bold,
                            color = BrownDark,
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            FamilyMemberCard(
                                relation = "Мать",
                                relative = uiState.mother,
                                onClick = onBirdClick,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FamilyMemberCard(
                                relation = "Отец",
                                relative = uiState.father,
                                onClick = onBirdClick,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        TreeConnector()
                        FamilyMemberCard(
                            relation = "Эта птица",
                            relative = bird,
                            onClick = {},
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (uiState.children.isNotEmpty()) {
                            TreeConnector()
                            Text("Дети", color = BrownMedium, fontSize = 12.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                uiState.children.forEach { child ->
                                    FamilyMemberCard(
                                        relation = when (child.sex) {
                                            "Самка" -> "Дочь"
                                            "Самец" -> "Сын"
                                            else -> "Птенец"
                                        },
                                        relative = child,
                                        onClick = onBirdClick,
                                        modifier = Modifier.width(130.dp)
                                    )
                                }
                            }
                        }
                        if (uiState.mother == null && uiState.father == null && uiState.children.isEmpty()) {
                            Text(
                                "Родственники пока не указаны",
                                color = BrownMedium,
                                fontSize = 13.sp
                            )
                            TextButton(onClick = { onEditBird(bird.id) }) {
                                Text("Настроить семью", color = BrownGold)
                            }
                        }
                    }
                }

                if (uiState.feedingHistory.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BeigeMedium)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text("История кормлений", fontWeight = FontWeight.Bold, color = BrownDark, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            uiState.feedingHistory.take(10).forEach { record ->
                                Text(
                                    "• ${formatDateTime(record.feedingTime)}",
                                    color = BrownMedium,
                                    fontSize = 13.sp
                                )
                            }
                            if (uiState.feedingHistory.size > 10) {
                                Text(
                                    "И ещё ${uiState.feedingHistory.size - 10}",
                                    color = BrownMedium,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = BeigeLight,
            title = { Text("Удалить птицу?", color = BrownDark) },
            text = {
                Text(
                    "${bird?.name ?: "Птица"} будет удалена, а семейные связи с ней удалены",
                    color = BrownMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBird()
                    showDeleteConfirm = false
                    onNavigateBack()
                }) {
                    Text("Удалить", color = FeedingHungry, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Отмена", color = BrownGold)
                }
            }
        )
    }
}

@Composable
private fun FamilyMemberCard(
    relation: String,
    relative: Bird?,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(enabled = relative != null) { relative?.let { onClick(it.id) } },
        shape = RoundedCornerShape(12.dp),
        color = BeigeLight
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(42.dp).background(
                    color = relative?.let { Color(it.headColor) } ?: BeigeMedium,
                    shape = CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    relative?.name?.take(1)?.uppercase() ?: "—",
                    color = BrownDark,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                relative?.name ?: if (relation == "Отец") "Не указан" else "Не указана",
                color = BrownDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(relation, color = BrownMedium, fontSize = 11.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun TreeConnector() {
    Box(modifier = Modifier.width(2.dp).height(20.dp).background(BrownGold.copy(alpha = 0.4f)))
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("$label: ", color = BrownMedium, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Text(value, color = BrownDark, fontSize = 14.sp)
    }
}

private fun formatDateTime(value: Long): String {
    return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(value))
}

private fun formatHatchDate(value: Long): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(value))
}
