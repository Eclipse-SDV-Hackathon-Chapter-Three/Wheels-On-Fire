//package com.example.digitalclusterapp
//
//import ClusterScreen
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onNodeWithContentDescription
//import androidx.compose.ui.test.onNodeWithText
//import org.junit.Test
//
//import org.junit.Assert.*
//import org.junit.Before
//import org.junit.Rule
//
//class InstrumentClusterScreenTest {
//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    @Before
//    fun setUp() {
//        composeTestRule.setContent {
//            MaterialTheme {
//                ClusterScreen(
//                    state = TODO(),
//                    onCruiseToggle = TODO()
//                )
//            }
//        }
//    }
//
//    @Test
//    fun testSpeedTextIsZero() {
//        composeTestRule.onNodeWithText("0 km/h").assertIsDisplayed()
//    }
//
//    @Test
//    fun testCruiseControlIconIsOff() {
//        composeTestRule.onNodeWithContentDescription("Cruise Control Icon")
//            .assertIsDisplayed()
//        // Note: Since the icon's state (on/off) is inferred from the switch state (false),
//        // we rely on text "off" until we add the asst«et
//        composeTestRule.onNodeWithText("Cruise On").assertDoesNotExist()
//    }
//}