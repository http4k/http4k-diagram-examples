package org.example

import org.http4k.tracing.TracePersistence
import org.http4k.tracing.TraceRenderPersistence
import org.http4k.tracing.junit.RecordingMode
import org.http4k.tracing.junit.TraceNamer
import org.http4k.tracing.junit.TracerBulletEvents
import org.http4k.tracing.persistence.FileSystem
import org.http4k.tracing.renderer.PumlInteractionDiagram
import org.http4k.tracing.renderer.PumlInteractionFlowDiagram
import org.http4k.tracing.renderer.PumlSequenceDiagram
import org.http4k.tracing.tracer.HttpTracer
import java.io.File

fun CustomTracingEvents(recordingMode: RecordingMode) = TracerBulletEvents(
    listOf(HttpTracer(AppName), DatabaseTracer(AppName), DomainEventTracer(AppName)),
    listOf(PumlSequenceDiagram, PumlInteractionDiagram, PumlInteractionFlowDiagram),
    FILE_TRACE_RENDER_PERSISTENCE,
    Namer(recordingMode),
    FILE_TRACE_PERSISTENCE,
    recordingMode
)

val FILE_TRACE_PERSISTENCE = TracePersistence.FileSystem(File("build/traces"))
val FILE_TRACE_RENDER_PERSISTENCE = TraceRenderPersistence.FileSystem(File(".generated/diagrams"))

fun Namer(recordingMode: RecordingMode) = TraceNamer{ it.testClass.get().simpleName + " " + it.testMethod.get().name + " (" + recordingMode.name + ")" }
