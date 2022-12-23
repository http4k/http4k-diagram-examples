package org.example

import org.http4k.tracing.HttpTracer
import org.http4k.tracing.TracePersistence
import org.http4k.tracing.TraceRenderPersistence
import org.http4k.tracing.junit.TracedEvents
import org.http4k.tracing.persistence.FileSystem
import org.http4k.tracing.renderer.PumlInteractionDiagram
import org.http4k.tracing.renderer.PumlInteractionFlowDiagram
import org.http4k.tracing.renderer.PumlSequenceDiagram
import java.io.File

fun CustomTracedEvents(app: String, testVariant: String? = null) = TracedEvents(
    app,
    testVariant,
    listOf(HttpTracer(AppName), DatabaseTracer(AppName), DomainEventTracer(AppName)),
    listOf(PumlSequenceDiagram, PumlInteractionDiagram, PumlInteractionFlowDiagram),
    FILE_TRACE_RENDER_PERSISTENCE,
    FILE_TRACE_PERSISTENCE
)

val FILE_TRACE_PERSISTENCE = TracePersistence.FileSystem(File("build/traces"))
val FILE_TRACE_RENDER_PERSISTENCE = TraceRenderPersistence.FileSystem(File(".generated/diagrams"))

