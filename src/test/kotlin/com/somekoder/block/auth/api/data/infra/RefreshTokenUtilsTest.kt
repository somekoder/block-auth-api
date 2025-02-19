package com.somekoder.block.auth.api.data.infra

import com.somekoder.block.auth.api.infra.RefreshTokenUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.lang.Thread.sleep
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RefreshTokenUtilsTest {

    @Test
    fun testGenerateToken() {
        val util = RefreshTokenUtils(
            expiresInMillis = 1000,
            clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.of("UTC"))
        )
        val token = util.generateToken()
        assertEquals(1000, token.expiresAt)
        assertNotEquals(token.refreshToken, token.refreshTokenHash)
    }

    @Test
    fun testVerify_GoodMatch() {
        val util = RefreshTokenUtils(
            expiresInMillis = 1000,
            clock = Clock.systemUTC()
        )
        val token = util.generateToken()
        assertTrue { util.verifyToken(token.refreshToken, token.refreshTokenHash) }
    }

    @Test
    fun testVerify_BadMatch() {
        val util = RefreshTokenUtils(
            expiresInMillis = 1000,
            clock = Clock.systemUTC()
        )
        val token = util.generateToken()
        assertFalse { util.verifyToken("wrongToken", token.refreshTokenHash) }
    }

    @Test
    fun testVerifyWithExpiration_GoodMatchNotExpired() {
        val util = RefreshTokenUtils(
            expiresInMillis = 1000,
            clock = Clock.systemUTC()
        )
        val token = util.generateToken()
        assertTrue { util.verifyTokenWithExpiration(token.refreshToken, token.refreshTokenHash, token.expiresAt) }
    }

    @Test
    fun testVerifyWithExpiration_BadMatchNotExpired() {
        val util = RefreshTokenUtils(
            expiresInMillis = 1000,
            clock = Clock.systemUTC()
        )
        val token = util.generateToken()
        assertFalse { util.verifyTokenWithExpiration("notToken", token.refreshTokenHash, token.expiresAt) }
    }

    @Test
    fun testVerifyWithExpiration_GoodMatchButExpired() {
        val util = RefreshTokenUtils(
            expiresInMillis = 1000,
            clock = Clock.systemUTC()
        )
        val token = util.generateToken()
        sleep(1000)
        assertFalse { util.verifyTokenWithExpiration(token.refreshToken, token.refreshTokenHash, token.expiresAt) }
    }
}