package org.example.oauth

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.base64Decoded
import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.lens.Header
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.AccessToken
import org.http4k.security.Nonce
import org.http4k.security.oauth.server.*
import org.http4k.security.oauth.server.accesstoken.AccessTokenRequestAuthentication
import org.http4k.security.oauth.server.accesstoken.AuthorizationCodeAccessTokenRequest
import org.http4k.security.openid.IdToken
import org.http4k.tracing.AppIncomingHttp
import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.*

fun AuthServer(): RoutingHttpHandler {

    val server = OAuthServer(
        tokenPath = "/token",
        authRequestTracking = SlightlyMoreSecureCookieBasedAuthRequestTracking(),
        authoriseRequestValidator = SimpleAuthoriseRequestValidator(InsecureClientValidator()),
        accessTokenRequestAuthentication = BasicAuthAccessTokenRequestAuthentication(),
        authorizationCodes = InsecureAuthorizationCodes(),
        accessTokens = InsecureAccessTokens(),
        idTokens = TestIdTokens(),
        clock = Clock.systemUTC(),
    )

    return AppIncomingHttp()
        .then(Filter { next ->
            {
                next(it)
            }
        })
        .then(routes(
            "as" bind routes(
                server.tokenRoute,
                "/jwks" bind Method.GET to { _: Request ->
                    Response(Status.OK)
                        .with(Header.CONTENT_TYPE of ContentType.APPLICATION_JSON)
                        .body(jwks)
                },
                "/userinfo" bind Method.GET to { _: Request -> Response(Status.OK).body("{}") },
                "/authorize" bind Method.GET to server.authenticationStart.then {
                    Response(Status.OK)
                        .body("""<html><body><form method="POST"><button type="submit" id="perform_login">Please authenticate</button></form></body></html>""")
                },
                "/authorize" bind Method.POST to server.authenticationComplete,
                "/" bind { _: Request -> Response(Status.OK).body("Authorisation server") }
            )
        ))
}


class TestIdTokens : IdTokens {
    override fun createForAccessToken(
        authorizationCodeDetails: AuthorizationCodeDetails,
        code: AuthorizationCode,
        accessToken: AccessToken
    ): IdToken = IdToken(
        idToken(
            authorizationCodeDetails.nonce ?: Nonce("ignored-for-access-token"),
            authorizationCodeDetails.clientId
        )
    )

    override fun createForAuthorization(
        request: Request,
        authRequest: AuthRequest,
        response: Response,
        nonce: Nonce?,
        code: AuthorizationCode
    ): IdToken = IdToken(idToken(nonce ?: Nonce("ignored-for-auth"), authRequest.client))

}

class InsecureClientValidator : ClientValidator {
    override fun validateClientId(request: Request, clientId: ClientId): Boolean = true

    override fun validateRedirection(request: Request, clientId: ClientId, redirectionUri: Uri): Boolean = true

    override fun validateScopes(request: Request, clientId: ClientId, scopes: List<String>): Boolean = true

    override fun validateCredentials(request: Request, clientId: ClientId, clientSecret: String): Boolean = true
}

class InsecureAuthorizationCodes : AuthorizationCodes {
    private val clock = Clock.systemUTC()
    private val codes = mutableMapOf<AuthorizationCode, AuthorizationCodeDetails>()

    override fun detailsFor(code: AuthorizationCode) =
        codes[code] ?: error("code not stored")

    override fun create(request: Request, authRequest: AuthRequest, response: Response) =
        Success(AuthorizationCode(UUID.randomUUID().toString()).also {
            codes[it] = AuthorizationCodeDetails(
                authRequest.client,
                authRequest.redirectUri!!,
                clock.instant().plus(1, ChronoUnit.DAYS),
                authRequest.state,
                authRequest.isOIDC(),
                nonce = authRequest.nonce
            )
        })
}

class InsecureAccessTokens : AccessTokens {
    override fun create(clientId: ClientId, tokenRequest: TokenRequest) =
        Failure(UnsupportedGrantType("client_credentials"))

    override fun create(
        clientId: ClientId,
        tokenRequest: AuthorizationCodeAccessTokenRequest,
        authorizationCode: AuthorizationCode
    ) =
        Success(AccessToken(UUID.randomUUID().toString()))
}

class BasicAuthAccessTokenRequestAuthentication : AccessTokenRequestAuthentication {
    override fun validateCredentials(
        request: Request,
        tokenRequest: TokenRequest
    ) =
        Success(
            Triple(
                request,
                request.basicAuthenticationCredentials()?.user?.let(::ClientId) ?: ClientId(""),
                tokenRequest
            )
        )

    private fun Request.basicAuthenticationCredentials(): Credentials? = header("Authorization")
        ?.trim()
        ?.takeIf { it.startsWith("Basic") }
        ?.substringAfter("Basic")
        ?.trim()
        ?.safeBase64Decoded()
        ?.toCredentials()

    private fun String.safeBase64Decoded(): String? = try {
        base64Decoded()
    } catch (e: IllegalArgumentException) {
        null
    }

    private fun String.toCredentials(): Credentials =
        split(":", ignoreCase = false, limit = 2)
            .let { Credentials(it.getOrElse(0) { "" }, it.getOrElse(1) { "" }) }

}

class SlightlyMoreSecureCookieBasedAuthRequestTracking : AuthRequestTracking {
    private val cookieName = "OauthFlowId"

    override fun trackAuthRequest(request: Request, authRequest: AuthRequest, response: Response): Response =
        response.cookie(Cookie(cookieName, authRequest.serialise()))

    override fun resolveAuthRequest(request: Request): AuthRequest? =
        request.cookie(cookieName)?.value
            ?.let { Request(Method.GET, Uri.of("dummy").query(it)) }?.authorizationRequest()

    private fun AuthRequest.serialise() = Request(Method.GET, "dummy")
        .with(OAuthServer.clientIdQueryParameter of client)
        .with(OAuthServer.redirectUriQueryParameter of redirectUri!!)
        .with(OAuthServer.scopesQueryParameter of scopes)
        .with(OAuthServer.state of state)
        .with(OAuthServer.responseType of responseType)
        .with(OAuthServer.nonce of nonce)
        .uri.query
}


