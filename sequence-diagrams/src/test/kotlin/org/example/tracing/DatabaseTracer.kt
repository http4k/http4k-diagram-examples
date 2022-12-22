package org.example.tracing

import org.example.DatabaseCall
import org.http4k.events.MetadataEvent
import org.http4k.tracing.Trace
import org.http4k.tracing.TraceActor
import org.http4k.tracing.TraceStep
import org.http4k.tracing.Tracer
import org.http4k.tracing.app

object DatabaseTracer : Tracer {
    override fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer) =
        parent
            .takeIf { it.event is DatabaseCall }
            ?.let { listOf(DatabaseTrace(it.app(), (it.event as DatabaseCall).name)) }
            ?: emptyList()
}

class DatabaseTrace(
    override val origin: String,
    methodName: String,
) : Trace, TraceStep {
    override val target = "db"
    override val request = methodName
    override val response = ""
    override val originActor = TraceActor.Internal(origin)
    override val targetActor = TraceActor.Database(target)
    override val children = emptyList<Trace>()
}