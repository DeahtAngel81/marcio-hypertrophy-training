package com.example.trainingmusclema.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for [com.example.trainingmusclema.ui.main.MainScreen]. */
class MainScreenTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    composeTestRule.setContent { MainScreen() }
  }

  @Test
  fun appHeader_exists() {
    composeTestRule.onNodeWithText("TRAINING MUSCLE MA!").assertExists()
    composeTestRule.onNodeWithText("Gestão de Treino Inteligente").assertExists()
  }
}
