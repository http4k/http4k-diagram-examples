package org.http4k.tracing

import org.http4k.events.HttpEvent
import org.http4k.events.MetadataEvent

object AppHttpTracer : Tracer<HttpCallTree> {
    override operator fun invoke(
        parent: MetadataEvent,
        rest: List<MetadataEvent>,
        tracer: Tracer<CallTree>
    ) = parent.takeIf { it.event is HttpEvent.Outgoing }
        ?.let { listOf(it.toTraceTree(rest - it, tracer)) } ?: emptyList()

    private fun MetadataEvent.toTraceTree(
        rest: List<MetadataEvent>,
        tracer: Tracer<CallTree>
    ): HttpCallTree {
        val parentEvent = event as HttpEvent.Outgoing
        return HttpCallTree(
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

