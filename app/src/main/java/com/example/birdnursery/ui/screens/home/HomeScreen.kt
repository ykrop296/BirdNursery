package com.example.birdnursery.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.birdnursery.ui.components.drawDetailedBird
import com.example.birdnursery.ui.components.rememberCurrentTime
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.data.model.Cage
import com.example.birdnursery.data.model.FeedingRecord
import com.example.birdnursery.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddBird: () -> Unit,
    onBirdClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lastFeedings by viewModel.lastFeedings.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showMoveDialog by remember { mutableStateOf<Bird?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CageDrawerContent(
                cages = uiState.cages,
                selectedCageId = uiState.selectedCageId,
                onCageSelected = { cageId ->
                    viewModel.selectCage(cageId)
                    scope.launch { drawerState.close() }
                },
                onCreateCage = { name -> viewModel.createCage(name) },
                onDeleteCage = { cageId -> viewModel.deleteCage(cageId) }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            uiState.selectedCageName,
                            fontWeight = FontWeight.Bold,
                            color = BrownDark
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Клетки", tint = BrownDark)
                        }
                    },
                    actions = {
                        TextButton(onClick = { scope.launch { drawerState.open() } }) {
                            Text("🏠 Клетки", color = BrownDark, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BeigeMedium
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddBird,
                    containerColor = BrownGold,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить птицу")
                }
            },
            containerColor = BeigeLight
        ) { paddingValues ->
            if (uiState.birds.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🐦", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Пока нет птиц",
                            style = MaterialTheme.typography.titleLarge,
                            color = BrownDark
                        )
                        Text(
                            "Нажмите + чтобы добавить",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BrownMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.birds, key = { it.id }) { bird ->
                        BirdCardItem(
                            bird = bird,
                            lastFeeding = lastFeedings[bird.id],
                            onClick = { onBirdClick(bird.id) },
                            onLongClick = { showMoveDialog = bird },
                            onFeedClick = { viewModel.feedBird(bird.id) }
                        )
                    }
                }
            }
        }
    }

    // Move bird dialog
    showMoveDialog?.let { bird ->
        MoveBirdDialog(
            bird = bird,
            cages = uiState.cages,
            currentCageId = bird.cageId,
            onMove = { cageId ->
                viewModel.moveBirdToCage(bird.id, cageId)
                showMoveDialog = null
            },
            onDelete = {
                viewModel.deleteBird(bird)
                showMoveDialog = null
            },
            onDismiss = { showMoveDialog = null }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BirdCardItem(
    bird: Bird,
    lastFeeding: FeedingRecord?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFeedClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BeigeMedium),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BeigeDark))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bird icon
            Canvas(modifier = Modifier.size(68.dp)) {
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
                    isLarge = false
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Bird info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bird.name.ifEmpty { "Без имени" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BrownDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (bird.species.isNotEmpty()) {
                    Text(
                        text = bird.species,
                        fontSize = 13.sp,
                        color = BrownMedium,
                        maxLines = 1
                    )
                }
                Row {
                    if (bird.sex.isNotEmpty()) {
                        Text(
                            text = bird.sex,
                            fontSize = 12.sp,
                            color = BrownMedium
                        )
                    }
                    if (bird.weight.isNotEmpty()) {
                        if (bird.sex.isNotEmpty()) Text(" · ", fontSize = 12.sp, color = BrownMedium)
                        Text(
                            text = "${bird.weight}г",
                            fontSize = 12.sp,
                            color = BrownMedium
                        )
                    }
                    if (bird.birthDate.isNotEmpty()) {
                        val age = calculateAge(bird.birthDate)
                        if (age.isNotEmpty()) {
                            Text(" · ", fontSize = 12.sp, color = BrownMedium)
                            Text(
                                text = age,
                                fontSize = 12.sp,
                                color = BrownMedium
                            )
                        }
                    }
                }
            }

            // Feeding button
            FeedingButtonCompact(
                lastFeedingTime = lastFeeding?.feedingTime,
                feedingIntervalHours = bird.feedingIntervalHours,
                onFeedClick = onFeedClick
            )
        }
    }
}

