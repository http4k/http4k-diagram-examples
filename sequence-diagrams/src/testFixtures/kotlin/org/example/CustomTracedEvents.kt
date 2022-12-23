package org.example

import org.http4k.tracing.HttpTracer
import org.http4k.tracing.TracePersistence
import org.http4k.tracing.TraceRenderPersistence
import org.http4k.tracing.TracerBullet
import org.http4k.tracing.junit.TracedEvents
import org.http4k.tracing.persistence.FileSystem
import org.http4k.tracing.renderer.PumlSequenceDiagram
import java.io.File

fun CustomTracedEvents(app: String, testVariant: String? = null) = TracedEvents(
    app,
    testVariant,
    TraceRenderPersistence.FileSystem(File(".generated/diagrams")),
    TracerBullet(HttpTracer, DatabaseTracer, DomainEventTracer),
    listOf(PumlSequenceDiagram),
    FILE_TRACE_PERSISTENCE
)

val FILE_TRACE_PERSISTENCE = TracePersistence.FileSystem(File("build/traces"))
