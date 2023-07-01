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

    fun <T> assertContains(actual: List<T>, expected: List<T>) {
        assert(actual.containsAll(expected)) {
            """
               | EXPECT: $expected
               | ACTUAL: $actual
            """.trimMargin()
        }
    }
}