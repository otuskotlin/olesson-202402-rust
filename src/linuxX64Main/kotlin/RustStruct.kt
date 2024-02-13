import kotlinx.cinterop.*
import rust.*

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

    fun addExtUnsafe(x: Int, y: Int): Int {
        val ptr = addRustReturn(x, y)
        val ctx = ptr?.pointed
        val res = ctx?.sum ?: throw Exception("Empty result")
        val status = ctx.status
        addRustReturnClean(ptr)
        if (status != SUCCESS) throw Exception("Wrong response status")
        return res
    }

    fun addExtSafe(x: Int, y: Int): Int {
        val resStruct = addRustReturn1(x, y)
        val (res, status) = resStruct.useContents { Pair(sum, status) }
        if (status != SUCCESS) throw Exception("Wrong response status")
        return res
    }
}
