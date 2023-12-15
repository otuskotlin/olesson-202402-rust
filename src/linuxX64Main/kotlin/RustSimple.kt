import kotlinx.cinterop.ExperimentalForeignApi
import rust.*

@OptIn(ExperimentalForeignApi::class)
class RustSimple {
    fun addExt(x: Int, y: Int): Int {
        return addRust(x, y)
    }
}
