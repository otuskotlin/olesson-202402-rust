package ru.otus.otuskotlin

import kotlin.test.Test
import kotlin.test.assertEquals

class JniTest {
    @Test
    fun x() {
        val nh = NativeHost(15)
        val res = nh.callInt(5)
        assertEquals(75, res)
    }
}
