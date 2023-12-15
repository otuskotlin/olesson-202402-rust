import kotlinx.cinterop.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import rust.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalForeignApi::class)
class RustSuspend {
    data class SomeData(
        var res: Int
    )

    suspend fun addExt(x: Int, y: Int): Int = memScoped {
        withContext(Dispatchers.IO) {
            val result = SomeData(0)
            cValue<RustStructQuery> {
                left = x
                right = y
            }.usePinned {
                StableRef.create(result).usePinned { expRes ->
                    addRustAsync(
                        it.get().ptr,
                        staticCFunction { ptr: CPointer<RustStructResult>?, udat: COpaquePointer? ->
                            val dat = ptr?.pointed ?: throw Exception("Empty result")
                            val typed = udat?.asStableRef<SomeData>() ?: throw Exception("No stableRef provided")
                            typed.get().res = dat.sum
                        },
                        expRes.get().asCPointer()
                    )
                }
            }
            result.res
        }
    }
}
