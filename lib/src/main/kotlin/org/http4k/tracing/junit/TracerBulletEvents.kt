package org.http4k.tracing.junit

import org.http4k.events.Event
import org.http4k.events.Events
import org.http4k.events.MetadataEvent
import org.http4k.testing.RecordingEvents
import org.http4k.tracing.ScenarioTraces
import org.http4k.tracing.StartRendering
import org.http4k.tracing.StopRendering
import org.http4k.tracing.TracePersistence
import org.http4k.tracing.TraceRenderPersistence
import org.http4k.tracing.TraceRenderer
import org.http4k.tracing.Tracer
import org.http4k.tracing.TracerBullet
import org.http4k.tracing.junit.RecordingMode.AUTO
import org.http4k.tracing.junit.RecordingMode.MANUAL
import org.http4k.tracing.persistence.InMemory
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.Locale

/**
 * JUnit plugin which is also an Events implementation that generates Trace renderings and stores them.
 */
class TracerBulletEvents(
    private val traceTitle: String,
    private val testVariant: String?,
    tracers: List<Tracer>,
    private val renderers: List<TraceRenderer>,
    private val traceRenderPersistence: TraceRenderPersistence,
    private val tracePersistence: TracePersistence = TracePersistence.InMemory(),
    private val mode: RecordingMode = AUTO
) : Events, Iterable<Event>, AfterTestExecutionCallback {

    private val tracerBullet = TracerBullet(tracers)

    private val events = RecordingEvents().apply {
        if (mode == MANUAL) this(MetadataEvent(StopRendering))
    }

    override fun afterTestExecution(context: ExtensionContext) {
        if (context.executionException.isEmpty) {
            val scenarioName = "${
                traceTitle.capitalize().replace('-', ' ') + (testVariant?.let { " ($testVariant)" } ?: "")
            }: ${context.testMethod.get().name}"

            val traces = tracerBullet(events.toList())

            if(traces.isNotEmpty()) {
                tracePersistence.store(ScenarioTraces(scenarioName, traces))
                renderers.forEach { traceRenderPersistence(it.render(scenarioName, traces)) }
            }
        }
    }

    /**
     * Enable Trac rendering for just this block.
     */
    fun render(block: () -> Unit) {
        events(MetadataEvent(StartRendering))
        block()
        events(MetadataEvent(StopRendering))
    }

    override fun toString() = events.toString()

    override fun invoke(p1: Event) = events(p1)

    override fun iterator() = events
        .map { if (it is MetadataEvent) it.event else it }
        .iterator()
}

private fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }