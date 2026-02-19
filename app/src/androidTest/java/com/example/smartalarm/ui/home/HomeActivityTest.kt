package com.example.smartalarm.ui.home


import android.content.Context
import android.content.res.Configuration
import android.view.View
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isSelected
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.example.smartalarm.R
import com.example.smartalarm.feature.home.presentation.view.HomeActivity
import com.example.smartalarm.feature.setting.activity.HelpActivity
import com.example.smartalarm.feature.setting.activity.SettingActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class HomeActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityScenarioRule = ActivityScenarioRule(HomeActivity::class.java)

    private val topLevelDestinations = listOf(
        Triple(R.id.alarmFragment, R.id.alarm_fragment_root, getResString(R.string.alarm)),
        Triple(R.id.clockFragment, R.id.clock_fragment_root, getResString(R.string.clock)),
        Triple(R.id.timerFragment, R.id.timer_fragment_root, getResString(R.string.timer)),
        Triple(R.id.stopwatchFragment, R.id.stopwatch_fragment_root, getResString(R.string.stopwatch))
    )

    @Before
    fun setUp() {
        // Initialize Hilt before each test
        hiltRule.inject()
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }


    @Test
    fun test_initialViewComponentVisibilityBasedOnScreenSize() {

        val screenWidthDp = getSmallestScreenWidth()
        verifyToolbarIsVisible()
        verifyNavHostFragmentIsVisible()
        when {
            screenWidthDp >= 840 -> verifyLargeTabletLayout()
            screenWidthDp >= 600 -> verifyTabletLayout()
            else -> verifyPhoneLayout()
        }
    }

    @Test
    fun test_homeActivityLaunchByDefault_displayAlarmFragment_With_CorrectToolbarTitle_And_SelectCorrectNavItem() {
        onView(withId(R.id.alarm_fragment_root)).check(matches(isDisplayed()))
        onView(withId(R.id.home_toolbar)).check(matches(withToolbarTitle(getResString(R.string.alarm))))
        onView(withId(getNavComponentIdForCurrentWidth())).check(matches(hasSelectedItemId(R.id.alarmFragment)))
    }



    @Test
    fun test_bottomNavNavigation_forPortraitPhoneLayout() {
        val screenWidthDp = getCurrentScreenWidth()

        // Only run this test if the screen width is for phones (< 600dp)
        if (screenWidthDp < 600) {
            verifyRequiredUIComponentsVisible(BottomNavigationView::class.java)
            topLevelDestinations.forEach { (menuId, rootId, expectedTitle) ->
                navigateAndVerify(menuId, rootId, expectedTitle)
                navigateAndVerify(menuId, rootId, expectedTitle, true)
            }
        }
    }

    @Test
    fun test_navigationRailNavigation_forTabletLayout() {
        val screenWidthDp = getCurrentScreenWidth()

        // Only run this test if the screen width is for tablets with Navigation Rail (600 <= width < 840)
        if (screenWidthDp in 600..839) {
            verifyRequiredUIComponentsVisible(NavigationRailView::class.java)
            topLevelDestinations.forEach { (menuId, rootId, expectedTitle) ->
                navigateAndVerify(menuId, rootId, expectedTitle)
                navigateAndVerify(menuId, rootId, expectedTitle, true)
            }
        }
    }

    @Test
    fun test_navigationViewNavigation_forLargeTabletLayout() {
        val screenWidthDp = getCurrentScreenWidth()

        // Only run this test if the screen width is for large tablets (>= 840dp)
        if (screenWidthDp >= 840) {
            verifyRequiredUIComponentsVisible(NavigationView::class.java)
            topLevelDestinations.forEach { (menuId, rootId, expectedTitle) ->
                navigateAndVerify(menuId, rootId, expectedTitle)
                navigateAndVerify(menuId, rootId, expectedTitle, true)
            }
        }
    }

    @Test
    fun test_navRailNavigation_forLandscapePhoneLayout() {

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val instrumentation = InstrumentationRegistry.getInstrumentation()

        // 1. Rotate the device
        device.setOrientationLeft()

        // 2. Wait for the system to settle (The "Pro" way)
        // This waits for the idle state where the orientation change is complete
        instrumentation.waitForIdleSync()


        var isPhoneAndLandscape = false

        // 3. Use ActivityScenario to ensure we are looking at the NEW activity instance
        activityScenarioRule.scenario.onActivity { activity ->

            val config = activity.resources.configuration
            val isPhone = config.smallestScreenWidthDp < 600
            val isLandscape = config.orientation == Configuration.ORIENTATION_LANDSCAPE
            isPhoneAndLandscape = isPhone && isLandscape
        }

        if (isPhoneAndLandscape) {

            // Logic only runs if the hardware state is confirmed
            verifyRequiredUIComponentsVisible(BottomNavigationView::class.java)

            topLevelDestinations.forEach { (menuId, rootId, expectedTitle) ->
                navigateAndVerify(menuId, rootId, expectedTitle)
            }
        }

        // Reset orientation
        device.setOrientationNatural()
        instrumentation.waitForIdleSync()
    }

    @Test
    fun test_toolbarSettingsMenu_opensSettingsActivity() {
        // 1. Open the overflow menu in the toolbar
        // Since your toolbar is a MaterialToolbar, we can use the standard open options menu
        openActionBarOverflowOrOptionsMenu(getApplicationContext())

        // 2. Click on the "Settings" menu item
        // We look for the text defined in your home_toolbar_menu.xml
        onView(withText(R.string.settings)).perform(click())

        // 3. Verify that an Intent to SettingActivity was sent
        intended(hasComponent(SettingActivity::class.java.name))
    }

    @Test
    fun test_toolbarSettingsMenu_opensHelpActivity() {
        // 1. Open the overflow menu in the toolbar
        // Since your toolbar is a MaterialToolbar, we can use the standard open options menu
        openActionBarOverflowOrOptionsMenu(getApplicationContext())

        // 2. Click on the "Settings" menu item
        // We look for the text defined in your home_toolbar_menu.xml
        onView(withText(R.string.help)).perform(click())

        // 3. Verify that an Intent to SettingActivity was sent
        intended(hasComponent(HelpActivity::class.java.name))
    }

    @Test
    fun test_SystemBackPress_FinishesActivity() {
        // 1. Ensure we are on the start destination (e.g., Alarm)
        // This is important because back usually goes 'up' unless at the root.
        onView(withId(R.id.alarm_fragment_root)).check(matches(isDisplayed()))

        // 2. Trigger the system back button using onBackPressedDispatcher
        activityScenarioRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed() // Simulates the system back press
        }

        // 3. Verify the activity is finishing or destroyed
        activityScenarioRule.scenario.onActivity { activity ->
            assertTrue(
                "Activity should be finishing after back press at root",
                activity.isFinishing
            )
        }
    }





    /// My Helper

    private fun verifyToolbarIsVisible() {
        onView(withId(R.id.home_toolbar)).check(matches(isDisplayed()))
    }

    private fun verifyNavHostFragmentIsVisible() {
        onView(
            allOf(
                withId(R.id.home_fragment_container_view),
                withParent(withId(R.id.home_activity))
            )
        ).check(matches(isDisplayed()))
    }

    private fun verifyLargeTabletLayout() {
        onView(withId(R.id.navigation_view))
            .check(matches(isDisplayed()))
            .check(matches(instanceOf(NavigationView::class.java)))

        onView(withId(R.id.bottom_nav)).check(doesNotExist())
        onView(withId(R.id.navigation_rail)).check(doesNotExist())
    }

    private fun verifyTabletLayout() {
        onView(withId(R.id.navigation_rail))
            .check(matches(isDisplayed()))
            .check(matches(instanceOf(NavigationRailView::class.java)))

        onView(withId(R.id.bottom_nav)).check(doesNotExist())
        onView(withId(R.id.navigation_view)).check(doesNotExist())
    }

    private fun verifyPhoneLayout() {
        onView(withId(R.id.bottom_nav))
            .check(matches(isDisplayed()))
            .check(matches(instanceOf(BottomNavigationView::class.java)))

        onView(withId(R.id.navigation_rail)).check(doesNotExist())
        onView(withId(R.id.navigation_view)).check(doesNotExist())
    }




    // ------------------------
    // Helper Methods
    // -------------------------

    private fun getResString(resId : Int) : String{
        return getApplicationContext<Context>().getString(resId)
    }

    private fun getSmallestScreenWidth() : Int {
        var smallestScreenWidth = 0
        activityScenarioRule.scenario.onActivity { activity ->
            smallestScreenWidth = activity.resources.configuration.smallestScreenWidthDp
        }
        return smallestScreenWidth
    }

    private fun getCurrentScreenWidth(): Int {
        var sw = 0
        activityScenarioRule.scenario.onActivity { activity ->
            sw = activity.resources.configuration.screenWidthDp
        }
        return sw
    }

    private fun getNavComponentIdForCurrentWidth() : Int {
        val screenWidthDp = getSmallestScreenWidth()
        return  when {
            screenWidthDp >= 840 -> R.id.navigation_view
            screenWidthDp >= 600 -> R.id.navigation_rail
            else -> R.id.bottom_nav
        }
    }

    private fun verifyRequiredUIComponentsVisible(expectedNavClass: Class<out View>) {

        // Verify Toolbar exists and is visible
        onView(withId(R.id.home_toolbar)).check(matches(isDisplayed()))

        // Verify the NavHostFragment container is visible
        onView(
            allOf(
                withId(R.id.home_fragment_container_view),
                withParent(withId(R.id.home_activity))
            )
        ).check(matches(isDisplayed()))

        // Verify navigation_container is the CORRECT type for this layout
        onView(withId(R.id.navigation_rail)).check(
            matches(
                allOf(
                    isDisplayed(),
                    isAssignableFrom(expectedNavClass)
                )
            )
        )
    }

    private fun navigateAndVerify(menuId: Int, rootId: Int, expectedTitle: String, clickAgain: Boolean = false) {

        // Perform Click on the Nav Item
        onView(withId(menuId)).perform(click())

        if (clickAgain) {
            onView(withId(menuId)).perform(click())
        }

        // Assert: The correct Fragment Root View is now visible
        onView(withId(rootId)).check(matches(isDisplayed()))

        // Assert: Toolbar title updated to match the fragment
        onView(withId(R.id.home_toolbar)).check(matches(withToolbarTitle(expectedTitle)))

        // Assert: Item shows as selected in the Nav Component
        onView(withId(menuId)).check(matches(isSelected()))
    }

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

    fun hasSelectedItemId(expectedId: Int): Matcher<View> {
        return object : BoundedMatcher<View, View>(View::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has selected item id: $expectedId")
            }

            override fun matchesSafely(view: View): Boolean {
                return when (view) {
                    is NavigationBarView -> view.selectedItemId == expectedId // BottomNav & Rail
                    is NavigationView -> {
                        val menu = view.menu
                        for (i in 0 until menu.size()) {
                            val item = menu.getItem(i)
                            if (item.isChecked && item.itemId == expectedId) return true
                        }
                        false
                    }
                    else -> false
                }
            }
        }
    }



}