package com.somekoder.block.auth.api.infra

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Verification
import com.somekoder.block.auth.api.domain.model.Token
import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.*

class TokenUtils(
    private val algorithm: Algorithm,
    private val issuer: String,
    private val audience: String,
    private val expiresInMillis: Long,
    private val clock: Clock = Clock.systemUTC()
) {

    fun verifier(): Verification {
        return JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .withIssuer(issuer)
    }

    fun generateToken(
        userId: UUID,
        email: String
    ): Token {
        val expiresAt = clock.instant().plus(expiresInMillis, ChronoUnit.MILLIS)
        val token = JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim(CLAIM_EMAIL, email)
            .withClaim(CLAIM_USERID, userId.toString())
            .withExpiresAt(expiresAt)
            .sign(algorithm)
        return Token(token, expiresAt.toEpochMilli())
    }

    companion object {
        const val CLAIM_USERID = "userId"
        const val CLAIM_EMAIL = "userId"
    }

}

