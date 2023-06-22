package io.violabs.core

object TestUtils {
    fun <T> assertEquals(expected: T, actual: T) {
        assert(expected == actual) {
            """
               | EXPECT: $expected
               | ACTUAL: $actual
            """.trimMargin()
        }
    }
}