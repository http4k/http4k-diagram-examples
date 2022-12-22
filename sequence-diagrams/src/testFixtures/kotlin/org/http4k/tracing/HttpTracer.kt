package org.http4k.tracing

import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.events.HttpEvent
import org.http4k.events.MetadataEvent

object HttpTracer : Tracer {
    override operator fun invoke(
        parent: MetadataEvent,
        rest: List<MetadataEvent>,
        tracer: Tracer
    ) = parent
        .takeIf { it.event is HttpEvent.Outgoing }
        ?.let { listOf(it.toTrace(rest - it, tracer)) } ?: emptyList()

    private fun MetadataEvent.toTrace(rest: List<MetadataEvent>, tracer: Tracer): Trace {
        val parentEvent = event as HttpEvent.Outgoing
        return HttpTrace(
            app(),
            traces()?.parentSpanId == null,
            parentEvent.uri.path(parentEvent.xUriTemplate), parentEvent.method, parentEvent.status,
            rest
                .filter { it.traces() != null && traces()?.spanId == it.traces()?.parentSpanId }
                .filter { (event as HttpEvent).uri.host == it.app() }
                .flatMap { tracer(it, rest - it, tracer) },
            emptyList()
        )
    }
}

class HttpTrace(
    override val origin: String,
    originating: Boolean,
    uri: Uri,
    method: Method,
    status: Status,
    override val children: List<Trace>,
    private val headers: List<String> = emptyList()
) : Trace, TraceStep {
    override val target = uri.host
    override val request = method.name + " " + uri.path + " " + describeHeaders()
    override val response = status.toString()
    override val originActor = if (originating) TraceActor.Person(origin) else TraceActor.Internal(origin)
    override val targetActor = TraceActor.Internal(uri.host)

    private fun describeHeaders() = headers
        .takeIf { it.isNotEmpty() }?.joinToString(prefix = "[", postfix = "]") ?: ""
}