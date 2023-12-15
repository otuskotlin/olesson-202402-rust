import kotlin.test.Test
import kotlin.test.assertEquals

class RustSimpleTest {
    @Test
    fun simpleTest() {
        val cont = RustSimple()
        assertEquals(5, cont.addExt(2, 3))
    }
}
