package ui.model

import domain.model.Message

sealed class MessageUiState {
    data object MessageUiStateDefault : MessageUiState()
    data object MessageUiStateLoading : MessageUiState()
    data class MessageUiStateError(val error: String) : MessageUiState()

    data class MessageUiStateSuccess(
        private val messageList: List<String>
    ) : MessageUiState() {
        constructor(messageList: String) : this(listOf(messageList))

        fun merge(message: Message): MessageUiStateSuccess =
            MessageUiStateSuccess(messageList + message.value)

        val message: String
            get() = messageList.joinToString("\n")
    }
}
