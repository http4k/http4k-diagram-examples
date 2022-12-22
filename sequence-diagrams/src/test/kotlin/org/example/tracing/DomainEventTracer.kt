package org.example.tracing

import org.example.BusinessEvent
import org.http4k.events.MetadataEvent
import org.http4k.tracing.Trace
import org.http4k.tracing.TraceActor
import org.http4k.tracing.TraceStep
import org.http4k.tracing.Tracer
import org.http4k.tracing.app

object DomainEventTracer : Tracer {
    override fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer) =
        parent
            .takeIf { it.event is BusinessEvent }
            ?.let { listOf(DomainEventTrace(it.app(), (it.event as BusinessEvent).name)) }
            ?: emptyList()
}

class DomainEventTrace(
    override val origin: String,
    override val request: String,
) : Trace, TraceStep {
    override val target = "events"
    override val response = ""
    override val originActor = TraceActor.Internal(origin)
    override val targetActor = TraceActor.Events(target)
    override val children = emptyList<Trace>()
}