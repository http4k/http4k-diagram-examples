package org.http4k.tracing

import org.http4k.events.MetadataEvent
import org.http4k.filter.ZipkinTraces

/**
 * Implement this to define custom Trace Event types - eg. writing to a database or sending a message
 */
interface Tracer<T : CallTree> {
    operator fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer<CallTree>): List<T>
}

fun MetadataEvent.app() = metadata["app"].toString()
fun MetadataEvent.traces() = (metadata["traces"] as? ZipkinTraces)
