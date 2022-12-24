package org.http4k.tracing.junit

import org.http4k.tracing.TracePersistence
import org.http4k.tracing.TraceRenderPersistence
import org.http4k.tracing.junit.RecordingMode.AUTO
import org.http4k.tracing.persistence.InMemory
import org.http4k.tracing.persistence.InMemoryTraceRenderPersistence
import org.junit.jupiter.api.Test

class TracingEventsTest {

    @Test
    fun `does not write empty traces`() {
        val renders = InMemoryTraceRenderPersistence()
        val events = tracingEvents(renders, TracePersistence.InMemory())
    }

    private fun tracingEvents(
        renderPersistence: TraceRenderPersistence,
        tracePersistence: TracePersistence = TracePersistence.InMemory(),
        recordingMode: RecordingMode = AUTO
    ) = TracingEvents(
        "title", "variant",
        listOf(),
        listOf(),
        renderPersistence,
        tracePersistence,
        recordingMode
    )
}