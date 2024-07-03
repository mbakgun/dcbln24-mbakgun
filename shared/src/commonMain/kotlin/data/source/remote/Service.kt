package data.source.remote

import LOOP_BACK_ADDRESS
import SERVER_PORT
import data.source.remote.model.MessageResponse
import data.source.remote.model.WSMessage
import io.ktor.client.call.body
import io.ktor.client.plugins.sse.sse
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Service : KtorApi() {

    suspend fun fetchMessage(): MessageResponse =
        client.get {
            apiRestUrl("message")
        }.body()

    fun fetchMessageSSE(): Flow<String?> = flow {
        serverSendEventsClient.sse(
            host = LOOP_BACK_ADDRESS,
            port = SERVER_PORT,
            path = "sse",
        ) {
            try {
                incoming.collect { message ->
                    emit(message.data)
                }
            } finally {
                cancel()
            }
        }
    }

    fun connectWebSocket(
        outgoing: Flow<WSMessage>
    ): Flow<String> = flow {
        webSocketClient.webSocket(
            host = LOOP_BACK_ADDRESS,
            port = SERVER_PORT,
            path = "ws"
        ) {
            launch { outgoing.collect { send(Frame.Text(Json.encodeToString(it))) } }
            try {
                incoming.consumeEach {
                    if (it is Frame.Text) emit(it.readText())
                }
            } finally {
                cancel()
            }
        }
    }
}
