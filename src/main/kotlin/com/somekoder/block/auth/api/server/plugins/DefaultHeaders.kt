package com.somekoder.block.auth.api.server.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*
fun Application.configureDefaultHeaders() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }
}
