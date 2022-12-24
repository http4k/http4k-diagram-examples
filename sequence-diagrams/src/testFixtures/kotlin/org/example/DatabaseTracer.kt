package org.example

import org.http4k.events.MetadataEvent
import org.http4k.tracing.ActorType.Database
import org.http4k.tracing.ActorType.System
import org.http4k.tracing.BiDirectional
import org.http4k.tracing.TraceActor
import org.http4k.tracing.TraceActorResolver
import org.http4k.tracing.Tracer

class DatabaseTracer(private val origin: TraceActorResolver) : Tracer {
    override fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer) = parent
        .takeIf { it.event is DatabaseCall }
        ?.let {
            BiDirectional(
                origin(it),
                "db",
                TraceActor(origin(it), System),
                TraceActor("db", Database),
                (it.event as DatabaseCall).name,
                emptyList()
            )
        }
        ?.let { listOf(it) }
        ?: emptyList()
}

