package com.somekoder.block.auth.api.data.infra

import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.somekoder.block.auth.api.infra.TokenUtils
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.lang.Thread.sleep
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TokenUtilsTest {

    @Test
    fun testGenerateToken() {
        val util = TokenUtils(
            algorithm = Algorithm.HMAC256("secret"),
            audience = "audience",
            issuer = "issuer",
            expiresInMillis = 1000,
            clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.of("UTC"))
        )
        val token = util.generateToken(userId = UUID.randomUUID(), email = "email@test.com")
        assertEquals(1000, token.expiresAt)
        assertTrue { token.token.isNotBlank() }
    }

    @Test
    fun testVerifier() {
        val util = TokenUtils(
            algorithm = Algorithm.HMAC256("secret"),
            audience = "audience",
            issuer = "issuer",
            expiresInMillis = 1000,
            clock = Clock.systemUTC()
        )
        val token = util.generateToken(userId = UUID.randomUUID(), email = "email@test.com")
        assertDoesNotThrow { util.verifier().build().verify(token.token) }
    }

    @Test
    fun testVerifierWithExpired() {
        val util = TokenUtils(
            algorithm = Algorithm.HMAC256("secret"),
            audience = "audience",
            issuer = "issuer",
            expiresInMillis = 1000,
            clock = Clock.systemUTC()
        )
        val token = util.generateToken(userId = UUID.randomUUID(), email = "email@test.com")
        sleep(1000)
        assertThrows<TokenExpiredException> { util.verifier().build().verify(token.token) }
    }
}