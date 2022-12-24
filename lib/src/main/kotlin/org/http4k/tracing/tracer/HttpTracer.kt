package org.http4k.tracing.tracer

import org.http4k.events.HttpEvent
import org.http4k.events.MetadataEvent
import org.http4k.tracing.ActorType.Person
import org.http4k.tracing.ActorType.System
import org.http4k.tracing.RequestResponse
import org.http4k.tracing.Trace
import org.http4k.tracing.TraceActor
import org.http4k.tracing.TraceActorResolver
import org.http4k.tracing.Tracer
import org.http4k.tracing.traces

fun HttpTracer(origin: TraceActorResolver) = Tracer { parent, rest, tracer ->
    parent
        .takeIf { it.event is HttpEvent.Outgoing }
        ?.let { it.toTrace(origin, rest - it, tracer) }
        ?.let { listOf(it) } ?: emptyList()
}

private fun MetadataEvent.toTrace(origin: TraceActorResolver, rest: List<MetadataEvent>, tracer: Tracer): Trace {
    val parentEvent = event as HttpEvent.Outgoing
    val uri = parentEvent.uri.path(parentEvent.xUriTemplate)
    val describeHeaders = emptyList<String>()
        .takeIf { it.isNotEmpty() }?.joinToString(prefix = "[", postfix = "]") ?: ""

    return RequestResponse(
        origin(this),
        uri.host,
        if (traces()?.parentSpanId == null) TraceActor(origin(this), Person) else TraceActor(origin(this), System),
        TraceActor(uri.host, System),
        parentEvent.method.name + " " + uri.path + " " + describeHeaders,
        parentEvent.status.toString(),
        rest
            .filter { it.traces() != null && traces()?.spanId == it.traces()?.parentSpanId }
            .filter { (event as HttpEvent).uri.host == origin(it) }
            .flatMap { tracer(it, rest - it, tracer) }
    )
}

