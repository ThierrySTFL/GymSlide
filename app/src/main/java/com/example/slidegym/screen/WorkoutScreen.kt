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
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

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
    val offset = remember { Animatable(0f) }
    val swipeThreshold = 150f
    val coroutineScope = rememberCoroutineScope()

    // Log para debug
    LaunchedEffect(exercise.isCompleted) {
        println("Estado do exercício alterado: ${exercise.name} - Completo: ${exercise.isCompleted}")
    }

    val backgroundColor = if (exercise.isCompleted) {
        Color(0xFF4CAF50)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(offset.value.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            if (abs(offset.value) > swipeThreshold) {
                                val shouldComplete = offset.value > 0
                                println("Swipe detectado: direção=${if(shouldComplete) "direita" else "esquerda"}")

                                // Cria um novo objeto Exercise com o estado atualizado
                                val updatedExercise = exercise.copy(isCompleted = shouldComplete)
                                onToggleCompletion(updatedExercise)
                            }
                            offset.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            offset.snapTo(offset.value + dragAmount.x)
                        }
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (exercise.isCompleted) Color.White else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${exercise.weight}kg - ${exercise.reps} reps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (exercise.isCompleted) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Mais opções",
                        tint = if (exercise.isCompleted) Color.White else MaterialTheme.colorScheme.onSurface
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