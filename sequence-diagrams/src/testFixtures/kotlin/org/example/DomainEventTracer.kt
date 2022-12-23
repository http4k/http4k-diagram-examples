package org.example

import org.http4k.events.MetadataEvent
import org.http4k.tracing.BiDirectional
import org.http4k.tracing.Trace
import org.http4k.tracing.TraceActor
import org.http4k.tracing.Tracer
import org.http4k.tracing.app

object DomainEventTracer : Tracer {
    override fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer) =
        parent
            .takeIf { it.event is DomainEvent }
            ?.let { listOf(Trace.DomainEvent(it.app(), (it.event as DomainEvent).name)) }
            ?: emptyList()
}

fun Trace.Companion.DomainEvent(
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
