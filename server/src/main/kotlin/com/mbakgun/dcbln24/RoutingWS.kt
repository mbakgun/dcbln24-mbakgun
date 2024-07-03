package com.mbakgun.dcbln24

import data.source.remote.model.MessageResponse
import io.ktor.http.ContentType.Text.Html
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

fun Route.routingWS() {
    val messageResponseFlow = MutableSharedFlow<MessageResponse>()
    val sharedFlow = messageResponseFlow.asSharedFlow()
    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Coroutine exception: ${exception.localizedMessage}")
    }

    webSocket("/ws") {
        send("You are connected to WebSocket!")

        val job = launch(exceptionHandler) {
            sharedFlow.collect { message ->
                send(message.message)
            }
        }

        runCatching {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val receivedText = frame.readText()
                    val messageResponse = MessageResponse(receivedText)
                    messageResponseFlow.emit(messageResponse)
                }
            }
        }.onFailure { exception ->
            println("WebSocket exception: ${exception.localizedMessage}")
        }.also {
            job.cancel()
        }
    }

    get("/ws-client") {
        call.respondText(
            """
        <html>
            <head>
                <title>WebSocket Client</title>
                <style>
                    body {
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        margin: 0;
                        background-color: #f0f0f0;
                    }
                    #ws {
                        position: absolute;
                        left: 48px;
                        top: 50%;
                        transform: translateY(-50%);
                        font-size: 24px;
                        font-weight: bold;
                        background-color: #ddd;
                        padding: 10px;
                        border-radius: 5px;
                    }
                    .dot {
                        position: absolute;
                        width: 10px;
                        height: 10px;
                        background-color: black;
                        border-radius: 50%;
                        animation: moveDot 2s infinite;
                    }
                    @keyframes moveDot {
                        0% { top: 50%; left: 48px; transform: translateY(-50%) rotate(45deg); }
                        100% { top: 0; left: 100%; transform: translateY(-50%) rotate(45deg); }
                    }
                </style>
            </head>
            <body>
                <div id="ws">WebSocket</div>
                ${
                List(100) { i ->
                    val delay = i * 0.001
                    """<div class="dot" id="dot-$i" style="animation-delay: ${delay}s;"></div>"""
                }.joinToString("\n")
            }
                <script>
                    const ws = new WebSocket("ws://localhost:8080/ws");
                    ws.onmessage = (event) => {
                        const data = JSON.parse(event.data);
                        const yIndex = data.yIndex;
                        const wsDiv = document.getElementById("ws");
                        wsDiv.style.top = yIndex + "%";
                        const dots = document.querySelectorAll(".dot");
                        dots.forEach(dot => {
                            dot.style.top = yIndex + "%";
                        });
                        const styleSheet = document.styleSheets[0];
                        const keyframes = `
                            @keyframes moveDot {
                                0% { left: 48px; transform: translateY(-50%) rotate(45deg); }
                                100% { top: 0; left: 100%; transform: translateY(-50%) rotate(45deg); }
                            }
                        `;
                        styleSheet.insertRule(keyframes, styleSheet.cssRules.length);
                        wsDiv.innerText = data.message;
                    };
                </script>
            </body>
        </html>
        """.trimIndent(), contentType = Html
        )
    }
}