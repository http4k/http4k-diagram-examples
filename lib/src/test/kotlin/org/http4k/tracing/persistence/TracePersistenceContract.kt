package org.http4k.tracing.persistence

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.tracing.NamedTrace
import org.http4k.tracing.TracePersistence
import org.http4k.tracing.bidi_b
import org.http4k.tracing.entire_trace
import org.http4k.tracing.event_a
import org.junit.jupiter.api.Test

interface TracePersistenceContract {
    val persistence: TracePersistence

    @Test
    fun `can store and retrieve traces`() {
        with(persistence) {
            store(NamedTrace("trace3", listOf(entire_trace)))
            store(NamedTrace("trace2", listOf(bidi_b)))
            store(NamedTrace("trace1", listOf(event_a)))

            assertThat(
                load().toSet(), equalTo(
                    setOf(
                        NamedTrace("trace3", listOf(entire_trace)),
                        NamedTrace("trace2", listOf(bidi_b)),
                        NamedTrace("trace1", listOf(event_a))
                    )
                )
            )
        }
    }
}