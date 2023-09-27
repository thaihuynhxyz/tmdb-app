package xyz.thaihuynh.tmdb.ui.widget

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.thaihuynh.tmdb.ui.theme.TMDBAppTheme

@RunWith(AndroidJUnit4::class)
class OfflineRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            TMDBAppTheme {
                Surface {
                    OfflineRow()
                }
            }
        }
    }

    @Test
    fun offlineRow_isDisplayed() {
        composeTestRule.onNodeWithContentDescription("Offline").assertIsDisplayed()
        composeTestRule.onNodeWithText("You are offline").assertIsDisplayed()
    }
}