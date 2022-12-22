package org.http4k.tracing

import org.http4k.events.MetadataEvent
import org.http4k.filter.ZipkinTraces

/**
 * Implement this to define custom Trace Event types - eg. writing to a database or sending a message
 */
fun interface Tracer {
    operator fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer): List<Trace>

    companion object {
        fun TreeWalker(tracers: List<Tracer>) = object : Tracer {
            override operator fun invoke(
                parent: MetadataEvent,
                rest: List<MetadataEvent>,
                tracer: Tracer
            ) = tracers.flatMap { it(parent, rest - parent, this) }
        }
    }
}

fun MetadataEvent.app() = metadata["app"].toString()
fun MetadataEvent.traces() = (metadata["traces"] as? ZipkinTraces)
