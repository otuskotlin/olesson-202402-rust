import kotlinx.cinterop.*
import rust.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalForeignApi::class)
class RustStruct {
    fun addExt(x: Int, y: Int): Int = memScoped {
        @Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_AGAINST_NOT_NOTHING_EXPECTED_TYPE")
        cValue<RustStructQuery> {
            left = x
            right = y
        }.usePinned {
            return addRustStruct(it.get().ptr)
        }
    }

    fun addExt1(x: Int, y: Int): Int {
        val ptr = addRustReturn(x, y)
        val ctx = ptr?.pointed
        addRustReturnClean(ptr)
        val res = ctx?.sum ?: throw Exception("Empty result")
        if (ctx.status != SUCCESS) throw Exception("Wrong response status")
        return res
    }
}
