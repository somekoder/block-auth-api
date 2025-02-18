package com.somekoder.block.auth.api.server.util

import io.ktor.server.config.*

class DefaultConfig(
    config: ApplicationConfig
) {

    val jwtSecret = config.property("config.jwt.secret").getString()
    val jwtIssuer = config.property("config.jwt.issuer").getString()
    val jwtAudience = config.property("config.jwt.audience").getString()
    val jwtExpiresIn = config.property("config.jwt.expiresIn").getString().toLong()

    val refreshExpiresIn = config.property("config.refresh.expiresIn").getString().toLong()

    val emailRegexPattern = config.property("config.email.regex").getString()

    val passwordMinLength = config.property("config.password.minLength").getString().toInt()
    val passwordMaxLength = config.property("config.password.maxLength").getString().toInt()
    val passwordRequireNumber = config.property("config.password.requireNumber").getString().toBoolean()
    val passwordRequireSpecial = config.property("config.password.requireSpecial").getString().toBoolean()
    val passwordRequireLowercase = config.property("config.password.requireLowercase").getString().toBoolean()
    val passwordRequireUppercase = config.property("config.password.requireUppercase").getString().toBoolean()
    val passwordAllowedChars = DEFAULT_PASSWORD_ALLOWED_CHARS
    val passwordAllowedSpecialChars = DEFAULT_PASSWORD_ALLOWED_SPECIAL_CHARS

    val databaseUrl = config.property("config.database.url").getString()
    val databaseName = config.property("config.database.name").getString()
    val databasePassword = config.property("config.database.password").getString()
    val databaseUsername = config.property("config.database.user").getString()

    companion object {
        private val DEFAULT_PASSWORD_ALLOWED_SPECIAL_CHARS = setOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '=', '_', '+', ',', '<', '.', '>', '/', '?', ';', ':', '\'', '\"', '[', ']', '{', '}', '\\', '|', '`', '~')
        private val DEFAULT_PASSWORD_ALLOWED_CHARS = (('a'..'z') + ('A'..'Z') + ('0'..'9') + DEFAULT_PASSWORD_ALLOWED_SPECIAL_CHARS).toSet()
    }

}