@Composable
private fun FeedingButtonCompact(
    lastFeedingTime: Long?,
    feedingIntervalHours: Int,
    onFeedClick: () -> Unit
) {
    val now = rememberCurrentTime()
    val needsFeeding = lastFeedingTime == null ||
            (now - lastFeedingTime) > feedingIntervalHours * 3600_000L

    val bgColor by animateColorAsState(
        if (needsFeeding) FeedingHungry else FeedingFed,
        label = "feedingColor"
    )

    val text = if (needsFeeding) {
        "Покормить!"
    } else {
        val hoursAgo = ((now - (lastFeedingTime ?: now)) / 3600_000L).toInt()
        val minsAgo = ((now - (lastFeedingTime ?: now)) / 60_000L).toInt()
        if (hoursAgo > 0) "${hoursAgo}ч назад" else "${minsAgo}мин"
    }

    Button(
        onClick = onFeedClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MoveBirdDialog(
    bird: Bird,
    cages: List<Cage>,
    currentCageId: Long?,
    onMove: (Long?) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BeigeLight,
        title = { Text("${bird.name}", color = BrownDark) },
        text = {
            Column {
                Text("Переместить в клетку:", color = BrownDark, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))

                // "No cage" option
                TextButton(
                    onClick = { onMove(null) },
                    enabled = currentCageId != null
                ) {
                    Text("📦 Без клетки", color = if (currentCageId != null) BrownGold else BrownMedium)
                }

                cages.forEach { cage ->
                    TextButton(
                        onClick = { onMove(cage.id) },
                        enabled = cage.id != currentCageId
                    ) {
                        Text(
                            "🏠 ${cage.name}",
                            color = if (cage.id != currentCageId) BrownGold else BrownMedium
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BeigeDark)

                TextButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = FeedingHungry)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Удалить птицу", color = FeedingHungry)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть", color = BrownGold)
            }
        }
    )
}

@Composable
private fun CageDrawerContent(
    cages: List<Cage>,
    selectedCageId: Long?,
    onCageSelected: (Long?) -> Unit,
    onCreateCage: (String) -> Unit,
    onDeleteCage: (Long) -> Unit
) {
    var newCageName by remember { mutableStateOf("") }

    ModalDrawerSheet(
        drawerContainerColor = BeigeLight
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "🐦 Клетки",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = BrownDark
        )
        HorizontalDivider(color = BeigeDark, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))

        // "All birds" option
        NavigationDrawerItem(
            label = { Text("Все птицы", color = BrownDark) },
            selected = selectedCageId == null,
            onClick = { onCageSelected(null) },
            icon = { Text("📋") },
            modifier = Modifier.padding(horizontal = 12.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = BeigeMedium,
                unselectedContainerColor = Color.Transparent
            )
        )

        cages.forEach { cage ->
            NavigationDrawerItem(
                label = { Text(cage.name, color = BrownDark) },
                selected = selectedCageId == cage.id,
                onClick = { onCageSelected(cage.id) },
                icon = { Text("🏠") },
                badge = {
                    IconButton(
                        onClick = { onDeleteCage(cage.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = BrownMedium,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = BeigeMedium,
                    unselectedContainerColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Create cage section
        HorizontalDivider(color = BeigeDark, modifier = Modifier.padding(horizontal = 16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newCageName,
                onValueChange = { newCageName = it },
                placeholder = { Text("Новая клетка...", color = BrownMedium) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrownGold,
                    unfocusedBorderColor = BeigeDark,
                    cursorColor = BrownGold
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (newCageName.isNotBlank()) {
                        onCreateCage(newCageName.trim())
                        newCageName = ""
                    }
                },
                enabled = newCageName.isNotBlank()
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Создать клетку",
                    tint = if (newCageName.isNotBlank()) BrownGold else BrownMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}



private fun calculateAge(birthDateStr: String): String {
    return try {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val birthDate = sdf.parse(birthDateStr) ?: return ""
        val now = Calendar.getInstance()
        val birth = Calendar.getInstance().apply { time = birthDate }
        val years = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        val months = now.get(Calendar.MONTH) - birth.get(Calendar.MONTH)
        val totalMonths = years * 12 + months
        when {
            totalMonths < 1 -> {
                val days = ((now.timeInMillis - birth.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                "${days}дн"
            }
            totalMonths < 12 -> "${totalMonths}мес"
            else -> {
                val y = totalMonths / 12
                val m = totalMonths % 12
                if (m > 0) "${y}г ${m}мес" else "${y}г"
            }
        }
    } catch (e: Exception) {
        ""
    }
}
