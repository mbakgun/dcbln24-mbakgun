package com.mbakgun.dcbln24

import data.source.remote.model.MessageResponse
import io.ktor.http.ContentType.Text.Html
import io.ktor.server.response.respondText
import io.ktor.server.routing.RootRoute
import io.ktor.server.routing.get
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun RootRoute.routingSSE() {
    sse("/sse") {
        while (true) {
            val messageResponse = MessageResponse("data: ${System.currentTimeMillis()}")
            send(ServerSentEvent(Json.encodeToString(messageResponse)))
            delay(1000)
        }
    }

    get("/sse-client") {
        call.respondText(
            """
        <html>
            <head>
                <title>SSE Client</title>
                <style>
                    body {
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        margin: 0;
                        background-color: #f0f0f0;
                    }
                    #sse {
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
                <div id="sse">SSE</div>
                ${List(100) { i ->
                val delay = i * 0.001
                """<div class="dot" style="animation-delay: ${delay}s;"></div>"""
            }.joinToString("\n")}
                <script>
                    const events = document.getElementById('events');
                    const eventSource = new EventSource('/sse');
                    eventSource.onmessage = (event) => {
                        const newElement = document.createElement('div');
                        newElement.textContent = event.data;
                        events.appendChild(newElement);
                    };
                </script>
            </body>
        </html>
        """.trimIndent(), contentType = Html
        )
    }
}
