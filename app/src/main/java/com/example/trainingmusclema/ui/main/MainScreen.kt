package com.example.trainingmusclema.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingmusclema.R
import com.example.trainingmusclema.data.ExerciseType
import java.util.Locale
import kotlin.math.abs

// Custom Theme Colors
val ThemeBackground = Color(0xFF0F0F12)
val ThemeSurface = Color(0xFF1B1B22)
val ThemeSurfaceVariant = Color(0xFF262630)
val ThemePrimary = Color(0xFF00E5FF)       // Cyan Neon
val ThemeSecondary = Color(0xFFFF6D00)     // Orange Neon
val ThemeTextPrimary = Color(0xFFFFFFFF)
val ThemeTextSecondary = Color(0xFFA0A0AB)
val ThemeSuccess = Color(0xFF00E676)

fun getExerciseDrawableRes(name: String): Int = when (name) {
    "ex_bench_press" -> R.drawable.ex_bench_press
    "ex_lat_pulldown" -> R.drawable.ex_lat_pulldown
    "ex_shoulder_press" -> R.drawable.ex_shoulder_press
    "ex_cable_row" -> R.drawable.ex_cable_row
    "ex_hip_thrust" -> R.drawable.ex_hip_thrust
    "ex_seated_curl" -> R.drawable.ex_seated_curl
    "ex_romanian_deadlift" -> R.drawable.ex_romanian_deadlift
    "ex_seated_calf" -> R.drawable.ex_seated_calf
    "ex_ez_curl" -> R.drawable.ex_ez_curl
    "ex_pushdown" -> R.drawable.ex_pushdown
    "ex_hammer_curl" -> R.drawable.ex_hammer_curl
    "ex_plank" -> R.drawable.ex_plank
    "ex_single_bridge" -> R.drawable.ex_single_bridge
    "ex_rear_fly" -> R.drawable.ex_rear_fly
    "upper_illustration" -> R.drawable.upper_illustration
    "lower_illustration" -> R.drawable.lower_illustration
    "warmup_illustration" -> R.drawable.warmup_illustration
    "cardio_illustration" -> R.drawable.cardio_illustration
    else -> 0
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = ThemeBackground,
            surface = ThemeSurface,
            primary = ThemePrimary,
            secondary = ThemeSecondary,
        ),
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ThemeBackground)
                .safeDrawingPadding(),
        ) {
            when (val screen = state.currentScreen) {
                is Screen.Dashboard -> {
                    DashboardScreen(
                        state = state,
                        onSelectPhase = { viewModel.selectPhase(it) },
                        onSelectDay = { viewModel.selectDay(it) },
                        onUpdateWeight = { viewModel.updateWeightInput(it) },
                        onUpdateSecondWeight = { viewModel.updateSecondWeightInput(it) },
                        onUpdateHeight = { viewModel.updateHeightInput(it) },
                        onUpdateEntryDate = { viewModel.updateEntryDateInput(it) },
                        onUpdateTargetDate = { viewModel.updateTargetDateInput(it) },
                        onUnlockInitialInputs = { viewModel.unlockInitialInputs() },
                        onSaveWeightRecord = { viewModel.saveWeightRecord() },
                        onDeleteWeightRecord = { viewModel.deleteWeightRecord(it) },
                    )
                }
                is Screen.Workout -> {
                    WorkoutScreen(
                        state = state,
                        onBack = { viewModel.navigateBack() },
                        onToggleExercise = { viewModel.toggleExerciseCompletion(it) },
                        onViewDetail = { viewModel.navigateTo(Screen.ExerciseDetail(it, screen.dayNumber)) },
                        onStartTimer = { id, sec -> viewModel.startTimer(id, sec) },
                        onToggleTimer = { viewModel.toggleTimer() },
                        onStopTimer = { viewModel.stopTimer() },
                    )
                }
                is Screen.ExerciseDetail -> {
                    ExerciseDetailScreen(
                        state = state,
                        exerciseId = screen.exerciseId,
                        onBack = { viewModel.navigateBack() },
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    state: MainScreenState,
    onSelectPhase: (Int) -> Unit,
    onSelectDay: (Int) -> Unit,
    onUpdateWeight: (String) -> Unit,
    onUpdateSecondWeight: (String) -> Unit,
    onUpdateHeight: (String) -> Unit,
    onUpdateEntryDate: (String) -> Unit,
    onUpdateTargetDate: (String) -> Unit,
    onUnlockInitialInputs: () -> Unit,
    onSaveWeightRecord: () -> Unit,
    onDeleteWeightRecord: (String) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
    ) {
        // App Header
        Spacer(modifier = Modifier.height(8.dp))
        Column {
            Text(
                text = "TRAINING MUSCLE MA!",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                color = ThemePrimary,
                modifier = Modifier.shadow(2.dp, shape = RoundedCornerShape(4.dp)),
            )
            Text(
                text = "Gestão de Treino Inteligente",
                fontSize = 13.sp,
                color = ThemeTextSecondary,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Weight and Height Tracker Card
        WeightTrackerSection(
            state = state,
            onUpdateWeight = onUpdateWeight,
            onUpdateSecondWeight = onUpdateSecondWeight,
            onUpdateHeight = onUpdateHeight,
            onUpdateEntryDate = onUpdateEntryDate,
            onUpdateTargetDate = onUpdateTargetDate,
            onUnlockInitialInputs = onUnlockInitialInputs,
            onSaveWeightRecord = onSaveWeightRecord,
            onDeleteWeightRecord = onDeleteWeightRecord,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Warning/Care Card - Tibia Fracture Recovery
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF3E2723), Color(0xFF260F0D)),
                    ),
                    shape = RoundedCornerShape(16.dp),
                )
                .border(1.dp, ThemeSecondary.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(ThemeSecondary, CircleShape),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ATENÇÃO: PACIENTE POSSUI FRATURA NO PLANALTO TIBIAL (JOELHO DIREITO).",
                    color = ThemeSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Phase Selector (Rotational System every 4 weeks)
        Text(
            text = "Ciclo de Treinamento (Rotação a cada 4 semanas)",
            color = ThemeTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.phases.forEachIndexed { index, phase ->
                val isSelected = state.currentPhaseIndex == index
                val borderBrush = if (isSelected) {
                    Brush.linearGradient(listOf(ThemePrimary, ThemeSecondary))
                } else {
                    Brush.linearGradient(listOf(ThemeSurfaceVariant, ThemeSurfaceVariant))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) ThemeSurfaceVariant else ThemeSurface,
                            RoundedCornerShape(12.dp),
                        )
                        .border(
                            width = 2.dp,
                            brush = borderBrush,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { onSelectPhase(index) }
                        .padding(12.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = phase.name,
                            color = if (isSelected) ThemePrimary else ThemeTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (index == 0) "Semanas 1 a 4" else "Semanas 5 a 8",
                            color = ThemeTextSecondary,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weekly View Header
        Text(
            text = "Selecione o Dia para Treinar (Intercalado M.S. e M.I.)",
            color = ThemeTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))

        // List of days
        state.currentPhase.days.forEach { day ->
            val isLower = day.focusName.contains("inferiores", ignoreCase = true)
            val dayFinished = day.exercises.all { state.completedExerciseIds.contains(it.id) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .background(ThemeSurface, RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        if (dayFinished) ThemeSuccess.copy(alpha = 0.5f) else ThemeSurfaceVariant,
                        RoundedCornerShape(16.dp),
                    )
                    .clickable { onSelectDay(day.dayNumber) }
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Day Number Indicator
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (isLower) ThemeSecondary.copy(alpha = 0.15f) else ThemePrimary.copy(alpha = 0.15f),
                                CircleShape,
                            )
                            .border(
                                1.dp,
                                if (isLower) ThemeSecondary else ThemePrimary,
                                CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = day.dayNumber.toString(),
                            color = if (isLower) ThemeSecondary else ThemePrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Day Details
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = day.dayName,
                                color = ThemeTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            )
                            if (dayFinished) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Concluído",
                                    color = ThemeSuccess,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(ThemeSuccess.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = day.focusName,
                            color = ThemeTextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    // Arrow Icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(ThemeSurfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "➔",
                            color = ThemePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeightTrackerSection(
    state: MainScreenState,
    onUpdateWeight: (String) -> Unit,
    onUpdateSecondWeight: (String) -> Unit,
    onUpdateHeight: (String) -> Unit,
    onUpdateEntryDate: (String) -> Unit,
    onUpdateTargetDate: (String) -> Unit,
    onUnlockInitialInputs: () -> Unit,
    onSaveWeightRecord: () -> Unit,
    onDeleteWeightRecord: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ThemeSurface, RoundedCornerShape(16.dp))
            .border(1.dp, ThemePrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(ThemePrimary, CircleShape),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ACOMPANHAMENTO DE PESO & ALTURA",
                    color = ThemePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp,
                )
            }
            Text(
                text = "Informe seu peso e altura para calcular o IMC e comparar a evolução de peso entre duas datas",
                color = ThemeTextSecondary,
                fontSize = 11.sp,
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Inputs Row 1: Weight and Height
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = state.userWeightInput,
                    onValueChange = onUpdateWeight,
                    enabled = !state.isInitialLocked,
                    label = { Text("Peso Inicial (kg)", fontSize = 11.sp, color = ThemeTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ThemeTextPrimary,
                        unfocusedTextColor = ThemeTextPrimary,
                        disabledTextColor = ThemeTextSecondary.copy(alpha = 0.8f),
                        disabledBorderColor = ThemeSurfaceVariant,
                        disabledContainerColor = ThemeSurfaceVariant.copy(alpha = 0.4f),
                        focusedBorderColor = ThemePrimary,
                        unfocusedBorderColor = ThemeSurfaceVariant,
                        focusedContainerColor = ThemeSurfaceVariant,
                        unfocusedContainerColor = ThemeSurfaceVariant,
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                )

                OutlinedTextField(
                    value = state.userHeightInput,
                    onValueChange = onUpdateHeight,
                    enabled = !state.isInitialLocked,
                    label = { Text("Altura (cm)", fontSize = 11.sp, color = ThemeTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ThemeTextPrimary,
                        unfocusedTextColor = ThemeTextPrimary,
                        disabledTextColor = ThemeTextSecondary.copy(alpha = 0.8f),
                        disabledBorderColor = ThemeSurfaceVariant,
                        disabledContainerColor = ThemeSurfaceVariant.copy(alpha = 0.4f),
                        focusedBorderColor = ThemePrimary,
                        unfocusedBorderColor = ThemeSurfaceVariant,
                        focusedContainerColor = ThemeSurfaceVariant,
                        unfocusedContainerColor = ThemeSurfaceVariant,
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Inputs Row 2: Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = state.userEntryDateInput,
                    onValueChange = onUpdateEntryDate,
                    enabled = !state.isInitialLocked,
                    label = { Text("Data Atual", fontSize = 11.sp, color = ThemeTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ThemeTextPrimary,
                        unfocusedTextColor = ThemeTextPrimary,
                        disabledTextColor = ThemeTextSecondary.copy(alpha = 0.8f),
                        disabledBorderColor = ThemeSurfaceVariant,
                        disabledContainerColor = ThemeSurfaceVariant.copy(alpha = 0.4f),
                        focusedBorderColor = ThemePrimary,
                        unfocusedBorderColor = ThemeSurfaceVariant,
                        focusedContainerColor = ThemeSurfaceVariant,
                        unfocusedContainerColor = ThemeSurfaceVariant,
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                )

                OutlinedTextField(
                    value = state.userTargetDateInput,
                    onValueChange = onUpdateTargetDate,
                    label = { Text("Segunda Data", fontSize = 11.sp, color = ThemeTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ThemeTextPrimary,
                        unfocusedTextColor = ThemeTextPrimary,
                        focusedBorderColor = ThemePrimary,
                        unfocusedBorderColor = ThemeSurfaceVariant,
                        focusedContainerColor = ThemeSurfaceVariant,
                        unfocusedContainerColor = ThemeSurfaceVariant,
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Exact date check: Field is editable ONLY on the exact day of the second date
            val todayCal = parseDate(state.userEntryDateInput)
            val secondCal = parseDate(state.userTargetDateInput)
            val isSecondDateEditable = (todayCal != null) && (secondCal != null) &&
                    (todayCal.get(java.util.Calendar.YEAR) == secondCal.get(java.util.Calendar.YEAR)) &&
                    (todayCal.get(java.util.Calendar.DAY_OF_YEAR) == secondCal.get(java.util.Calendar.DAY_OF_YEAR))

            // BMI Calculations
            val w1 = state.userWeightInput.replace(',', '.').toFloatOrNull() ?: 0f
            val w2 = state.userSecondWeightInput.replace(',', '.').toFloatOrNull()
            val heightCm = state.userHeightInput.replace(',', '.').toFloatOrNull() ?: 0f
            val heightM = heightCm / 100f

            val bmi1 = if ((heightM > 0f) && (w1 > 0f)) w1 / (heightM * heightM) else 0f
            val cat1 = getBmiCategory(bmi1)

            val hasSecondWeight = isSecondDateEditable && (w2 != null) && (w2 > 0f) && (w1 > 0f) && (heightM > 0f)

            val secondW = w2 ?: 0f
            val bmi2 = if (heightM > 0f) secondW / (heightM * heightM) else 0f
            val cat2 = getBmiCategory(bmi2)
            val diffKg = secondW - w1

            val changeText = when {
                diffKg < 0f -> "▼ REDUÇÃO DE ${String.format(Locale.US, "%.1f", abs(diffKg))} KG"
                diffKg > 0f -> "▲ AUMENTO DE ${String.format(Locale.US, "%.1f", diffKg)} KG"
                else -> "SEM ALTERAÇÃO"
            }

            val changeColor = when {
                diffKg < 0f -> ThemeSuccess
                diffKg > 0f -> ThemeSecondary
                else -> ThemePrimary
            }

            val statusDescription = when {
                bmi2 < 18.5f -> "Abaixo do peso recomendado na tabela IMC"
                bmi2 < 25.0f -> "Dentro da faixa de peso ideal (Peso normal)"
                bmi2 < 30.0f -> "Acima do peso ideal (Sobrepeso)"
                else -> "Classificado como Obesidade na tabela IMC"
            }

            // Input Row 3: Divided space between "Segundo Peso" input (Left) and "Resultado do Peso" field (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.userSecondWeightInput,
                    onValueChange = { newValue ->
                        if (isSecondDateEditable) {
                            onUpdateSecondWeight(newValue)
                        }
                    },
                    enabled = isSecondDateEditable,
                    label = { Text("Segundo Peso (kg)", fontSize = 11.sp, color = if (isSecondDateEditable) ThemeTextSecondary else ThemeTextSecondary.copy(alpha = 0.5f)) },
                    placeholder = {
                        Text(
                            text = if (isSecondDateEditable) "Informe o 2º peso" else "Bloqueado até ${state.userTargetDateInput}",
                            fontSize = 10.sp,
                            color = ThemeTextSecondary.copy(alpha = 0.5f),
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ThemeTextPrimary,
                        unfocusedTextColor = ThemeTextPrimary,
                        disabledTextColor = ThemeTextSecondary.copy(alpha = 0.6f),
                        disabledBorderColor = ThemeSurfaceVariant.copy(alpha = 0.5f),
                        disabledContainerColor = ThemeSurfaceVariant.copy(alpha = 0.3f),
                        focusedBorderColor = ThemeSecondary,
                        unfocusedBorderColor = ThemeSurfaceVariant,
                        focusedContainerColor = ThemeSurfaceVariant,
                        unfocusedContainerColor = ThemeSurfaceVariant,
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                )

                // Result Field next to Second Weight
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .background(ThemeSurfaceVariant, RoundedCornerShape(10.dp))
                        .border(
                            1.dp,
                            if (hasSecondWeight) changeColor else if (!isSecondDateEditable) ThemeSecondary.copy(alpha = 0.5f) else ThemeSurfaceVariant,
                            RoundedCornerShape(10.dp),
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (!isSecondDateEditable) {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = "🔒 Campo Bloqueado",
                                color = ThemeSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Liberado em: ${state.userTargetDateInput}",
                                color = ThemeTextSecondary,
                                fontSize = 10.sp,
                            )
                        }
                    } else if (hasSecondWeight) {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = changeText,
                                color = changeColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "IMC: ${String.format(Locale.US, "%.1f", bmi2)} ($cat2)",
                                color = ThemeTextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = "Resultado do Peso",
                                color = ThemeTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Aguardando 2º peso",
                                color = ThemeTextSecondary.copy(alpha = 0.6f),
                                fontSize = 10.sp,
                            )
                        }
                    }
                }
            }

            if (!isSecondDateEditable) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ThemeSecondary.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .border(1.dp, ThemeSecondary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                ) {
                    Text(
                        text = "🔒 O preenchimento do segundo peso estará liberado no dia exato da segunda data (${state.userTargetDateInput}).",
                        color = ThemeSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lock Indicator Banner when Initial Weight/Height are saved
            if (state.isInitialLocked) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .background(ThemePrimary.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                        .border(1.dp, ThemePrimary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🔒 Dados Iniciais Salvos e Fixados",
                                color = ThemePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                            )
                            Text(
                                text = "Permanecerão inalterados até a 2ª data (${state.userTargetDateInput}).",
                                color = ThemeTextSecondary,
                                fontSize = 10.sp,
                            )
                        }

                        Text(
                            text = "Desbloquear",
                            color = ThemeSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .clickable { onUnlockInitialInputs() }
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            // Progress / Regression Detailed Indicator Box
            if (hasSecondWeight) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ThemeSurfaceVariant, RoundedCornerShape(12.dp))
                        .border(1.5.dp, changeColor, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                ) {
                    Column {
                        Text(
                            text = "INDICADOR DE EVOLUÇÃO (TABELA IMC)",
                            color = ThemeTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "• Data Inicial (${state.userEntryDateInput}): $w1 kg ➔ IMC ${String.format(Locale.US, "%.1f", bmi1)} ($cat1)",
                                color = ThemeTextPrimary,
                                fontSize = 11.sp,
                            )
                            Text(
                                text = "• Segunda Data (${state.userTargetDateInput}): $secondW kg ➔ IMC ${String.format(Locale.US, "%.1f", bmi2)} ($cat2)",
                                color = ThemePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Status Tabela IMC: $statusDescription",
                                color = changeColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onSaveWeightRecord,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemePrimary,
                        contentColor = Color.Black,
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    Text("Salvar Registro Comparativo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                // Default display when only initial weight is filled
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ThemeSurfaceVariant, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "IMC Inicial: " + if (bmi1 > 0f) String.format(Locale.US, "%.1f kg/m²", bmi1) else "-",
                            color = ThemePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                        Text(
                            text = "Classificação: $cat1",
                            color = ThemeTextSecondary,
                            fontSize = 11.sp,
                        )
                    }

                    Button(
                        onClick = onSaveWeightRecord,
                        enabled = !state.isInitialLocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ThemePrimary,
                            contentColor = Color.Black,
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(if (state.isInitialLocked) "Dados Salvos 🔒" else "Salvar Registro", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            // Saved Records List
            if (state.weightRecords.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Histórico de Medições",
                    color = ThemeTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
                Spacer(modifier = Modifier.height(6.dp))

                state.weightRecords.forEach { record ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .background(ThemeSurfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                if ((record.secondWeightKg != null) && (record.secondWeightKg > 0f)) {
                                    val diff = record.secondWeightKg - record.weightKg
                                    val diffLabel = if (diff < 0) "Redução: ${String.format(Locale.US, "%.1f", abs(diff))} kg" else "Aumento: ${String.format(Locale.US, "%.1f", diff)} kg"
                                    Text(
                                        text = "${record.weightKg} kg (${record.date}) ➔ ${record.secondWeightKg} kg (${record.secondDate}) | $diffLabel",
                                        color = ThemeTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                    )
                                    Text(
                                        text = "IMC: ${String.format(Locale.US, "%.1f", record.bmiInitial)} (${record.bmiCategoryInitial}) ➔ ${String.format(Locale.US, "%.1f", record.bmiSecond ?: 0f)} (${record.bmiCategorySecond ?: ""})",
                                        color = ThemePrimary,
                                        fontSize = 10.sp,
                                    )
                                } else {
                                    Text(
                                        text = "${record.weightKg} kg  |  ${record.heightCm} cm  |  IMC: ${String.format(Locale.US, "%.1f", record.bmiInitial)} (${record.bmiCategoryInitial})",
                                        color = ThemeTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                    )
                                    Text(
                                        text = "Medido em: ${record.date}",
                                        color = ThemeTextSecondary,
                                        fontSize = 10.sp,
                                    )
                                }
                            }

                            Text(
                                text = "✕",
                                color = ThemeSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { onDeleteWeightRecord(record.id) }
                                    .padding(4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutScreen(
    state: MainScreenState,
    onBack: () -> Unit,
    onToggleExercise: (String) -> Unit,
    onViewDetail: (String) -> Unit,
    onStartTimer: (String, Int) -> Unit,
    onToggleTimer: () -> Unit,
    onStopTimer: () -> Unit,
) {
    val day = state.currentDay
    val scrollState = rememberScrollState()

    // Calculate Workout Completion Progress (Based on time)
    // Warmup: 5 min, Strength: 10 min each, Cardio: 15 min.
    var totalTime = 0
    var completedTime = 0
    day.exercises.forEach { ex ->
        val exTime = when (ex.type) {
            ExerciseType.WARMUP -> 5
            ExerciseType.CARDIO -> 15
            else -> 10 // Strength
        }
        totalTime += exTime
        if (state.completedExerciseIds.contains(ex.id)) {
            completedTime += exTime
        }
    }

    val progress = if (totalTime > 0) completedTime.toFloat() / totalTime.toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        // App Bar / Top Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(ThemeSurface, CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "◀",
                    color = ThemePrimary,
                    fontSize = 14.sp,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = day.dayName + " - " + day.focusName,
                    color = ThemeTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = state.currentPhase.name,
                    color = ThemePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        // Active Global Timer Bar (Floating Rest Timer)
        if (state.activeTimerExerciseId != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(listOf(ThemeSecondary, Color(0xFFFF3D00))),
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val activeEx = day.exercises.find { it.id == state.activeTimerExerciseId }
                    val minutes = state.timerRemainingSeconds / 60
                    val seconds = state.timerRemainingSeconds % 60
                    val timeString = String.format(Locale.US, "%02d:%02d", minutes, seconds)

                    Column {
                        Text(
                            text = "Intervalo Ativo: " + (activeEx?.name ?: "Descanso"),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = if (state.isTimerRunning) "Contagem regressiva rodando..." else "Timer Pausado",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = timeString,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        // Play/Pause button
                        Text(
                            text = if (state.isTimerRunning) "⏸" else "▶",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .clickable { onToggleTimer() }
                                .padding(8.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Stop button
                        Text(
                            text = "✕",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .clickable { onStopTimer() }
                                .padding(8.dp),
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
        ) {
            // Progress Bar Tracker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ThemeSurface, RoundedCornerShape(16.dp))
                    .padding(16.dp),
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Meta Diária: $totalTime min",
                            color = ThemeTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                        Text(
                            text = "$completedTime min concluídos",
                            color = ThemePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // The glowing progress track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(ThemeSurfaceVariant),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(ThemePrimary, ThemeSecondary),
                                    ),
                                ),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Lista de Exercícios",
                color = ThemeTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Exercise items
            day.exercises.forEach { ex ->
                val isCompleted = state.completedExerciseIds.contains(ex.id)
                val hasActiveTimer = state.activeTimerExerciseId == ex.id

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(
                            if (isCompleted) ThemeSurface.copy(alpha = 0.5f) else ThemeSurface,
                            RoundedCornerShape(16.dp),
                        )
                        .border(
                            1.dp,
                            if (hasActiveTimer) ThemeSecondary else if (isCompleted) ThemeSuccess.copy(alpha = 0.5f) else ThemeSurfaceVariant,
                            RoundedCornerShape(16.dp),
                        )
                        .padding(12.dp),
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Checkbox to complete
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCompleted) ThemeSuccess else ThemeSurfaceVariant,
                                    )
                                    .border(
                                        1.dp,
                                        if (isCompleted) ThemeSuccess else ThemeTextSecondary,
                                        CircleShape,
                                    )
                                    .clickable { onToggleExercise(ex.id) },
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isCompleted) {
                                    Text("✓", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Exercise detail
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ex.name,
                                    color = if (isCompleted) ThemeTextSecondary else ThemeTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Text(
                                        text = "${ex.sets} séries",
                                        color = ThemePrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        text = "${ex.reps} reps",
                                        color = ThemeSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    if (ex.rest != "N/A") {
                                        Text(
                                            text = "Descanso: ${ex.rest}",
                                            color = ThemeTextSecondary,
                                            fontSize = 11.sp,
                                        )
                                    }
                                }
                            }

                            // View Instruction button
                            Text(
                                text = "Instrução",
                                color = ThemePrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(ThemePrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .clickable { onViewDetail(ex.id) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                            )
                        }

                        // Extra actions (Start Rest Timer)
                        if (!isCompleted && ex.rest != "N/A") {
                            Spacer(modifier = Modifier.height(12.dp))

                            // Rest duration parsing (e.g. "90 seg" -> 90, "60 seg" -> 60)
                            val restSeconds = if (ex.rest.contains("90")) 90 else 60

                            Button(
                                onClick = { onStartTimer(ex.id, restSeconds) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ThemeSurfaceVariant,
                                    contentColor = ThemeTextPrimary,
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text("Iniciar Descanso (${ex.rest})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ExerciseDetailScreen(
    state: MainScreenState,
    exerciseId: String,
    onBack: () -> Unit,
) {
    val day = state.currentDay
    val exercise = day.exercises.find { it.id == exerciseId } ?: return
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        // Image Header with back button overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
        ) {
            val imageRes = remember(exercise.imageName) {
                getExerciseDrawableRes(exercise.imageName)
            }
            if (imageRes != 0) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = exercise.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ThemeSurface),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = exercise.name + "\n\nIlustração em preparação",
                        color = ThemeTextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp),
                    )
                }
            }
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, ThemeBackground.copy(alpha = 0.95f)),
                        ),
                    ),
            )

            // Back button
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .clickable { onBack() }
                    .align(Alignment.TopStart),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "◀",
                    color = Color.White,
                    fontSize = 14.sp,
                )
            }
        }

        // Exercise Title and stats
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = exercise.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeTextPrimary,
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .background(ThemePrimary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("${exercise.sets} Séries", color = ThemePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .background(ThemeSecondary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("${exercise.reps} Repetições", color = ThemeSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                if (exercise.rest != "N/A") {
                    Box(
                        modifier = Modifier
                            .background(ThemeSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text("Descanso: ${exercise.rest}", color = ThemeTextSecondary, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Knee-Safety Alert for Lower Body
            if ((exercise.type == ExerciseType.STRENGTH_LOWER) || (exercise.type == ExerciseType.CARDIO)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ThemeSecondary.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .border(1.dp, ThemeSecondary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                ) {
                    Column {
                        Text(
                            text = "💡 CUIDADO COM A TÍBIA & JOELHO",
                            color = ThemeSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Este exercício foi selecionado para poupar a tíbia e evitar cisalhamento do joelho. Cuidado com o alinhamento das pernas e evite cargas exageradas que causem dor.",
                            color = ThemeTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Description
            Text(
                text = "Descrição do Exercício",
                color = ThemeTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = exercise.description,
                color = ThemeTextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Step-by-Step execution form
            Text(
                text = "Forma de Execução & Postura",
                color = ThemeTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))

            exercise.executionTips.forEachIndexed { index, tip ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(20.dp)
                            .background(ThemePrimary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            color = ThemePrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = tip,
                        color = ThemeTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
