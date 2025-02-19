package com.somekoder.block.auth.api.infra

import com.somekoder.block.auth.api.domain.model.RefreshToken
import java.time.Clock
import java.util.*

class RefreshTokenUtils(
    private val expiresInMillis: Long,
    private val clock: Clock = Clock.systemUTC(),
) {

    fun generateToken(): RefreshToken {
        val token = UUID.randomUUID().toString()
        val hash = Hashing.hash(token)
        val expiresAt = clock.instant().plusMillis(expiresInMillis).toEpochMilli()
        return RefreshToken(
            refreshToken = token,
            refreshTokenHash = hash,
            expiresAt = expiresAt,
            createdAt = clock.millis()
        )
    }

    fun verifyToken(token: String, hashedToken: String): Boolean {
        return Hashing.verify(token, hashedToken)
    }

    fun verifyTokenWithExpiration(token: String, hashedToken: String, expiresAt: Long): Boolean {
        val match = Hashing.verify(token, hashedToken)
        if (!match) return false
        if (clock.millis() > expiresAt) return false
        return true
    }
}