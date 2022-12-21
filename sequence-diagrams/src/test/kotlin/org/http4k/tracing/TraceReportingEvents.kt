package org.http4k.tracing

import org.http4k.events.Event
import org.http4k.events.Events
import org.http4k.events.MetadataEvent
import org.http4k.events.then
import org.http4k.testing.RecordingEvents
import org.http4k.tracing.renderer.PumlSequenceDiagram
import org.http4k.tracing.renderer.TraceStepRenderer
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.File
import java.util.Locale

class TraceReportingEvents(
    private val app: AppName,
    private val testVariant: String? = null,
    private val dir: File = File(".generated/diagrams"),
    private val print: Boolean = false,
    private val renderers: List<TraceStepRenderer> = listOf(PumlSequenceDiagram)
) : Events, Iterable<Event>, AfterTestExecutionCallback {

    private val events = RecordingEvents()

    override fun afterTestExecution(context: ExtensionContext) {
        if (context.executionException.isEmpty) {
            val tracerBullet = TracerBullet(AppHttpTracer)(events.toList())

            val calls = tracerBullet.filterIsInstance<TraceStep>()

            renderers.forEach { it.writePuml(context.testMethod.get().name, calls) }
        }
    }

    private fun TraceStepRenderer.writePuml(scenarioName: String, calls: List<TraceStep>) {
        val appTitle = app.value.capitalize().replace('-', ' ')
        val fullTitle = appTitle + (testVariant?.let { " ($testVariant)" } ?: "")
        val render = render("$fullTitle: $scenarioName", calls)

        File(dir.apply { mkdirs() }, "${render.title}.puml").writeText(render.content)
    }

    override fun toString() = events.toString()

    override fun invoke(p1: Event) =
        when {
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

private fun String.capitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
