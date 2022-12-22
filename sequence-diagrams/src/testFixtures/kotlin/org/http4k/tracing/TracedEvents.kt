package org.http4k.tracing

import org.http4k.events.Event
import org.http4k.events.Events
import org.http4k.events.MetadataEvent
import org.http4k.events.then
import org.http4k.testing.RecordingEvents
import org.http4k.tracing.renderer.TraceRenderer
import org.http4k.tracing.util.capitalize
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

class TracedEvents(
    private val app: AppName,
    private val testVariant: String? = null,
    private val persistence: TracePersistence,
    private val print: Boolean = false,
    private val tracerBullet: TracerBullet,
    private val renderers: List<TraceRenderer>
) : Events, Iterable<Event>, AfterTestExecutionCallback {

    private val events = RecordingEvents()

    override fun afterTestExecution(context: ExtensionContext) {
        if (context.executionException.isEmpty) {
            val traces = tracerBullet(events.toList()).filterIsInstance<TraceStep>()

            renderers.forEach { it.writePuml(context.testMethod.get().name, traces) }
        }
    }

    private fun TraceRenderer.writePuml(scenarioName: String, calls: List<TraceStep>) {
        val appTitle = app.value.capitalize().replace('-', ' ')
        val fullTitle = appTitle + (testVariant?.let { " ($testVariant)" } ?: "")
        val render = render("$fullTitle: $scenarioName", calls)
        persistence(render)

    }

    override fun toString() = events.toString()

    override fun invoke(p1: Event) = when {
        print -> events.then { println(it) }(p1)
        else -> events(p1)
    }

    override fun iterator() = events
        .map { if (it is MetadataEvent) it.event else it }
        .iterator()
}

interface SystemDescriptor {
    val name: String
}

data class AppName(val value: String) : SystemDescriptor {
    override val name: String = value
}
