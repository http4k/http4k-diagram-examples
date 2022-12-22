package org.example.tracing

import org.http4k.tracing.AppName
import org.http4k.tracing.DatabaseTracer
import org.http4k.tracing.DomainEventTracer
import org.http4k.tracing.HttpTracer
import org.http4k.tracing.TracePersistence.Companion.FileSystem
import org.http4k.tracing.TracedEvents
import org.http4k.tracing.TracerBullet
import org.http4k.tracing.renderer.PumlSequenceDiagram
import java.io.File

fun CustomTracedEvents(
    app: AppName,
    testVariant: String? = null,
    print: Boolean = false
) = TracedEvents(
    app,
    testVariant,
    FileSystem(File(".generated/diagrams")),
    print,
    TracerBullet(HttpTracer, DatabaseTracer, DomainEventTracer),
    listOf(PumlSequenceDiagram)
)