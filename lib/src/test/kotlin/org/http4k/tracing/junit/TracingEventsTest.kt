package org.http4k.tracing.junit

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.events.Event
import org.http4k.events.EventFilters.AddZipkinTraces
import org.http4k.events.MetadataEvent
import org.http4k.events.then
import org.http4k.tracing.FireAndForget
import org.http4k.tracing.ScenarioTraces
import org.http4k.tracing.TraceActor
import org.http4k.tracing.TracePersistence
import org.http4k.tracing.TraceRender
import org.http4k.tracing.TraceRenderer
import org.http4k.tracing.TraceStep
import org.http4k.tracing.Tracer
import org.http4k.tracing.junit.RecordingMode.AUTO
import org.http4k.tracing.persistence.InMemory
import org.http4k.tracing.persistence.InMemoryTraceRenderPersistence
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import java.lang.reflect.Proxy
import java.util.Optional

class TracingEventsTest {
    private val traceRenderPersistence = InMemoryTraceRenderPersistence()
    private val tracePersistence = TracePersistence.InMemory()

    @Test
    fun `write traces`() {
        val events = tracingEvents(AUTO)

        val tracedEvents = AddZipkinTraces().then(events)

        val toSend = listOf(MyEvent, MyOtherEvent)
        toSend.forEach(tracedEvents)
        events.afterTestExecution(FakeEC())

        val title = "Title (variant): toString"
        val traces = toSend.map { toTrace(MetadataEvent(it)) }
        assertThat(
            traceRenderPersistence.toList(),
            equalTo(listOf(TraceRender(title, title, traces.toString())))
        )
        assertThat(
            tracePersistence.load().toList(),
            equalTo(listOf(ScenarioTraces(title, traces)))
        )
    }

    @Test
    fun `does not write empty traces`() {
        val events = tracingEvents(AUTO)
        events.afterTestExecution(FakeEC())

        assertThat(tracePersistence.load().toList().isEmpty(), equalTo(true))
        assertThat(traceRenderPersistence.toList().isEmpty(), equalTo(true))
    }

    private fun tracingEvents(recordingMode: RecordingMode) = TracingEvents(
        "title", "variant",
        listOf(MyTracer()),
        listOf(MyTraceRenderer()),
        traceRenderPersistence,
        tracePersistence,
        recordingMode
    )
}

private class MyTraceRenderer : TraceRenderer {
    override fun render(scenarioName: String, steps: List<TraceStep>): TraceRender =
        TraceRender(scenarioName, scenarioName, steps.toString())
}

private class MyTracer : Tracer {
    override fun invoke(parent: MetadataEvent, rest: List<MetadataEvent>, tracer: Tracer) = listOf(toTrace(parent))
}

object MyEvent : Event
object MyOtherEvent : Event

inline fun <reified T> proxy(): T = Proxy.newProxyInstance(
    T::class.java.classLoader,
    arrayOf(T::class.java)
) { _, m, _ -> TODO(m.name + " not implemented") } as T

private class FakeEC : ExtensionContext by proxy() {
    override fun getExecutionException() = Optional.empty<Throwable>()
    override fun getTestMethod() = Optional.of(String::class.java.getMethod("toString"))
}

private fun toTrace(parent: MetadataEvent): FireAndForget {
    val name = parent.event.javaClass.simpleName
    return FireAndForget(
        name, "target",
        TraceActor.Internal(name),
        TraceActor.Internal("target"),
        "req",
        emptyList()
    )
}