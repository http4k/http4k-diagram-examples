package org.example

import org.http4k.events.MetadataEvent
import org.http4k.tracing.BiDirectional
import org.http4k.tracing.OriginNamer
import org.http4k.tracing.Trace
import org.http4k.tracing.TraceActor
import org.http4k.tracing.Tracer

class DomainEventTracer(private val origin: OriginNamer) : Tracer {
    override fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer) =
        parent
            .takeIf { it.event is DomainEvent }
            ?.let { listOf(Trace.DomainEvent(origin(it), (it.event as DomainEvent).name)) }
            ?: emptyList()
}

private fun Trace.Companion.DomainEvent(
    origin: String,
    request: String
) = BiDirectional(
    origin,
    "events",
    TraceActor.Internal(origin),
    TraceActor.Events("events"),
    request,
    emptyList()
)
