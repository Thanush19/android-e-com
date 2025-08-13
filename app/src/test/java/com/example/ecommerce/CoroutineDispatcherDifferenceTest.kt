import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class CoroutineDispatcherDifferenceTest {

    @Test
    fun standardTestDispatcher_runsTasksOnlyOnAdvanceUntilIdle() = runTest {
        val results = mutableListOf<String>()
        val dispatcher = StandardTestDispatcher(testScheduler)
        launch(dispatcher) {
            results.add("start")
            results.add("end")
        }
        // At this point, nothing has run yet
        assertEquals(emptyList<String>(), results)
        // Run all tasks
        advanceUntilIdle()
        assertEquals(listOf("start", "end"), results)
    }

    @Test
    fun unconfinedTestDispatcher_runsImmediatelyAcrossSuspensions() = runTest {
        var state = "initial"
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        launch(dispatcher) {
            state = "started"
            delay(100)
            state = "resumed"
        }
        // "started" is set immediately, but "resumed" waits for delay
        assertEquals("started", state)
        // advanceUntilIdle() is still needed to progress virtual time for delays,
        // even though UnconfinedTestDispatcher runs code immediately on the current thread.
        advanceUntilIdle()
        assertEquals("resumed", state)
    }
}
