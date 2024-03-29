package org.example

import org.http4k.core.then
import org.http4k.events.then
import org.http4k.tracing.junit.RecordingMode.Manual
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class Ping_ManualRecordingTest {

    // generates PlantUML diagrams in .generated/diagrams
    @RegisterExtension
    val events = CustomTracingEvents(recordingMode = Manual)

    private val pingServer = pingApp()
    private val pingClient = AppOutgoingHttp(AppEvents("another-server").then(events)).then(pingServer)
    private val indirectPingServer = indirectPingApp(pingClient)
    private val pingActor = UserAsActor(events, pingServer, indirectPingServer)

    @Test
    fun `Indirect ping test`() {
        events.record {
            pingActor.indirectPing()
        }
    }
}
