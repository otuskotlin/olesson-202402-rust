import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class RustCallbackTest {
    @Test
    fun asyncOneTest() = runTest(timeout = 30.seconds) {
        val cont = RustCallback()
        assertEquals(5, cont.addExt(2, 3))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun asyncTwoTest() = runTest(
        timeout = 30.seconds
    ) {
        val cont = RustCallback()
        withContext(Dispatchers.IO.limitedParallelism(2)) {
            val r1 = async { cont.addExt(6, 3) }
            val r2 = async { cont.addExt(9, 3) }
            assertEquals(9, r1.await())
            assertEquals(12, r2.await())
        }
    }
}
