package org.http4k.tracing
import org.http4k.events.Event
import org.http4k.events.MetadataEvent
import org.http4k.filter.ZipkinTraces

/**
 * Entry--point for creating TraceTrees from a list of MetadataEvents. Provide a Tracer for each of the
 * implementations that you want to support.
 */
class TracerBullet(private vararg val tracers: Tracer<*>) {

    operator fun invoke(events: List<Event>): List<CallTree> {
        val metadataEvents = events.filterIsInstance<MetadataEvent>().removeUnrenderedEvents()

        return metadataEvents
            .filter { it.metadata["traces"] != null && it.traces().parentSpanId == null }
            .flatMap { event -> tracers.flatMap { it(event, metadataEvents - event, uberTracer) } }
    }

    private val uberTracer = object : Tracer<CallTree> {
        override operator fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer<CallTree>) =
            tracers.flatMap { it(parent, rest - parent, this) }
    }

    private enum class CollectEvents { collect, drop }

    private fun List<MetadataEvent>.removeUnrenderedEvents(): List<MetadataEvent> {
        fun List<MetadataEvent>.andNext(collectEvents: CollectEvents) =
            Pair(this, collectEvents)

        val collectElements =
            if (any { it.event == TraceStep.StartRendering }) CollectEvents.drop else CollectEvents.collect
        return fold(Pair(listOf<MetadataEvent>(), collectElements)) { acc, event ->
            when (acc.second) {
                CollectEvents.collect -> when (event.event) {
                    TraceStep.StopRendering -> acc.first.andNext(CollectEvents.drop)
                    else -> (acc.first + event).andNext(CollectEvents.collect)
                }
                CollectEvents.drop -> when (event.event) {
                    TraceStep.StartRendering -> acc.first.andNext(CollectEvents.collect)
                    else -> acc.first.andNext(CollectEvents.drop)
                }
            }
        }.first
    }

    private fun MetadataEvent.traces() = (metadata["traces"] as ZipkinTraces)

}
