package com.example.birdnursery.ui.screens.addbird

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.birdnursery.theme.*
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.data.model.GouldianGenetics
import com.example.birdnursery.ui.components.GouldianAssetImage
import com.example.birdnursery.ui.components.GouldianBirdImage
import com.example.birdnursery.ui.components.GouldianImageCatalog
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
            // Genotype coloring card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BeigeMedium),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "🎨 Окрас птицы",
                        fontWeight = FontWeight.Bold,
                        color = BrownDark,
                        fontSize = 17.sp
                    )
                    Text(
                        "Окрас задаётся генотипом, как в калькуляторе gouldianfinches.eu",
                        color = BrownMedium,
                        fontSize = 12.sp
                    )

                    GouldianBirdImage(
                        headGenotype = uiState.headGenotype,
                        breastGenotype = uiState.breastGenotype,
                        bodyGenotype = uiState.bodyGenotype,
                        sexLabel = uiState.sex,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )

                    GenotypeDropdown(
                        label = "Голова",
                        options = GouldianGenetics.headOptions(uiState.sex),
                        selected = uiState.headGenotype,
                        onSelect = { viewModel.updateHeadGenotype(it) }
                    )
                    GenotypeDropdown(
                        label = "Грудь",
                        options = GouldianGenetics.breastOptions,
                        selected = uiState.breastGenotype,
                        onSelect = { viewModel.updateBreastGenotype(it) }
                    )
                    GenotypeDropdown(
                        label = "Тело",
                        options = GouldianGenetics.bodyOptions(uiState.sex),
                        selected = uiState.bodyGenotype,
                        onSelect = { viewModel.updateBodyGenotype(it) }
                    )
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

                    val mother = uiState.birds.find { it.id == uiState.motherId }
                    val father = uiState.birds.find { it.id == uiState.fatherId }
                    if (mother != null && father != null) {
                        OffspringForecast(mother = mother, father = father)
                    }
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

    // Parent picker dialog
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
private fun GenotypeDropdown(
    label: String,
    options: List<GouldianGenetics.GenotypeOption>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLabel = options.find { it.code == selected }?.label ?: selected

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrownDark),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$label: ", fontWeight = FontWeight.Medium)
                Text(
                    currentLabel,
                    color = BrownMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BrownGold)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = BeigeLight
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label, color = BrownDark) },
                    onClick = {
                        onSelect(option.code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun OffspringForecast(mother: Bird, father: Bird) {
    val context = LocalContext.current
    val available = remember { GouldianImageCatalog.availableFiles(context.applicationContext) }
    val predictions = remember(mother, father) {
        GouldianGenetics.predictOffspring(
            fatherHead = father.headGenotype,
            fatherBreast = father.breastGenotype,
            fatherBody = father.bodyGenotype,
            motherHead = mother.headGenotype,
            motherBreast = mother.breastGenotype,
            motherBody = mother.bodyGenotype
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            "🧬 Возможные окрасы птенцов",
            fontWeight = FontWeight.Bold,
            color = BrownDark,
            fontSize = 15.sp
        )
        listOf(
            GouldianGenetics.Sex.MALE to "Самцы",
            GouldianGenetics.Sex.FEMALE to "Самки"
        ).forEach { (sex, title) ->
            val rows = predictions.filter { it.sex == sex }
            Text(title, color = BrownMedium, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            rows.take(10).forEach { prediction ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GouldianAssetImage(
                        file = GouldianGenetics.imageFile(
                            prediction.head, prediction.breast, prediction.body,
                            prediction.sex, available
                        ),
                        contentDescription = prediction.label,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        prediction.label,
                        modifier = Modifier.weight(1f),
                        color = BrownDark,
                        fontSize = 13.sp
                    )
                    Text(
                        GouldianGenetics.formatPercent(prediction.probability),
                        color = BrownMedium,
                        fontSize = 13.sp
                    )
                }
            }
            if (rows.size > 10) {
                Text(
                    "и ещё ${rows.size - 10} вариантов",
                    color = BrownMedium,
                    fontSize = 12.sp
                )
            }
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
