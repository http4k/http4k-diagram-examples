package org.example.oauth

import org.http4k.core.*
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.security.OAuthProvider
import org.http4k.security.OAuthProviderConfig
import org.http4k.tracing.AppIncomingHttp


fun RelyingParty(client: HttpHandler): RoutingHttpHandler {

    val provider = OAuthProvider(
        OAuthProviderConfig(
            Uri.of("http://auth-server"),
            "/as/authorize",
            "/as/token",
            Credentials("", "")
        ),
        client,
        Uri.of("http://relying-party/rp/callback"),
        listOf("user"),
        InsecureCookieBasedOAuthPersistence("test")
    )

    return AppIncomingHttp()
        .then(Filter { next ->
            {
                next(it)
            }
        })
        .then(routes(
            provider.callbackEndpoint,
            "/a-protected-resource" bind Method.GET to provider.authFilter.then {
                Response(Status.OK).body("pong")
            }
        ))
}

