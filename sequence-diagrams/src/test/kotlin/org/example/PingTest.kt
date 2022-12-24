package org.example

import org.http4k.core.then
import org.http4k.events.MetadataEvent
import org.http4k.events.then
import org.http4k.tracing.StopRendering
import org.http4k.tracing.junit.RecordingMode.AUTO
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class PingTest {

    // generates PlantUML diagrams in .generated/diagrams
    @RegisterExtension
    val events = CustomTracingEvents("sequence-diagram-example", recordingMode = AUTO)

    private val pingServer = pingApp()
    private val pingClient = AppOutgoingHttp(AppEvents("another-server").then(events)).then(pingServer)
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

