package com.example.smartalarm.integration.stopwatch

import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi

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