package org.example

import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.then
import org.http4k.events.Events
import org.http4k.events.MetadataEvent
import org.http4k.events.then
import org.http4k.hamkrest.hasBody
import org.http4k.tracing.*
import org.http4k.tracing.TraceStep.StopRendering
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class PingTest {

    // generates PlantUML diagrams in .generated/diagrams
    @RegisterExtension
    val events = TraceReportingEvents(AppName("sequence-diagram-example"))

    private val pingServer = pingApp()
    private val pingClient = AppOutgoingHttp(AppEvents(AppName("another-server")).then(events)).then(pingServer)
    private val indirectPingServer = indirectPingApp(pingClient)
    private val pingActor = UserAsActor(events, pingServer, indirectPingServer)

    @Test
    fun `Ping test`() {
        pingActor.ping()
    }

    @Test
    fun `Indirect ping test`() {
        pingActor.indirectPing()
    }

    @Test
    fun `Multiple interactions test`() {
        pingActor.multipleInteractions()
    }

    @Test
    fun `Ignored interactions`() {
        pingActor.ping()
        events(MetadataEvent(StopRendering))
        pingActor.multipleInteractions()
    }
}

class UserAsActor(evens: Events, ping: HttpHandler, indirectPing: HttpHandler) {
    private val name = "John Doe"
    private val pingHttp = TracedActorHttp(name, ping, evens)
    private val indirectPingHttp = TracedActorHttp(name, indirectPing, evens)

    fun ping() {
        assertThat(pingHttp(Request(GET, "http://a-server/ping")), hasBody("pong"))
    }

    fun indirectPing() {
        assertThat(indirectPingHttp(Request(GET, "http://another-server/indirect-ping")), hasBody("pong"))
    }

    fun multipleInteractions() {
        indirectPing()
        ping()
    }
}

