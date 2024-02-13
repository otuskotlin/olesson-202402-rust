import kotlin.test.Test
import kotlin.test.assertEquals

class RustStructTest {
    @Test
    fun structInTest() {
        val cont = RustStruct()
        assertEquals(5, cont.addExt(2, 3))
    }

    @Test
    fun structOutTest() {
        val cont = RustStruct()
        assertEquals(9, cont.addExt(6, 3))
    }

    @Test
    fun structOutUnsafeTest() {
        val cont = RustStruct()
        assertEquals(10, cont.addExtUnsafe(6, 4))
    }

    @Test
    fun structOutSafeTest() {
        val cont = RustStruct()
        assertEquals(11, cont.addExtSafe(6, 5))
    }
}
