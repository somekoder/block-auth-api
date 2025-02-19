package com.somekoder.block.auth.api.infra.logging

import io.ktor.server.application.*

class DefaultLogger(
    private val env: ApplicationEnvironment
) : Logger {

    override fun debug(message: String) {
        env.log.debug(message)
    }

    override fun info(message: String) {
        env.log.info(message)
    }

    override fun warn(message: String) {
        env.log.warn(message)
    }

    override fun error(message: String) {
        env.log.error(message)
    }
}