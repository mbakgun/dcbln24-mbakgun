package data.source.remote

import LOOP_BACK_ADDRESS
import SERVER_PORT
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

abstract class KtorApi {
    val client = httpClient
    val serverSendEventsClient = SSEClient
    val webSocketClient = WSClient

    /**
     * Use this method for configuring the request url
     */
    fun HttpRequestBuilder.apiRestUrl(path: String) {
        url {
            host = LOOP_BACK_ADDRESS
            port = SERVER_PORT
            path(path)
        }
    }
}

private val jsonConfiguration
    get() = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        useAlternativeNames = false
    }

private val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(jsonConfiguration)
    }
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.ALL
    }
}

private val SSEClient = HttpClient {
    install(SSE) {
        showCommentEvents()
        showRetryEvents()
    }
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.ALL
    }
}

private val WSClient = HttpClient(CIO) {
    install(WebSockets) {
        pingInterval = 20_000
    }
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.ALL
    }
}
