package com.example.smartalarm.integration.timer

/**
 * Integration tests for the TimerDao class, which performs CRUD operations
 * on the `timer_table` in the Room database.
 *
 * These tests ensure that the DAO methods for saving, retrieving, deleting,
 * and converting between model and entity are working as expected.
 *
 * ## Test Coverage:
 * 1. **testGetTimers**: Tests that the list of timers can be retrieved from the database.
 * 2. **testDeleteTimer**: Tests that a timer can be deleted by its `timerId`.
 * 3. **testSaveTimerAndConvertToModel**: Tests that a `TimerModel` can be saved as a `TimerEntity` and retrieved back with proper data conversion.
 *
 * Each test runs on an in-memory Room database and validates the specific functionality
 * of the DAO methods and model-to-entity conversions.
 */
class TimerDaoIT {

/*    private lateinit var database: MyDatabase
    private lateinit var timerDao: TimerDao

    *//**
     * Sets up an in-memory database before each test.
     * Initializes the `TimerDao` to interact with the database.
     *//*
    @Before
    fun setup() {
        // Create an in-memory Room database for testing
        val context = InstrumentationRegistry.getInstrumentation().context
        database = Room.inMemoryDatabaseBuilder(
            context, MyDatabase::class.java
        ).allowMainThreadQueries().build()

        timerDao = database.timerDao()
    }

    *//**
     * Closes the in-memory database after each test.
     *//*
    @After
    fun tearDown() {
        database.close() // Clean up the database after the test
    }

    *//**
     * Test to verify that timers can be retrieved correctly from the database.
     *
     * It saves a `TimerEntity` to the database and checks if it can be fetched.
     *//*
    @Test
    fun testGetTimers() = runTest {
        // Arrange
        val timerEntity = TimerEntity(
            startTimeMillis = System.currentTimeMillis(),
            remainingMillis = 60000,
            endTimeMillis = System.currentTimeMillis() + 60000,
            targetDurationMillis = System.currentTimeMillis() + 60000,
            isTimerRunning = true,
            isTimerSnoozed = false,
            snoozedTargetDurationMillis = 0,
            state = TimerStatus.RUNNING.name
        )

        // Act
        timerDao.persistTimer(timerEntity)

        // Assert
        val timers = timerDao.getTimerList().first() // Get the first emission of the Flow
        Assert.assertNotNull(timers)
        Assert.assertTrue(timers.isNotEmpty()) // Ensure at least one timer is present
    }

    *//**
     * Test to verify that a timer can be deleted by its `timerId`.
     *
     * It saves a `TimerEntity` to the database, deletes it, and checks that it no longer exists.
     *//*
    @Test
    fun testDeleteTimer() = runTest {
        // Arrange
        val timerEntity = TimerEntity(
            startTimeMillis = System.currentTimeMillis(),
            remainingMillis = 60000,
            endTimeMillis = System.currentTimeMillis() + 60000,
            targetDurationMillis = System.currentTimeMillis() + 60000,
            isTimerRunning = true,
            isTimerSnoozed = false,
            snoozedTargetDurationMillis = 0,
            state = TimerStatus.RUNNING.name
        )

        // Act
        timerDao.persistTimer(timerEntity)
        timerDao.deleteTimerById(timerEntity.id)

        // Assert
        val timers = timerDao.getTimerList().first()
        Assert.assertTrue(timers.none { it.id == timerEntity.id }) // Assert that no timer with that ID exists
    }

    *//**
     * Test to verify that a `TimerModel` can be converted to a `TimerEntity`,
     * saved to the database, and then properly retrieved back.
     *
     * This test checks that the data conversion between model and entity is correct.
     *//*
    @Test
    fun testSaveTimerAndConvertToModel() = runTest {
        // Arrange
        val timerModel = TimerModel(
            timerId = 0, // Let Room auto-generate the ID
            startTime = System.currentTimeMillis(),
            remainingTime = 60000,
            endTime = System.currentTimeMillis() + 60000,
            targetTime = System.currentTimeMillis() + 60000,
            isTimerRunning = true,
            isTimerSnoozed = false,
            snoozedTargetTime = 0,
            status = TimerStatus.RUNNING
        )

        // Act
        val timerEntity = timerModel.toEntity() // Convert TimerModel to TimerEntity
        timerDao.persistTimer(timerEntity)

        // Assert
        val timers = timerDao.getTimerList().first()
        Assert.assertNotNull(timers)
        Assert.assertTrue(timers.isNotEmpty())

        val savedTimer = timers[0]

        // Assert the conversion from model to entity was done correctly
        Assert.assertNotEquals(
            timerModel.timerId,
            savedTimer.id
        ) // Auto-generated ID in DB will not match
        Assert.assertEquals(timerModel.startTime, savedTimer.startTimeMillis)
        Assert.assertEquals(timerModel.remainingTime, savedTimer.remainingMillis)
        Assert.assertEquals(
            timerModel.status.name,
            savedTimer.state
        ) // Ensure state is converted properly
    }*/
}