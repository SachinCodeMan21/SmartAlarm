package com.example.smartalarm.feature.home.presentation.viewmodel

import com.example.smartalarm.core.framework.sharedPreference.contract.SharedPrefsHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher

/**
 * Unit tests for [HomeViewModel], validating event handling logic and the UI effects emitted through the `uiEffect` flow.
 *
 * The test suite ensures that:
 * - Navigation occurs correctly based on notification data or saved preferences.
 * - The last opened destination is restored properly from SharedPreferences.
 * - System back press triggers the appropriate finish activity effect.
 *
 * A [StandardTestDispatcher] is used for controlled coroutine scheduling during testing.
 * A mock [SharedPrefsHelper] is used to simulate and isolate SharedPreferences operations.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
//
//    private lateinit var sharedPrefsHelper: SharedPrefsHelper
//    private lateinit var homeViewModel: HomeViewModel
//
//    /**
//     * Initializes the test environment:
//     * - Mocks [SharedPrefsHelper] to provide predictable results.
//     * - Instantiates the [HomeViewModel] with the mocked dependencies.
//     */
//    @Before
//    fun setUp() {
//        sharedPrefsHelper = mockk()
//        homeViewModel = HomeViewModel(sharedPrefsHelper)
//    }
//
//    /**
//     * Resets mock states after each test to ensure no state leaks between tests.
//     */
//    @After
//    fun tearDown() {
//        unmockkAll()
//    }
//
//
//    //========================================================================
//    // NavigateToInitialDestination Test Methods
//    //========================================================================
//
//    /**
//     * Verifies that the ViewModel navigates to the provided notification destination
//     * when the notification destination ID is non-zero.
//     *
//     * - **Arrange:** Provides a non-zero notification destination ID.
//     * - **Act:** Triggers the `NavigateToInitialDestination` event.
//     * - **Assert:** Ensures the ViewModel emits the correct navigation effect.
//     */
//    @Test
//    fun `navigateToInitialDestination - notification destination ID is non-zero - navigates to the notification destination`() = runTest {
//
//        // Arrange
//        val notificationDestinationId = R.id.clockFragment
//
//        homeViewModel.uiEffect.test {
//
//            // Act
//            homeViewModel.handleEvent(HomeEvent.RestoreLastOpenedDestination(notificationDestinationId))
//
//            // Assert
//            assertEquals(HomeEffect.NavigateToChildFragment(notificationDestinationId), awaitItem())
//            cancelAndIgnoreRemainingEvents()
//        }
//    }
//
//    /**
//     * Verifies that when the notification destination ID is zero,
//     * the ViewModel restores the last opened destination from SharedPreferences.
//     *
//     * - **Arrange:** Mocks SharedPreferences to return the last opened destination.
//     * - **Act:** Triggers the `NavigateToInitialDestination` event with ID 0 (no notification).
//     * - **Assert:** Ensures the ViewModel emits a navigation effect to the restored destination.
//     */
//    @Test
//    fun `navigateToInitialDestination - notification destination ID is zero - restores last opened destination`() = runTest {
//
//        // Arrange
//        every { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs } returns R.id.alarmFragment
//
//        homeViewModel.uiEffect.test {
//
//            // Act
//            homeViewModel.handleEvent(HomeEvent.RestoreLastOpenedDestination(0))
//            advanceUntilIdle()
//
//            // Assert
//            assertEquals(HomeEffect.NavigateToChildFragment(R.id.alarmFragment), awaitItem())
//            cancelAndIgnoreRemainingEvents()
//            verify { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs }
//        }
//    }
//
//
//
//
//    //========================================================================
//    // RestoreLastOpenedHomeDestination Test Methods
//    //========================================================================
//
//    /**
//     * Verifies that when no destination is saved (i.e., last opened destination is `-1`),
//     * the ViewModel navigates to the default alarm fragment.
//     *
//     * - **Arrange:** Mocks SharedPreferences to return `-1`, indicating no saved destination.
//     * - **Act:** Triggers the `NavigateToInitialDestination` event with ID 0 (no notification).
//     * - **Assert:** Ensures the ViewModel emits a navigation effect to the default alarm fragment.
//     */
//    @Test
//    fun `restoreLastOpenedHomeDestination - no destination saved, should navigate to default alarm fragment`() = runTest {
//
//        // Arrange
//        every { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs } returns -1
//
//        homeViewModel.uiEffect.test {
//
//            // Act
//            homeViewModel.handleEvent(HomeEvent.RestoreLastOpenedDestination(0))
//
//            // Assert
//            assertEquals(HomeEffect.NavigateToChildFragment(R.id.alarmFragment), awaitItem())
//            cancelAndIgnoreRemainingEvents()
//            verify { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs }
//        }
//    }
//
//    /**
//     * Verifies that when a valid destination is saved, the ViewModel navigates to the saved destination.
//     *
//     * - **Arrange:** Mocks SharedPreferences to return a valid saved destination ID.
//     * - **Act:** Triggers the `NavigateToInitialDestination` event.
//     * - **Assert:** Ensures the ViewModel emits a navigation effect to the saved destination.
//     */
//    @Test
//    fun `restoreLastOpenedHomeDestination - valid destination saved, should navigate to saved destination`() = runTest {
//
//        // Arrange
//        val savedDestinationId = R.id.clockFragment
//        every { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs } returns savedDestinationId
//
//        homeViewModel.uiEffect.test {
//
//            // Act
//            homeViewModel.handleEvent(HomeEvent.RestoreLastOpenedDestination(0))
//
//            // Assert
//            assertEquals(HomeEffect.NavigateToChildFragment(savedDestinationId), awaitItem())
//            cancelAndIgnoreRemainingEvents()
//            verify { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs }
//
//        }
//    }
//
//    /**
//     * Verifies that when an invalid or corrupted destination is saved (negative or out-of-bound),
//     * the ViewModel still navigates to the default destination (alarm fragment).
//     *
//     * - **Arrange:** Mocks SharedPreferences to return an invalid destination ID (e.g., `-999`).
//     * - **Act:** Triggers the `NavigateToInitialDestination` event.
//     * - **Assert:** Ensures the ViewModel emits a navigation effect to the default alarm fragment.
//     */
//    @Test
//    fun `restoreLastOpenedHomeDestination - invalid destination saved, should navigate to default alarm fragment`() = runTest {
//
//        // Arrange
//        val invalidSavedDestinationId = -999
//        every { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs } returns invalidSavedDestinationId
//
//        homeViewModel.uiEffect.test {
//
//            // Act
//            homeViewModel.handleEvent(HomeEvent.RestoreLastOpenedDestination(0))
//
//            // Assert
//            assertEquals(HomeEffect.NavigateToChildFragment(R.id.alarmFragment), awaitItem())
//            cancelAndIgnoreRemainingEvents()
//            verify { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs }
//        }
//    }
//
//
//
//
//    //========================================================================
//    // HandleNavMenuItemSelection Test Methods
//    //========================================================================
//
//    /**
//     * Verifies that when the same destination is selected as the last saved destination,
//     * no effect is emitted, and the destination remains unchanged.
//     *
//     * - **Arrange:** Mocks SharedPreferences to return the same saved destination.
//     * - **Act:** Triggers the `HandleNavMenuItemSelection` event with the same destination ID.
//     * - **Assert:** Ensures no effect is emitted and SharedPreferences remain unchanged.
//     */
//    @Test
//    fun `handleNavMenuItemSelection - same destination as saved - emits no effect`() = runTest {
//
//        // Arrange
//        val selectedDestinationId = R.id.clockFragment
//        val savedDestinationId = R.id.clockFragment
//        every { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs } returns savedDestinationId
//
//        homeViewModel.uiEffect.test {
//
//            // Act
//            homeViewModel.handleEvent(HomeEvent.NavMenuItemSelected(selectedDestinationId))
//
//            // Assert
//            expectNoEvents()
//            verify { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs }
//        }
//    }
//
//    /**
//     * Verifies that when a different destination is selected than the saved one,
//     * the ViewModel updates the saved destination and emits the appropriate effect.
//     *
//     * - **Arrange:** Mocks SharedPreferences to return a different saved destination ID.
//     * - **Act:** Triggers the `HandleNavMenuItemSelection` event with a different destination ID.
//     * - **Assert:** Ensures the destination is updated in SharedPreferences and a navigation effect is emitted.
//     */
//    @Test
//    fun `handleNavMenuItemSelection - different destination - updates prefs and emits rotate nav menu item effect`() = runTest {
//
//        // Arrange
//        val selectedDestinationId = R.id.clockFragment
//        val savedDestinationId = R.id.alarmFragment
//        every { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs } returns savedDestinationId
//        every { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs = selectedDestinationId } answers {}
//
//        homeViewModel.uiEffect.test {
//
//            // Act
//            homeViewModel.handleEvent(HomeEvent.NavMenuItemSelected(selectedDestinationId))
//
//            // Assert
//            assertEquals(HomeEffect.RotateSelectedNavItemIcon(selectedDestinationId), awaitItem())
//            cancelAndIgnoreRemainingEvents()
//            verify { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs }
//            verify { sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs = selectedDestinationId }
//        }
//    }
//
//
//
//
//    //========================================================================
//    // SystemBackPressed Test Methods
//    //========================================================================
//
//    /**
//     * Verifies that when the system back button is pressed, the ViewModel emits a `FinishActivity` effect.
//     *
//     * - **Arrange:** Trigger the system back pressed event.
//     * - **Act:** Triggers the `HandleSystemBackPressed` event.
//     * - **Assert:** Ensures the ViewModel emits the `FinishActivity` effect.
//     */
//    @Test
//    fun `handleSystemBackPressed emits FinishActivity effect`() = runTest {
//
//        homeViewModel.uiEffect.test {
//
//            // Act
//            homeViewModel.handleEvent(HomeEvent.SystemBackPressed)
//
//            // Assert
//            assertEquals(HomeEffect.FinishActivity, awaitItem())
//            cancelAndIgnoreRemainingEvents()
//        }
//    }

}
