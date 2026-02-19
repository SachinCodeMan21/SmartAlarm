package com.example.smartalarm.ui.alarm

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartalarm.R
import com.example.smartalarm.feature.alarm.presentation.view.activity.AlarmEditorActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AlarmEditorActivityTest {


    @get:Rule(0)
    val hiltRule = HiltAndroidRule(this)
    private val context : Context = ApplicationProvider.getApplicationContext()


    @Test
    fun whenLaunchedForNewAlarm_receivesDefaultExtras_andShowsCreateAlarmToolbarTitle() {

        launchActivityForNewAlarm().use { scenario ->
            scenario.onActivity { activity ->
                val receivedId = activity.intent.getIntExtra(AlarmEditorActivity.EXISTING_ALARM_ID_KEY, -1)
                assertThat(receivedId).isEqualTo(-1)
            }

            // Verify toolbar title is "Create new alarm" (assuming string resource)
            onView(ViewMatchers.isAssignableFrom(MaterialToolbar::class.java))
                .check(matches(withToolbarTitle(context.getString(R.string.create_new_alarm))))
        }
    }

    @Test
    fun whenLaunchedForExistingAlarm_receivesCorrectExtras_andShowsUpdateAlarmToolbarTitle() {

        launchActivityWithExistingAlarmId().use { scenario ->
            scenario.onActivity { activity ->
                val receivedId = activity.intent.getIntExtra(AlarmEditorActivity.Companion.EXISTING_ALARM_ID_KEY, -1)
                Truth.assertThat(receivedId).isEqualTo(5)
            }

            // Verify toolbar title is "Update alarm" (assuming string resource)
            onView(ViewMatchers.isAssignableFrom(MaterialToolbar::class.java))
                .check(matches(withToolbarTitle(context.getString(R.string.update_alarm))))
        }
    }

    @Test
    fun whenLaunched_toolbarShowsBackArrowForBothNewAndExistingAlarm() {
        // Test for new alarm scenario
        listOf(
            { launchActivityForNewAlarm() },
            { launchActivityWithExistingAlarmId() }
        ).forEach { launchScenario ->
            launchScenario().use { scenario ->
                // Verify that the toolbar contains the back arrow
                onView(ViewMatchers.isAssignableFrom(MaterialToolbar::class.java)).check(matches(hasNavigationIcon()))
            }
        }
    }

    @Test
    fun whenBackPressed_activityFinishesForBothNewAndExistingAlarm() {

        ActivityScenario.launch(AlarmEditorActivity::class.java).use{scenario ->

            scenario.onActivity { activity ->
                // Simulate a back press
                activity.onBackPressedDispatcher.onBackPressed()

                // Verify the activity is finished (i.e., it should not be active anymore)
                assertThat(activity.isFinishing).isTrue()
            }

        }

    }



    // ------------------------
    // Helper Methods
    // -------------------------
    private fun withToolbarTitle(expectedTitle: String): Matcher<View> {
        return object : BoundedMatcher<View, MaterialToolbar>(MaterialToolbar::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("with toolbar title: $expectedTitle")
            }

            override fun matchesSafely(toolbar: MaterialToolbar): Boolean {
                val title = toolbar.title
                return title != null && title.toString() == expectedTitle
            }
        }
    }
    private fun hasNavigationIcon(): Matcher<View> = object : TypeSafeMatcher<View>() {

        override fun matchesSafely(view: View): Boolean {
            return view is MaterialToolbar && view.navigationIcon != null
        }

        override fun describeTo(description: Description) {
            description.appendText("MaterialToolbar with a navigation icon set")
        }
    }


    // Launch Activity Scenarios
    private fun launchActivityForNewAlarm(): ActivityScenario<AlarmEditorActivity> {
        val intent = Intent(context, AlarmEditorActivity::class.java)
        return ActivityScenario.launch(intent)
    }
    private fun launchActivityWithExistingAlarmId(): ActivityScenario<AlarmEditorActivity> {
        val intent = Intent(context, AlarmEditorActivity::class.java).apply {
            putExtra(AlarmEditorActivity.EXISTING_ALARM_ID_KEY, 5)
        }
        return ActivityScenario.launch(intent)
    }


}