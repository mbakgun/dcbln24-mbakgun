package domain.usecase

import data.repository.Repository
import domain.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class FetchUseCase : KoinComponent {

    private val repository: Repository = get()

    fun fetchMessage(): Flow<Message> =
        repository
            .fetchMessage()
            .map { Message(value = it.message) }

    fun fetchMessageSSE(): Flow<Message> =
        repository
            .fetchMessageSSE()
            .filterNotNull()
            .map(::Message)

    fun connectWebSocket(): Flow<Message> =
        repository
            .connectWebSocket()
            .map(::Message)

    suspend fun sendWsMessage(percentage: Float) {
        repository.sendWsMessage(percentage.toInt())
    }
}
