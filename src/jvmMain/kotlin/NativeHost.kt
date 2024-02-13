package ru.otus.otuskotlin

@Suppress("unused")
class NativeHost(val coeff: Int) {
    external fun callInt(x: Int) : Int

    companion object {
        init {
            val platformStr = System.getProperty("os.arch")
            val osStr = System.getProperty("os.name")
            println("PLATFORM: $platformStr $osStr")
            System.loadLibrary("jni")
        }

    }
}
