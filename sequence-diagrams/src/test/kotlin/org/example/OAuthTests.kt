package org.example

import org.example.oauth.AuthServer
import org.example.oauth.Browser
import org.example.oauth.RelyingParty
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.events.Events
import org.http4k.events.HttpEvent
import org.http4k.events.then
import org.http4k.filter.ClientFilters.RequestTracing
import org.http4k.filter.ResponseFilters.ReportHttpTransaction
import org.http4k.tracing.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class OAuthTests {

    // generates PlantUML diagrams in .generated/diagrams
    @RegisterExtension
    val events = TraceReportingEvents(AppName("oauth-examples"))

    private val authServer = AuthServer()
    private val relyingParty = RelyingParty(authServer)
    private val browser = Browser(relyingParty, authServer, events)
    private val user = UserActor(events, browser)

    @Test
    fun `Authorization code oauth2 flow`() {
        user(Request(GET, "https://browser/a-protected-resource").header("host", "relying-party"))
        println(events.joinToString("\n"))
    }
}

fun UserActor(evens: Events, browser: HttpHandler) = TracedActorHttp("John Doe", browser, evens)

fun proxiedOutbound(events: Events, client: HttpHandler, name: String) =
    Filter.NoOp
        .then(ReportHttpTransaction { AppEvents(AppName("browser")).then(events)(HttpEvent.Outgoing(it)) })
        .then(RequestTracing())
        .then(client)