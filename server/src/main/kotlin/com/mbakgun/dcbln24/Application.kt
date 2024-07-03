package com.mbakgun.dcbln24

import SERVER_PORT
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.sse.SSE
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import java.time.Duration

fun main() {
    embeddedServer(
        factory = CIO,
        port = SERVER_PORT,
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) { json() }
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(30)
        timeout = Duration.ofSeconds(60)
    }
    install(SSE)

    routing {
        routingREST()
        routingSSE()
        routingWS()
    }
}
