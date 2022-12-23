package org.example

import org.http4k.events.MetadataEvent
import org.http4k.tracing.BiDirectional
import org.http4k.tracing.Trace
import org.http4k.tracing.TraceActor
import org.http4k.tracing.Tracer
import org.http4k.tracing.app

object DatabaseTracer : Tracer {
    override fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer) =
        parent
            .takeIf { it.event is DatabaseCall }
            ?.let { listOf(Trace.Database(it.app(), (it.event as DatabaseCall).name)) }
            ?: emptyList()
}

fun Trace.Companion.Database(origin: String, request: String) = BiDirectional(
    origin,
    "db",
    TraceActor.Internal(origin),
    TraceActor.Database("db"),
    request,
    emptyList()
)
