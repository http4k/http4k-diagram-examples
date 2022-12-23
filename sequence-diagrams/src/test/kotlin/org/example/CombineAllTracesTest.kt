package org.example

import org.junit.jupiter.api.Test

class CombineAllTracesTest {

    @Test
    fun `combine into one list`() {
        println(
            tracePersistence.load()
                .flatMap { it.traces }
                .map { it.origin to it.target }
                .toSet()
        )
    }
}
