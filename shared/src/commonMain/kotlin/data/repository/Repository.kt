package data.repository

import data.source.DataSource
import data.source.remote.model.MessageResponse
import data.source.remote.model.WSMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class Repository : KoinComponent {

    private val remoteSource: DataSource.Remote = get()
    private val outgoingMessages = MutableSharedFlow<WSMessage>()

    fun fetchMessage(): Flow<MessageResponse> = flow {
        delay(500)
        emit(remoteSource.fetchMessage())
    }

    fun fetchMessageSSE(): Flow<String?> = remoteSource.fetchMessageSSE()

    fun connectWebSocket(): Flow<String> =
        remoteSource.connectWebSocket(outgoingMessages)

    suspend fun sendWsMessage(percentage: Int) {
        outgoingMessages.emit(WSMessage("WS Update %$percentage", percentage))
    }
}
