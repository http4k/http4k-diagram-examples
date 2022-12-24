package org.http4k.tracing

import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.events.HttpEvent
import org.http4k.events.MetadataEvent

fun HttpTracer(origin: OriginNamer) = object : Tracer {
    override operator fun invoke(
        parent: MetadataEvent,
        rest: List<MetadataEvent>,
        tracer: Tracer
    ) = parent
        .takeIf { it.event is HttpEvent.Outgoing }
        ?.let { listOf(it.toTrace(rest - it, tracer)) } ?: emptyList()

    private fun MetadataEvent.toTrace(rest: List<MetadataEvent>, tracer: Tracer): Trace {
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
