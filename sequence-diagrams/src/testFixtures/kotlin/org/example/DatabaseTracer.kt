package org.example

import org.http4k.events.MetadataEvent
import org.http4k.tracing.Actor
import org.http4k.tracing.ActorResolver
import org.http4k.tracing.ActorType.Database
import org.http4k.tracing.BiDirectional
import org.http4k.tracing.Tracer

class DatabaseTracer(private val actorFrom: ActorResolver) : Tracer {
    override fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer) = parent
        .takeIf { it.event is DatabaseCall }
        ?.let {
            BiDirectional(
                actorFrom(it),
                Actor("db", Database),
                (it.event as DatabaseCall).name,
                emptyList()
            )
        }
        ?.let { listOf(it) }
        ?: emptyList()
}

