package com.example.smartalarm.feature.stopwatch.presentation.mapper

import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.stopwatch.domain.model.StopWatchLapModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchLapUiModel
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchUiModel
import io.mockk.every
import org.junit.Before
import kotlin.test.Test
import com.example.smartalarm.R
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import org.junit.After
import kotlin.test.assertEquals


/**
 * Unit tests for the [StopwatchUiMapper] class.
 *
 * This class tests the functionality of the [StopwatchUiMapper] to ensure it correctly maps
 * domain models (StopWatchModel and StopWatchLapModel) to their UI representations
 * (StopwatchUiModel and StopwatchLapUiModel respectively).
 *
 * The tests use MockK to mock dependencies like [ResourceProvider], [NumberFormatter], and
 * [TimeFormatter] to verify the behavior of the mapping logic without relying on the actual
 * implementations of these classes.
 */
class StopwatchUiMapperTest {

    // Mocks for dependencies of StopwatchUiMapper
    @MockK
    private lateinit var mockResourceProvider: ResourceProvider

    @MockK
    private lateinit var mockNumberFormatter: NumberFormatter

    @MockK
    private lateinit var mockTimeFormatter: TimeFormatter

    @InjectMockKs
    private lateinit var stopwatchUiMapper: StopwatchUiMapper

    /**
     * Sets up the mock objects and injects them into the [StopwatchUiMapper] before each test.
     * This method initializes MockK annotations to prepare the mocks.
     */
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    /** Clear all mocks after each */
    @After
    fun tearDown() {
        unmockkAll()
    }



    /**
     * Test case for [StopwatchUiMapper.mapToUiModel] to verify that it correctly converts
     * a [StopwatchModel] to a [StopwatchUiModel].
     *
     * This test checks that:
     * - The stopwatch time is formatted correctly.
     * - The lap information is properly mapped, including the lap index and elapsed times.
     *
     * @see StopwatchUiMapper.mapToUiModel
     */
    @Test
    fun `test mapToUiModel should return correct UI model for StopwatchModel`() {

        // Arrange: A StopwatchModel with elapsed time and lap times
        val stopwatchModel = StopwatchModel(
            elapsedTime = 150500L,
            isRunning = true,
            lapTimes = listOf(
                StopWatchLapModel(1, 1000L, 4000L, 5000L),
                StopWatchLapModel(2, 6000L, 8000L, 9000L)
            )
        )

        // Mocks for time formatting and resource provider
        every { mockTimeFormatter.formatDurationForStopwatch(150500L, false) } returns "25s"
        every { mockTimeFormatter.formatMillisForStopwatch(150500L) } returns "500ms"

        every { mockTimeFormatter.formatDurationForStopwatch(1000L, true) } returns "00:00:01:00"
        every { mockTimeFormatter.formatDurationForStopwatch(4000L, true) } returns "00:00:04:00"
        every { mockTimeFormatter.formatDurationForStopwatch(5000L, true) } returns "00:00:05:00"
        every { mockTimeFormatter.formatDurationForStopwatch(6000L, true) } returns "00:00:06:00"
        every { mockTimeFormatter.formatDurationForStopwatch(8000L, true) } returns "00:00:08:00"
        every { mockTimeFormatter.formatDurationForStopwatch(9000L, true) } returns "00:00:09:00"
        every { mockResourceProvider.getString(R.string.lap_index) } returns "# Lap"
        every { mockNumberFormatter.formatLocalizedNumber(1L, false) } returns "1"
        every { mockNumberFormatter.formatLocalizedNumber(2L, false) } returns "2"

        // Act: Mapping the StopwatchModel to a UI model
        val result = stopwatchUiMapper.mapToUiModel(stopwatchModel)

        // Assert: Verifying the expected output
        assertEquals("25s", result.secondsText)
        assertEquals("500ms", result.milliSecondsText)
        assertEquals(true, result.isRunning)
        assertEquals(2, result.laps.size)

        // Check that each lap was mapped correctly
        val firstLap = result.laps[0]
        assertEquals("# Lap 1", firstLap.formattedLapIndex)
        assertEquals("00:00:01:00", firstLap.formattedLapStartTime)
        assertEquals("00:00:04:00", firstLap.formattedLapElapsedTime)
        assertEquals("00:00:05:00", firstLap.formattedLapEndTime)

        val secondLap = result.laps[1]
        assertEquals("# Lap 2", secondLap.formattedLapIndex)
        assertEquals("00:00:06:00", secondLap.formattedLapStartTime)
        assertEquals("00:00:08:00", secondLap.formattedLapElapsedTime)
        assertEquals("00:00:09:00", secondLap.formattedLapEndTime)
    }


    /**
     * Test case for [StopwatchUiMapper.mapLapToUiModel] to verify that it correctly converts
     * a [StopWatchLapModel] to a [StopwatchLapUiModel].
     *
     * This test checks that:
     * - The lap index is correctly formatted.
     * - The lap start time, elapsed time, and end time are formatted correctly.
     *
     * @see StopwatchUiMapper.mapLapToUiModel
     */
    @Test
    fun `test mapLapToUiModel should return correct UI model for StopwatchLapModel`() {

        // Arrange: A StopWatchLapModel representing a single lap
        val lapModel = StopWatchLapModel(
            lapIndex = 1,
            lapStartTime = 1000L,
            lapElapsedTime = 2000L,
            lapEndTime = 2000L
        )

        // Mocks for formatting the lap data
        every { mockResourceProvider.getString(R.string.lap_index) } returns "# Lap"
        every { mockTimeFormatter.formatDurationForStopwatch(1000L, true) } returns "00:00:01:00"
        every { mockTimeFormatter.formatDurationForStopwatch(2000L, true) } returns "00:00:02:00"
        every { mockNumberFormatter.formatLocalizedNumber(1L, false) } returns "1"

        // Act: Mapping the StopWatchLapModel to a UI lap model
        val result = stopwatchUiMapper.mapLapToUiModel(lapModel)

        // Assert: Verifying the expected output
        assertEquals("# Lap 1", result.formattedLapIndex)
        assertEquals("00:00:01:00", result.formattedLapStartTime)
        assertEquals("00:00:02:00", result.formattedLapElapsedTime)
        assertEquals("00:00:02:00", result.formattedLapEndTime)

    }
}

