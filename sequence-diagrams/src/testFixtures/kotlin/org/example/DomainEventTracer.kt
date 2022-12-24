package org.example

import org.http4k.events.MetadataEvent
import org.http4k.tracing.Actor
import org.http4k.tracing.ActorResolver
import org.http4k.tracing.ActorType.Queue
import org.http4k.tracing.BiDirectional
import org.http4k.tracing.Tracer

class DomainEventTracer(private val actorFrom: ActorResolver) : Tracer {
    override fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer) = parent
        .takeIf { it.event is DomainEvent }
        ?.let {
            BiDirectional(
                actorFrom(it),
                Actor("events", Queue),
                (it.event as DomainEvent).name,
                emptyList()
            )
        }
        ?.let(::listOf)
        ?: emptyList()
}

