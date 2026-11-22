package com.example.birdnursery.ui.screens.addbird

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.birdnursery.theme.*
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.ui.components.InteractiveBirdView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBirdScreen(
    birdId: Long? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddBirdViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showColorPicker by remember { mutableStateOf<String?>(null) }
    var showParentPicker by remember { mutableStateOf<String?>(null) }
    var showChildrenPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Initialize or reset on entry
    LaunchedEffect(birdId) {
        viewModel.initOrReset(birdId)
    }

    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updatePhotoUri(it.toString()) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditing) "Редактирование птицы" else "Новая птица",
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
                    TextButton(
                        onClick = { viewModel.saveBird { onNavigateBack() } },
                        enabled = uiState.name.isNotBlank() && !uiState.isSaving
                    ) {
                        Text(
                            "Готово",
                            color = if (uiState.name.isNotBlank()) BrownGold else BrownMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BeigeMedium)
            )
        },
        containerColor = BeigeLight
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Interactive Bird Coloring Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BeigeMedium),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "🎨 Окрас птицы",
                        fontWeight = FontWeight.Bold,
                        color = BrownDark,
                        fontSize = 17.sp
                    )
                    Text(
                        "Нажмите на часть птицы или кнопку внизу для выбора цвета",
                        color = BrownMedium,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // High Quality Interactive Bird Canvas
                    InteractiveBirdView(
                        headColor = Color(uiState.headColor),
                        cheekColor = Color(uiState.cheekColor),
                        throatColor = Color(uiState.throatColor),
                        bodyColor = Color(uiState.bodyColor),
                        chestColor = Color(uiState.chestColor),
                        bellyColor = Color(uiState.bellyColor),
                        wingColor = Color(uiState.wingColor),
                        tailColor = Color(uiState.tailColor),
                        legColor = Color(uiState.legColor),
                        beakColor = Color(uiState.beakColor),
                        onPartTapped = { part -> showColorPicker = part },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Part Selection Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PartColorChip("Голова", Color(uiState.headColor)) { showColorPicker = "head" }
                        PartColorChip("Щёки", Color(uiState.cheekColor)) { showColorPicker = "cheeks" }
                        PartColorChip("Горло", Color(uiState.throatColor)) { showColorPicker = "throat" }
                        PartColorChip("Грудь", Color(uiState.chestColor)) { showColorPicker = "chest" }
                        PartColorChip("Живот", Color(uiState.bellyColor)) { showColorPicker = "belly" }
                        PartColorChip("Крыло", Color(uiState.wingColor)) { showColorPicker = "wing" }
                        PartColorChip("Спина", Color(uiState.bodyColor)) { showColorPicker = "body" }
                        PartColorChip("Хвост", Color(uiState.tailColor)) { showColorPicker = "tail" }
                        PartColorChip("Клюв", Color(uiState.beakColor)) { showColorPicker = "beak" }
                        PartColorChip("Лапки", Color(uiState.legColor)) { showColorPicker = "legs" }
                    }
                }
            }

            // Photo section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BeigeMedium)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📷 Фото птицы (по желанию)", fontWeight = FontWeight.Bold, color = BrownDark)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.photoUri != null) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BeigeDark.copy(alpha = 0.3f))
                                .border(2.dp, BrownGold, RoundedCornerShape(12.dp))
                                .clickable { photoLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📸", fontSize = 32.sp)
                                Text("Фото выбрано", fontSize = 12.sp, color = BrownDark, fontWeight = FontWeight.Medium)
                                Text("Нажмите для замены", fontSize = 10.sp, color = BrownMedium)
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { photoLauncher.launch("image/*") },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrownGold),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = androidx.compose.ui.graphics.SolidColor(BrownGold)
                            )
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Выбрать фото из галереи")
                        }
                    }
                }
            }

            // Bird info form
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BeigeMedium)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("📋 Основные данные", fontWeight = FontWeight.Bold, color = BrownDark, fontSize = 17.sp)

                    // Name
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = { Text("Имя / Кличка птицы *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = formTextFieldColors()
                    )

                    // Species
                    OutlinedTextField(
                        value = uiState.species,
                        onValueChange = { viewModel.updateSpecies(it) },
                        label = { Text("Вид / Порода (например: Волнистый попугай)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = formTextFieldColors()
                    )

                    // Sex selection
                    Text("Пол:", color = BrownDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Самец", "Самка", "Не определён").forEach { sexOption ->
                            FilterChip(
                                selected = uiState.sex == sexOption,
                                onClick = { viewModel.updateSex(sexOption) },
                                label = { Text(sexOption) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrownGold,
                                    selectedLabelColor = Color.White,
                                    containerColor = BeigeLight,
                                    labelColor = BrownDark
                                )
                            )
                        }
                    }

                    // Weight
                    OutlinedTextField(
                        value = uiState.weight,
                        onValueChange = { viewModel.updateWeight(it) },
                        label = { Text("Вес (в граммах)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = formTextFieldColors()
                    )

                    // Birth date
                    OutlinedTextField(
                        value = uiState.birthDate,
                        onValueChange = { viewModel.updateBirthDate(it) },
                        label = { Text("Дата рождения (ДД.ММ.ГГГГ)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = formTextFieldColors()
                    )

                    // Arrival date
                    OutlinedTextField(
                        value = uiState.arrivalDate,
                        onValueChange = { viewModel.updateArrivalDate(it) },
                        label = { Text("Дата привоза в питомник (ДД.ММ.ГГГГ)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = formTextFieldColors()
                    )

                    // Feeding interval
                    Text(
                        "Интервал кормления: каждые ${uiState.feedingIntervalHours} ч",
                        color = BrownDark,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2, 4, 6, 8, 12, 24).forEach { hours ->
                            FilterChip(
                                selected = uiState.feedingIntervalHours == hours,
                                onClick = { viewModel.updateFeedingInterval(hours) },
                                label = { Text("${hours}ч") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrownGold,
                                    selectedLabelColor = Color.White,
                                    containerColor = BeigeLight,
                                    labelColor = BrownDark
                                )
                            )
                        }
                    }

                    // Cage selection
                    Text("Клетка / Папка:", color = BrownDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.cageId == null,
                            onClick = { viewModel.updateCageId(null) },
                            label = { Text("Без клетки") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrownGold,
                                selectedLabelColor = Color.White,
                                containerColor = BeigeLight,
                                labelColor = BrownDark
                            )
                        )
                        uiState.cages.forEach { cage ->
                            FilterChip(
                                selected = uiState.cageId == cage.id,
                                onClick = { viewModel.updateCageId(cage.id) },
                                label = { Text("🏠 ${cage.name}") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrownGold,
                                    selectedLabelColor = Color.White,
                                    containerColor = BeigeLight,
                                    labelColor = BrownDark
                                )
                            )
                        }
                    }

                    // Diseases
                    OutlinedTextField(
                        value = uiState.diseases,
                        onValueChange = { viewModel.updateDiseases(it) },
                        label = { Text("Перенесённые болезни / Особенности") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = formTextFieldColors()
                    )

                    // Notes
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = { viewModel.updateNotes(it) },
                        label = { Text("Дополнительные заметки") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = formTextFieldColors()
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BeigeMedium)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Семейное дерево", fontWeight = FontWeight.Bold, color = BrownDark, fontSize = 17.sp)
                    Text(
                        "Свяжите птицу с родителями и уже добавленными детьми",
                        color = BrownMedium,
                        fontSize = 13.sp
                    )
                    ParentSelector(
                        label = "Мать",
                        selectedName = uiState.birds.find { it.id == uiState.motherId }?.name,
                        onClick = { showParentPicker = "mother" }
                    )
                    ParentSelector(
                        label = "Отец",
                        selectedName = uiState.birds.find { it.id == uiState.fatherId }?.name,
                        onClick = { showParentPicker = "father" }
                    )
                    OutlinedButton(
                        onClick = { showChildrenPicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrownGold)
                    ) {
                        Text(if (uiState.childIds.isEmpty()) "Выбрать детей" else "Дети: ${uiState.childIds.size}")
                    }
                    if (uiState.childIds.isNotEmpty()) {
                        Text(
                            uiState.birds.filter { it.id in uiState.childIds }.joinToString { it.name },
                            color = BrownDark,
                            fontSize = 14.sp
                        )
                        if (uiState.sex !in listOf("Самец", "Самка")) {
                            Text(
                                "Для сохранения связи с детьми укажите пол птицы выше",
                                color = FeedingHungry,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BeigeMedium)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Яйцо в гнезде", fontWeight = FontWeight.Bold, color = BrownDark, fontSize = 17.sp)
                            Text(
                                "В выбранную дату яйцо вылупится в птенца",
                                color = BrownMedium,
                                fontSize = 13.sp
                            )
                        }
                        Switch(
                            checked = uiState.expectedHatchDate != null,
                            onCheckedChange = { enabled ->
                                if (enabled) showDatePicker = true else viewModel.updateExpectedHatchDate(null)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BrownGold
                            )
                        )
                    }
                    if (uiState.expectedHatchDate != null) {
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrownGold)
                        ) {
                            Text("Вылупление: ${formatHatchDate(uiState.expectedHatchDate!!)}")
                        }
                    }
                }
            }

            uiState.errorMessage?.let { error ->
                Text(error, color = FeedingHungry, fontSize = 14.sp)
            }

            // Save button
            Button(
                onClick = { viewModel.saveBird { onNavigateBack() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = uiState.name.isNotBlank() && !uiState.isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrownGold,
                    contentColor = Color.White,
                    disabledContainerColor = BeigeDark
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(
                        if (uiState.isEditing) "💾 Сохранить изменения" else "➕ Добавить птицу",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Color picker dialog
    showColorPicker?.let { part ->
        ColorPickerDialogInline(
            title = when (part) {
                "head" -> "Цвет головы"
                "cheeks" -> "Цвет щёк"
                "throat" -> "Цвет горла и нагрудника"
                "chest" -> "Цвет груди"
                "belly" -> "Цвет живота"
                "body" -> "Цвет спинки / тела"
                "wing" -> "Цвет крыла"
                "tail" -> "Цвет хвоста"
                "legs" -> "Цвет лапок и ветки"
                "beak" -> "Цвет клюва"
                else -> "Выберите цвет"
            },
            currentColor = Color(
                when (part) {
                    "head" -> uiState.headColor
                    "cheeks" -> uiState.cheekColor
                    "throat" -> uiState.throatColor
                    "chest" -> uiState.chestColor
                    "belly" -> uiState.bellyColor
                    "body" -> uiState.bodyColor
                    "wing" -> uiState.wingColor
                    "tail" -> uiState.tailColor
                    "legs" -> uiState.legColor
                    "beak" -> uiState.beakColor
                    else -> 0
                }
            ),
            onColorSelected = { color ->
                viewModel.updatePartColor(part, color.toArgb())
                showColorPicker = null
            },
            onDismiss = { showColorPicker = null }
        )
    }

    showParentPicker?.let { role ->
        val selectedId = if (role == "mother") uiState.motherId else uiState.fatherId
        val otherParentId = if (role == "mother") uiState.fatherId else uiState.motherId
        BirdPickerDialog(
            title = if (role == "mother") "Выберите мать" else "Выберите отца",
            birds = uiState.birds.filter {
                it.id != uiState.editBirdId && it.id != otherParentId && it.id !in uiState.childIds
            },
            selectedId = selectedId,
            onSelect = { id ->
                if (role == "mother") viewModel.updateMotherId(id) else viewModel.updateFatherId(id)
                showParentPicker = null
            },
            onDismiss = { showParentPicker = null }
        )
    }

    if (showChildrenPicker) {
        ChildrenPickerDialog(
            birds = uiState.birds.filter {
                it.id != uiState.editBirdId && it.id != uiState.motherId && it.id != uiState.fatherId
            },
            selectedIds = uiState.childIds,
            onToggle = viewModel::toggleChild,
            onDismiss = { showChildrenPicker = false }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.expectedHatchDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateExpectedHatchDate(
                        datePickerState.selectedDateMillis ?: uiState.expectedHatchDate
                    )
                    showDatePicker = false
                }) {
                    Text("Выбрать", color = BrownGold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена", color = BrownMedium)
                }
            }
        ) {
            DatePicker(state = datePickerState, showModeToggle = false)
        }
    }
}

@Composable
private fun ParentSelector(label: String, selectedName: String?, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrownGold)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("$label: ", fontWeight = FontWeight.Medium)
            Text(selectedName ?: if (label == "Отец") "не указан" else "не указана", color = BrownMedium)
        }
    }
}

@Composable
private fun BirdPickerDialog(
    title: String,
    birds: List<Bird>,
    selectedId: Long?,
    onSelect: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BeigeLight,
        title = { Text(title, color = BrownDark) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 360.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(null) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selectedId == null, onClick = { onSelect(null) })
                    Text("Не указана", color = BrownDark)
                }
                birds.forEach { bird ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(bird.id) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedId == bird.id, onClick = { onSelect(bird.id) })
                        Column {
                            Text(bird.name, color = BrownDark)
                            if (bird.species.isNotBlank()) {
                                Text(bird.species, color = BrownMedium, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Закрыть", color = BrownGold) }
        }
    )
}

@Composable
private fun ChildrenPickerDialog(
    birds: List<Bird>,
    selectedIds: Set<Long>,
    onToggle: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BeigeLight,
        title = { Text("Дети птицы", color = BrownDark) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 360.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (birds.isEmpty()) {
                    Text("Сначала добавьте других птиц", color = BrownMedium)
                }
                birds.forEach { bird ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggle(bird.id) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = bird.id in selectedIds,
                            onCheckedChange = { onToggle(bird.id) }
                        )
                        Column {
                            Text(bird.name, color = BrownDark)
                            if (bird.species.isNotBlank()) {
                                Text(bird.species, color = BrownMedium, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Готово", color = BrownGold) }
        }
    )
}

private fun formatHatchDate(value: Long): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(value))
}

@Composable
private fun PartColorChip(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = BeigeLight,
        border = BorderStroke(1.dp, BeigeDark),
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(1.dp, BrownDark.copy(alpha = 0.5f), CircleShape)
            )
            Text(label, fontSize = 13.sp, color = BrownDark, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun formTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BrownGold,
    unfocusedBorderColor = BeigeDark,
    focusedLabelColor = BrownGold,
    unfocusedLabelColor = BrownMedium,
    cursorColor = BrownGold,
    focusedTextColor = BrownDark,
    unfocusedTextColor = BrownDark
)

@Composable
private fun ColorPickerDialogInline(
    title: String,
    currentColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val palette = listOf(
        // Browns & Beiges
        Color(0xFFFFF5E6), Color(0xFFF5E6CC), Color(0xFFD4A574), Color(0xFFC49A6C),
        Color(0xFF8B6914), Color(0xFF7A5C3E), Color(0xFF4A3728), Color(0xFF2E1C0C),
        // Yellows & Oranges
        Color(0xFFFFEB3B), Color(0xFFFFC107), Color(0xFFFF9800), Color(0xFFFF5722),
        Color(0xFFFF8A65), Color(0xFFFFD54F), Color(0xFFFFCC80), Color(0xFFFFAB91),
        // Greens
        Color(0xFF8BC34A), Color(0xFF4CAF50), Color(0xFF2E7D32), Color(0xFF1B5E20),
        Color(0xFFAED581), Color(0xFF81C784), Color(0xFFC8E6C9), Color(0xFF558B2F),
        // Blues
        Color(0xFF03A9F4), Color(0xFF2196F3), Color(0xFF1565C0), Color(0xFF0D47A1),
        Color(0xFF81D4FA), Color(0xFF64B5F6), Color(0xFF00BCD4), Color(0xFF0097A7),
        // Reds & Pinks
        Color(0xFFE91E63), Color(0xFFF44336), Color(0xFFC2185B), Color(0xFFB71C1C),
        // Whites, Grays & Blacks
        Color(0xFFFFFFFF), Color(0xFFE0E0E0), Color(0xFF9E9E9E), Color(0xFF424242),
        Color(0xFF212121), Color(0xFF1A1A1A)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BeigeLight,
        title = { Text(title, color = BrownDark, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Выберите оттенок:", color = BrownMedium, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(260.dp)
                ) {
                    items(palette) { color ->
                        val isSelected = color == currentColor
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) BrownGold else Color.Gray.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                                .clickable { onColorSelected(color) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Выбрано",
                                    tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
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

private fun Color.luminance(): Float {
    return 0.299f * red + 0.587f * green + 0.114f * blue
}
