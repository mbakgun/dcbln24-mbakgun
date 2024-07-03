package data.source.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class WSMessage(
    val message: String,
    val yIndex: Int
)
