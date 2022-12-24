package org.example

import org.http4k.events.MetadataEvent
import org.http4k.tracing.BiDirectional
import org.http4k.tracing.OriginNamer
import org.http4k.tracing.Trace
import org.http4k.tracing.TraceActor
import org.http4k.tracing.Tracer

class DatabaseTracer(private val origin: OriginNamer) : Tracer {
    override fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer) = parent
        .takeIf { it.event is DatabaseCall }
        ?.let { listOf(Trace.Database(origin(it), (it.event as DatabaseCall).name)) }
        ?: emptyList()
}

private fun Trace.Companion.Database(origin: String, request: String) = BiDirectional(
    origin,
    "db",
    TraceActor.Internal(origin),
    TraceActor.Database("db"),
    request,
    emptyList()
)
