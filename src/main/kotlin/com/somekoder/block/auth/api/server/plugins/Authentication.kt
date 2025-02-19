package com.somekoder.block.auth.api.server.plugins

import com.somekoder.block.auth.api.infra.TokenUtils
import com.somekoder.block.auth.api.infra.TokenUtils.Companion.CLAIM_EMAIL
import com.somekoder.block.auth.api.infra.TokenUtils.Companion.CLAIM_USERID
import com.somekoder.block.auth.api.server.util.DefaultConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject
import java.util.*

fun Application.configureAuthentication() {

    val tokenUtils by inject<TokenUtils>()
    val config by inject<DefaultConfig>()

    val tokenExpiresIn = config.jwtExpiresIn
    val refreshTokenExpiresIn = config.refreshExpiresIn
    val leewaySeconds = (refreshTokenExpiresIn - tokenExpiresIn) / 1000

    install(Authentication) {
        jwt("default") {
            verifier(tokenUtils.verifier().build())
            validate { createUserPrincipal(it) }
        }
        jwt("allow-expired") {
            verifier(
                tokenUtils
                    .verifier()
                    .acceptExpiresAt(leewaySeconds)
                    .build()
            )
            validate { createUserPrincipal(it) }
        }
    }
}

private fun createUserPrincipal(credential: JWTCredential): UserPrincipal? {
    val userId = credential.payload.getClaim(CLAIM_USERID).asString()
    val email = credential.payload.getClaim(CLAIM_EMAIL).asString()
    return if (userId != null && email != null) {
        UserPrincipal(UUID.fromString(userId), email)
    } else {
        null
    }
}

data class UserPrincipal(
    val id: UUID,
    val email: String
)