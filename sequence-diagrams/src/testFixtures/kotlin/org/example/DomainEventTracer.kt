package org.example

import org.http4k.events.MetadataEvent
import org.http4k.tracing.ActorType.Queue
import org.http4k.tracing.BiDirectional
import org.http4k.tracing.TraceActor
import org.http4k.tracing.TraceActorResolver
import org.http4k.tracing.Tracer

class DomainEventTracer(private val actorFrom: TraceActorResolver) : Tracer {
    override fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer) = parent
        .takeIf { it.event is DomainEvent }
        ?.let {
            BiDirectional(
                actorFrom(it).name,
                "events",
                actorFrom(it),
                TraceActor("events", Queue),
                (it.event as DomainEvent).name,
                emptyList()
            )
        }
        ?.let(::listOf)
        ?: emptyList()
}

