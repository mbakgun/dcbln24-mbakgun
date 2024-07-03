package data.source.remote

import data.source.DataSource
import data.source.remote.model.MessageResponse
import data.source.remote.model.WSMessage
import kotlinx.coroutines.flow.Flow

class RemoteDataSource(
    private val service: Service,
) : DataSource.Remote {

    override suspend fun fetchMessage(): MessageResponse =
        service.fetchMessage()

    override fun fetchMessageSSE(): Flow<String?> =
        service.fetchMessageSSE()

    override fun connectWebSocket(
        outgoing: Flow<WSMessage>
    ): Flow<String> =
        service.connectWebSocket(outgoing)
}
