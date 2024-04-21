package com.example.expenditurelogger

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isFocusable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var navController: TestNavHostController

    @Before
    fun setupAppNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            MainApp(navController = navController)
        }
    }

    @Test
    fun MainApp_verifyStartDestination() {
        composeTestRule.onRoot().printToLog("currentLabelExists")

        composeTestRule
            .onNodeWithText("Expenditure Logger")
            .assertIsDisplayed()
    }

    @Test
    fun MainApp_navigationToCamera() {
        composeTestRule
            .onNodeWithContentDescription("Camera")
            .performClick()

        composeTestRule.onNodeWithText("Please grant camera permissions.").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Navigate Back").performClick()
        composeTestRule.onNodeWithText("Expenditure Logger").assertIsDisplayed()
    }

    @Test
    fun MainApp_navigationToForm() {
        composeTestRule
            .onNodeWithContentDescription("Manual Input")
            .performClick()

        composeTestRule.onNodeWithText("Merchant").assertIsDisplayed().assert(isFocusable())
        composeTestRule.onNodeWithText("Date").assertIsDisplayed().assert(isFocusable())
        composeTestRule.onNodeWithText("Total").assertIsDisplayed().assert(isFocusable())

        composeTestRule.onNodeWithText("Send").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Navigation to Back").performClick()
        composeTestRule.onNodeWithText("Expenditure Logger").assertIsDisplayed()
    }

    @Test
    fun MainApp_navigationToSettings() {
        composeTestRule
            .onNodeWithContentDescription("Settings")
            .performClick()

        composeTestRule.onNodeWithText("Sending Data").assertIsDisplayed()
        composeTestRule.onNodeWithText("List of Known Merchants").assertIsDisplayed()

        composeTestRule.onNodeWithText("Sending Data").assertIsDisplayed().performClick()
        composeTestRule.onAllNodesWithText("Backend URL").assertAny(isFocusable())
        composeTestRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigation to Back").performClick()

        composeTestRule.onNodeWithText("List of Known Merchants").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Editable List of Merchants").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigation to Back").performClick()

        composeTestRule.onNodeWithContentDescription("Navigation to Back").performClick()
        composeTestRule.onNodeWithText("Expenditure Logger").assertIsDisplayed()
    }

}