package org.example

import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.events.Events
import org.http4k.hamkrest.hasBody

class UserAsActor(evens: Events, ping: HttpHandler, indirectPing: HttpHandler) {
    private val name = "John Doe"
    private val pingHttp = TracedActorHttp(name, ping, evens)
    private val indirectPingHttp = TracedActorHttp(name, indirectPing, evens)

    fun ping() {
        assertThat(pingHttp(Request(Method.GET, "http://a-server/ping")), hasBody("pong"))
    }

    fun indirectPing() {
        assertThat(indirectPingHttp(Request(Method.GET, "http://another-server/indirect-ping")), hasBody("pong"))
    }

    fun multipleInteractions() {
        indirectPing()
        ping()
    }
}