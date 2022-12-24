package org.http4k.tracing.junit

import org.http4k.tracing.TracePersistence
import org.http4k.tracing.TraceRenderPersistence
import org.http4k.tracing.junit.RecordingMode.AUTO
import org.http4k.tracing.persistence.InMemory
import org.http4k.tracing.persistence.NoOp
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class TracingEventsTest {

    @Test
    @Disabled
    fun `test`() {
        TracingEvents(
            "title", "variant",
            listOf(),
            listOf(),
            TraceRenderPersistence.NoOp,
            TracePersistence.InMemory(),
            AUTO
        )
    }
}