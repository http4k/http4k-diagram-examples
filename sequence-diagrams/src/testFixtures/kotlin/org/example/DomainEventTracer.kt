package org.example

import org.http4k.events.MetadataEvent
import org.http4k.tracing.ActorType.Queue
import org.http4k.tracing.ActorType.System
import org.http4k.tracing.BiDirectional
import org.http4k.tracing.TraceActor
import org.http4k.tracing.TraceActorResolver
import org.http4k.tracing.Tracer

class DomainEventTracer(private val origin: TraceActorResolver) : Tracer {
    override fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer) = parent
        .takeIf { it.event is DomainEvent }
        ?.let {
            BiDirectional(
                origin(it),
                "events",
                TraceActor(origin(it), System),
                TraceActor("events", Queue),
                (it.event as DomainEvent).name,
                emptyList()
            )
        }
        ?.let(::listOf)
        ?: emptyList()
}

