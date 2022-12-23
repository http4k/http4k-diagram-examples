package org.http4k.tracing.junit

import org.http4k.events.Event
import org.http4k.events.Events
import org.http4k.events.MetadataEvent
import org.http4k.testing.RecordingEvents
import org.http4k.tracing.NamedTrace
import org.http4k.tracing.TracePersistence
import org.http4k.tracing.TraceRenderPersistence
import org.http4k.tracing.TraceRenderer
import org.http4k.tracing.Tracer
import org.http4k.tracing.TracerBullet
import org.http4k.tracing.capitalize
import org.http4k.tracing.persistence.InMemory
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

class TracedEvents(
    private val title: String,
    private val testVariant: String?,
    tracers: List<Tracer>,
    private val renderers: List<TraceRenderer>,
    private val persistence: TraceRenderPersistence,
    private val tracePersistence: TracePersistence = TracePersistence.InMemory()
) : Events, Iterable<Event>, AfterTestExecutionCallback {

    private val fullTitle = run {
        val baseTitle = title.capitalize().replace('-', ' ')
        baseTitle + (testVariant?.let { " ($testVariant)" } ?: "")
    }

    private val tracerBullet = TracerBullet(tracers)

    private val events = RecordingEvents()

    override fun afterTestExecution(context: ExtensionContext) {
        if (context.executionException.isEmpty) {
            val traces = tracerBullet(events.toList())

            tracePersistence.store(NamedTrace(context.testMethod.get().name, traces))
            renderers.forEach {
                persistence(it.render("$fullTitle: ${context.testMethod.get().name}", traces))
            }
        }
    }

    override fun toString() = events.toString()

    override fun invoke(p1: Event) = events(p1)

    override fun iterator() = events
        .map { if (it is MetadataEvent) it.event else it }
        .iterator()
}