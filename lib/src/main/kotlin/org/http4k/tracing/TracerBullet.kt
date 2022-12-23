package org.http4k.tracing

import org.http4k.events.Event
import org.http4k.events.MetadataEvent
import org.http4k.tracing.CollectEvents.collect
import org.http4k.tracing.CollectEvents.drop

/**
 * Entry--point for creating Trace from a list of MetadataEvents. Provide a Tracer for each of the
 * implementations that you want to support.
 */
class TracerBullet(private val tracers: List<Tracer>) {
    operator fun invoke(events: List<Event>): List<Trace> {
        val metadataEvents = events.filterIsInstance<MetadataEvent>().removeUnrenderedEvents()
        val uberTracer = Tracer.TreeWalker(tracers)

        return metadataEvents
            .filter { it.traces()?.let { it.parentSpanId == null } ?: false }
            .flatMap { event -> tracers.flatMap { it(event, metadataEvents - event, uberTracer) } }
    }
}

private enum class CollectEvents { collect, drop }

private fun List<MetadataEvent>.removeUnrenderedEvents(): List<MetadataEvent> {
    fun List<MetadataEvent>.andNext(collectEvents: CollectEvents) = this to collectEvents

    val collectElements = if (any { it.event == StartRendering }) drop else collect

    return fold(Pair(listOf<MetadataEvent>(), collectElements)) { acc, event ->
        when (acc.second) {
            collect -> when (event.event) {
                StopRendering -> acc.first.andNext(drop)
                else -> (acc.first + event).andNext(collect)
            }

            drop -> when (event.event) {
                StartRendering -> acc.first.andNext(collect)
                else -> acc.first.andNext(drop)
            }
        }
    }.first
}
