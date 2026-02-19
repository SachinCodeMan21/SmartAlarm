package com.example.smartalarm.integration.stopwatch

import com.example.smartalarm.core.permission.PermissionManager
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.core.utility.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.fakes.stopwatch.FakePermissionManager
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract.BlinkEffectJobManager
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract.StopwatchTickerJobManager
import com.example.smartalarm.feature.stopwatch.presentation.mapper.StopwatchUiMapper
import com.example.smartalarm.feature.stopwatch.presentation.viewmodel.StopWatchViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

@HiltAndroidTest
@ExperimentalCoroutinesApi
class StopWatchViewModelIT {
/*
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var stopwatchUseCase: StopwatchUseCases

    @Inject
    lateinit var stopWatchUiMapper: StopwatchUiMapper

    @Inject
    lateinit var resourceProvider: ResourceProvider

    @Inject
    lateinit var stopwatchStateManager: StopwatchStateManager

    @Inject
    lateinit var blinkEffectJobManager: BlinkEffectJobManager

    @Inject
    lateinit var stopwatchTickerJobManager: StopwatchTickerJobManager

    @Inject
    lateinit var sharedPrefsHelper: SharedPrefsHelper

    private lateinit var viewModel: StopWatchViewModel
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var fakePermissionManager: PermissionManager


    @Before
    fun setup() {

        hiltRule.inject()

        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        // Mock only system-level permission checks
        fakePermissionManager = FakePermissionManager()

        // Initialize ViewModel with real dependencies
        viewModel = StopWatchViewModel(
            stopwatchUseCase = stopwatchUseCase,  // REAL
            stopWatchUiMapper = stopWatchUiMapper,  // REAL
            resourceProvider = resourceProvider,  // REAL
            blinkEffectJobManager = blinkEffectJobManager,  // REAL
            stopwatchTickerJobManager = stopwatchTickerJobManager,  // REAL
            stopwatchStateManager = stopwatchStateManager,  // REAL
            sharedPrefsHelper = sharedPrefsHelper,
            defaultDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() = runTest {
        // Clean up real database after each test
        Dispatchers.resetMain()
    }*/

}