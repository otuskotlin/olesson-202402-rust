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
}
