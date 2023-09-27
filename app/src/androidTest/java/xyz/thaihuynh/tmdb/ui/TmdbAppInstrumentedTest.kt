package xyz.thaihuynh.tmdb.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import xyz.thaihuynh.tmdb.MainActivity

@HiltAndroidTest
class TmdbAppInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun testShowHomeScreen() {
        composeTestRule.onNodeWithContentDescription("SearchBar").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("TrendingList").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("OfflineBox").assertExists()
    }
}