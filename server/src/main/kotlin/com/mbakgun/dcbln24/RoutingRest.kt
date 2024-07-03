package com.mbakgun.dcbln24

import Greeting
import data.source.remote.model.MessageResponse
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.RootRoute
import io.ktor.server.routing.get

fun RootRoute.routingREST() {
    get("/") {
        call.respondText("KMP Ktor: ${Greeting().greet()}")
    }

    get("/message") {
        call.respond(MessageResponse("Hello from KMP Ktor REST API!"))
    }
}
