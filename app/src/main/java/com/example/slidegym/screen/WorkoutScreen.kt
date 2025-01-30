package com.example.slidegym.ui.screen

import androidx.compose.animation.animateContentSize
import com.example.slidegym.data.model.WeekDay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.slidegym.data.model.Exercise
import com.example.slidegym.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(viewModel: WorkoutViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Treino")
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar exercício"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs dos dias
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedDay.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 16.dp
            ) {
                WeekDay.values().forEach { day ->
                    Tab(
                        selected = uiState.selectedDay == day,
                        onClick = { viewModel.onDaySelected(day) },
                        text = {
                            Text(
                                day.name,
                                color = if (uiState.selectedDay == day)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    )
                }
            }

            // Lista de exercícios
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val exercises = uiState.exercises[uiState.selectedDay] ?: emptyList()
                items(
                    count = exercises.size,
                    key = { exercises[it].id }
                ) { index ->
                    val exercise = exercises[index]
                    ExerciseCard(
                        exercise = exercise,
                        onToggleCompletion = { viewModel.toggleExerciseCompletion(it) },
                        onEdit = { viewModel.showEditDialog(it) },
                        onDelete = { viewModel.deleteExercise(it) }
                    )
                }
            }
        }
    }

    if (uiState.isAddDialogVisible) {
        ExerciseDialog(
            exercise = uiState.editingExercise,
            onDismiss = { viewModel.hideDialog() },
            onSave = { name, weight, reps ->
                if (uiState.editingExercise != null) {
                    viewModel.updateExercise(
                        uiState.editingExercise!!.copy(
                            name = name,
                            weight = weight,
                            reps = reps
                        )
                    )
                } else {
                    viewModel.addExercise(name, weight, reps)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCard(
    exercise: Exercise,
    onToggleCompletion: (Exercise) -> Unit,
    onEdit: (Exercise) -> Unit,
    onDelete: (Exercise) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        onClick = { onToggleCompletion(exercise) },
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (exercise.isCompleted)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${exercise.weight}kg - ${exercise.reps} reps",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Mais opções"
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar") },
                        onClick = {
                            onEdit(exercise)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Excluir") },
                        onClick = {
                            onDelete(exercise)
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDialog(
    exercise: Exercise?,
    onDismiss: () -> Unit,
    onSave: (name: String, weight: Double, reps: Int) -> Unit
) {
    var name by remember { mutableStateOf(exercise?.name ?: "") }
    var weight by remember { mutableStateOf(exercise?.weight?.toString() ?: "") }
    var reps by remember { mutableStateOf(exercise?.reps?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (exercise == null) "Novo Exercício" else "Editar Exercício") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Exercício") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Repetições") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val weightDouble = weight.toDoubleOrNull() ?: 0.0
                    val repsInt = reps.toIntOrNull() ?: 0
                    if (name.isNotBlank() && weightDouble > 0 && repsInt > 0) {
                        onSave(name, weightDouble, repsInt)
                        onDismiss()
                    }
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}