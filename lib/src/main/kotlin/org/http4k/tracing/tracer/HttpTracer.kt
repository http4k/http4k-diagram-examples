package org.http4k.tracing.tracer

import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.events.HttpEvent
import org.http4k.events.MetadataEvent
import org.http4k.tracing.OriginNamer
import org.http4k.tracing.RequestResponse
import org.http4k.tracing.Trace
import org.http4k.tracing.TraceActor
import org.http4k.tracing.Tracer
import org.http4k.tracing.traces

fun HttpTracer(origin: OriginNamer) = Tracer { parent, rest, tracer ->
    parent
        .takeIf { it.event is HttpEvent.Outgoing }
        ?.let { it.toTrace(origin, rest - it, tracer) }
        ?.let { listOf(it) } ?: emptyList()
}

private fun MetadataEvent.toTrace(
    origin: OriginNamer,
    rest: List<MetadataEvent>,
    tracer: Tracer): Trace {
    val parentEvent = event as HttpEvent.Outgoing
    return Trace.Http(
        origin(this),
        traces()?.parentSpanId == null,
        parentEvent.uri.path(parentEvent.xUriTemplate),
        parentEvent.method,
        parentEvent.status,
        rest
            .filter { it.traces() != null && traces()?.spanId == it.traces()?.parentSpanId }
            .filter { (event as HttpEvent).uri.host == origin(it) }
            .flatMap { tracer(it, rest - it, tracer) },
        emptyList()
    )
}

private fun Trace.Companion.Http(
    origin: String,
    originating: Boolean,
    uri: Uri,
    method: Method,
    status: Status,
    children: List<Trace>,
    headers: List<String> = emptyList()
): Trace {
    val describeHeaders = headers.takeIf { it.isNotEmpty() }?.joinToString(prefix = "[", postfix = "]") ?: ""
    return RequestResponse(
        origin,
        uri.host,
        if (originating) TraceActor.Person(origin) else TraceActor.Internal(origin),
        TraceActor.Internal(uri.host),
        method.name + " " + uri.path + " " + describeHeaders,
        status.toString(),
        children
    )
}
