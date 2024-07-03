package ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.usecase.FetchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ui.model.MessageUiState
import ui.model.MessageUiState.MessageUiStateDefault
import ui.model.MessageUiState.MessageUiStateError
import ui.model.MessageUiState.MessageUiStateLoading
import ui.model.MessageUiState.MessageUiStateSuccess

class DemoViewModel : ViewModel(), KoinComponent {

    private val fetchUseCase: FetchUseCase by inject()

    private val _messageUiState: MutableStateFlow<MessageUiState> =
        MutableStateFlow(MessageUiStateDefault)
    val messageUiState: StateFlow<MessageUiState> = _messageUiState.asStateFlow()

    fun fetchMessage() {
        fetchUseCase
            .fetchMessage()
            .onStart {
                _messageUiState.value = MessageUiStateLoading
            }
            .onEach {
                _messageUiState.value = MessageUiStateSuccess(it.value)
            }
            .catch {
                _messageUiState.value = MessageUiStateError(
                    it.message ?: "An error occurred"
                )
            }
            .launchIn(viewModelScope)
    }

    fun fetchMessageSSE() {
        fetchUseCase
            .fetchMessageSSE()
            .onStart {
                _messageUiState.value = MessageUiStateLoading
            }
            .onEach { message ->
                _messageUiState.update {
                    if (it is MessageUiStateSuccess) it.merge(message)
                    else MessageUiStateSuccess(message.value)
                }
            }
            .onCompletion {
                _messageUiState.value = MessageUiStateError(
                    error = "SSE connection closed"
                )
            }
            .catch {
                _messageUiState.value = MessageUiStateError(
                    error = it.localizedMessage ?: "An error occurred"
                )
            }
            .launchIn(viewModelScope)
    }

    fun connectWebSocket() {
        fetchUseCase
            .connectWebSocket()
            .onStart {
                _messageUiState.value = MessageUiStateLoading
            }
            .onEach { message ->
                _messageUiState.update {
                    MessageUiStateSuccess(message.value)
                }
            }
            .onCompletion {
                _messageUiState.value = MessageUiStateError(
                    error = "WS connection closed"
                )
            }
            .catch {
                _messageUiState.value = MessageUiStateError(
                    error = it.localizedMessage ?: "An error occurred"
                )
            }
            .launchIn(viewModelScope)
    }

    fun sendWsMessage(percentage: Float) {
        viewModelScope.launch {
            fetchUseCase.sendWsMessage(percentage)
        }
    }
}
