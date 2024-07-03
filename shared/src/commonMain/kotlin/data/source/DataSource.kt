package data.source

import data.source.remote.model.MessageResponse
import data.source.remote.model.WSMessage
import kotlinx.coroutines.flow.Flow

interface DataSource {

    interface Remote {
        suspend fun fetchMessage(): MessageResponse
        fun fetchMessageSSE(): Flow<String?>
        fun connectWebSocket(outgoing: Flow<WSMessage>): Flow<String>
    }
}
