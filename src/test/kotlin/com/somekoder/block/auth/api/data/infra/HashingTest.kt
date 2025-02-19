package com.somekoder.block.auth.api.data.infra

import com.somekoder.block.auth.api.infra.Hashing
import org.junit.Assert.*
import org.junit.Test
import java.security.SecureRandom
import java.util.*

class HashingTest {

    @Test
    fun testHash() {
        val initial = "initial"
        val hashed = Hashing.hash(initial)
        assertNotEquals(hashed, initial)
    }

    @Test
    fun testVerify_GoodMatch() {
        val initial = "initial"
        val hashed = Hashing.hash(initial)
        val match = Hashing.verify(initial, hashed)
        assertTrue(match)
    }

    @Test
    fun testVerify_BadMatch() {
        val hashed = Hashing.hash("initial")
        val match = Hashing.verify("notinitial", hashed)
        assertFalse(match)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testThrowsWhenHashTooBig() {
        val secureRandom = SecureRandom()
        val byteArray = ByteArray(Hashing.MAX_BYTES + 1)
        secureRandom.nextBytes(byteArray)
        val string = Base64.getEncoder().encodeToString(byteArray).take(Hashing.MAX_BYTES + 1)
        Hashing.hash(string)
    }
}