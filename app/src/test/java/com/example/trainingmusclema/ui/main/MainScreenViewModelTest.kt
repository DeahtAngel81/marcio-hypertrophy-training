package com.example.trainingmusclema.ui.main

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MainScreenViewModelTest {

  @Test
  fun initialState_isDashboard() = runTest {
    val viewModel = MainScreenViewModel()
    val state = viewModel.state.first()

    assertEquals(Screen.Dashboard, state.currentScreen)
    assertEquals(1, state.selectedDayNumber)
    assertEquals(0, state.currentPhaseIndex)
    assertTrue(state.completedExerciseIds.isEmpty())
  }

  @Test
  fun selectDay_updatesScreenToWorkout() = runTest {
    val viewModel = MainScreenViewModel()
    viewModel.selectDay(2)

    val state = viewModel.state.value
    assertEquals(Screen.Workout(2), state.currentScreen)
    assertEquals(2, state.selectedDayNumber)
  }

  @Test
  fun toggleExerciseCompletion_togglesExerciseState() = runTest {
    val viewModel = MainScreenViewModel()
    val exerciseId = "ex_test_1"

    viewModel.toggleExerciseCompletion(exerciseId)
    assertTrue(viewModel.state.value.completedExerciseIds.contains(exerciseId))

    viewModel.toggleExerciseCompletion(exerciseId)
    assertFalse(viewModel.state.value.completedExerciseIds.contains(exerciseId))
  }

  @Test
  fun navigateBack_fromWorkoutToDashboard() = runTest {
    val viewModel = MainScreenViewModel()
    viewModel.selectDay(1)
    assertEquals(Screen.Workout(1), viewModel.state.value.currentScreen)

    viewModel.navigateBack()
    assertEquals(Screen.Dashboard, viewModel.state.value.currentScreen)
  }

  @Test
  fun selectPhase_resetsPhaseState() = runTest {
    val viewModel = MainScreenViewModel()
    viewModel.selectDay(2)
    viewModel.toggleExerciseCompletion("ex_test_1")

    viewModel.selectPhase(1)

    val state = viewModel.state.value
    assertEquals(1, state.currentPhaseIndex)
    assertEquals(1, state.selectedDayNumber)
    assertTrue(state.completedExerciseIds.isEmpty())
    assertEquals(Screen.Dashboard, state.currentScreen)
  }
}
